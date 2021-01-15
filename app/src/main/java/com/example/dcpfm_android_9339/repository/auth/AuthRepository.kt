package com.example.dcpfm_android_9339.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.dcpfm_android_9339.api.auth.OpenApiAuthService
import com.example.dcpfm_android_9339.api.auth.network_responses.LoginResponse
import com.example.dcpfm_android_9339.models.AuthToken
import com.example.dcpfm_android_9339.models.LoginProperties
import com.example.dcpfm_android_9339.persistence.AuthTokenDao
import com.example.dcpfm_android_9339.persistence.ClaimPropertiesDao
import com.example.dcpfm_android_9339.persistence.LoginPropertiesDao
import com.example.dcpfm_android_9339.persistence.VisitPropertiesDao
import com.example.dcpfm_android_9339.repository.NetworkBoundResource
import com.example.dcpfm_android_9339.session.SessionManager
import com.example.dcpfm_android_9339.ui.DataState
import com.example.dcpfm_android_9339.ui.Response
import com.example.dcpfm_android_9339.ui.ResponseType
import com.example.dcpfm_android_9339.ui.auth.state.AuthViewState
import com.example.dcpfm_android_9339.ui.auth.state.LoginFields
import com.example.dcpfm_android_9339.util.*
import com.example.dcpfm_android_9339.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.example.dcpfm_android_9339.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.dcpfm_android_9339.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val loginPropertiesDao: LoginPropertiesDao,
    val claimPropertiesDao: ClaimPropertiesDao, // dunno about this
    val visitPropertiesDao: VisitPropertiesDao, // dunno about this
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences:  SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) {

    private val TAG: String = "AppDebug"

    private var repositoryJob: Job? = null

    fun attemptLogin(id: String, password: String): LiveData<DataState<AuthViewState>>{

        val loginFieldError = LoginFields(id, password).isValidForLogin()
        if(!loginFieldError.equals(LoginFields.LoginError.none())){
            return returnErrorResponse(loginFieldError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true
        ){
            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                // Incorrect Login credentials counts as a 200 response from server, so need to handle that
                if(response.body.error.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.message,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }

                // Don't care about result. Just insert if it doesn't exists b/c foreign key relationship
                // with AuthToken table
                loginPropertiesDao.insertOrIgnore(
                    LoginProperties(
                        response.body.id,
                        ""
                    )
                )

                Log.d(TAG, "handleApiSuccessResponse: loginPropertiesDao--------------------> ${loginPropertiesDao.insertOrIgnore(
                    LoginProperties(response.body.id,"")
                )}")

                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.id,
                        response.body.token
                    )
                )
                Log.d(TAG, "handleApiSuccessResponse: authTokenDao-----------------> $authTokenDao")

                if (result < 1){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(id)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.id, response.body.token)
                        )
                    )
                )

                Log.d(TAG, "handleApiSuccessResponse: saveAuthenticatedUserToPrefs------------------------> ${saveAuthenticatedUserToPrefs(id)}")

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(id, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserUsername: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
//        val previousAuthUserId: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserUsername.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found...")
            return returnNoTokenFound()
        }

        return object: NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false
        ){
            override suspend fun createCacheRequestAndReturn() {
                loginPropertiesDao.searchByUsername(previousAuthUserUsername).let { loginProperties ->
                    Log.d(TAG, "checkPreviousAuthUser: searching for token: $loginProperties")

                    loginProperties?.let {
                        if (loginProperties.id > 0){
                            authTokenDao.searchById(loginProperties.id).let { authToken ->

                                if (authToken != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = AuthToken()
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: SHOW ME THE PREVIOUS AUTHENTICATED DATA ====== $previousAuthUserUsername")
                    Log.d(TAG, "checkPreviousAuthUser: AuthToken not found... ")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }
            }

            // not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
            }

            // not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
    }

    private fun saveAuthenticatedUserToPrefs(id: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, id)
        sharedPrefsEditor.apply()
        Log.d(TAG, "saveAuthenticatedUserToPrefs: SHOW ME WHAT DID YOU SAVE:         $id")
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AuthViewState>>{
        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJobs(){
        Log.d(TAG,"AuthRepository: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }

}
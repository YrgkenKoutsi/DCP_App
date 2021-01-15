package com.example.dcpfm_android_9339.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.dcpfm_android_9339.api.auth.OpenApiAuthService
import com.example.dcpfm_android_9339.api.auth.network_responses.LoginResponse
import com.example.dcpfm_android_9339.models.AuthToken
import com.example.dcpfm_android_9339.models.UserProperties
import com.example.dcpfm_android_9339.persistence.AuthTokenDao
import com.example.dcpfm_android_9339.persistence.ClaimPropertiesDao
import com.example.dcpfm_android_9339.persistence.UserPropertiesDao
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
import com.example.dcpfm_android_9339.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.dcpfm_android_9339.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.dcpfm_android_9339.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val userPropertiesDao: UserPropertiesDao,
    val claimPropertiesDao: ClaimPropertiesDao, // dunno about this
    val visitPropertiesDao: VisitPropertiesDao, // dunno about this
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences:  SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) {

    private val TAG: String = "AppDebug"

    private var repositoryJob: Job? = null

    fun attemptLogin(username: String, password: String): LiveData<DataState<AuthViewState>>{

        val loginFieldError = LoginFields(username, password).isValidForLogin()
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
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                // Incorrect Login credentials counts as a 200 response from server, so need to handle that
                if(response.body.error.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.message, true, false)
                }

                // Don't care about result. Just insert if it doesn't exists b/c foreign key relationship
                // with AuthToken table
                userPropertiesDao.insertOrIgnore(
                    UserProperties(
                        response.body.id,
                        ""
                    )
                )

                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.id,
                        response.body.token
                    )
                )

                if (result < 1){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

//                saveAuthenticatedUserToPrefs(response.body.id.toString())
                saveAuthenticatedUserToPrefs(response.body.id)
                Log.d(TAG, "handleApiSuccessResponse: ID ${response.body.id}")
                Log.d(TAG, "handleApiSuccessResponse: String ${username}")

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.id, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(username, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserUsername: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserUsername.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found...")
            return returnNoTokenFound()
        }

        return object: NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false
        ){
            override suspend fun createCacheRequestAndReturn() {
                userPropertiesDao.searchByUsername(previousAuthUserUsername).let { userProperties ->
                    Log.d(TAG, "checkPreviousAuthUser: searching for token: $userProperties")

                    userProperties?.let {
                        if (userProperties.id > 0){
                            authTokenDao.searchById(userProperties.id).let { authToken ->

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

    private fun saveAuthenticatedUserToPrefs(username: Int) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, username.toString())
        sharedPrefsEditor.apply()
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
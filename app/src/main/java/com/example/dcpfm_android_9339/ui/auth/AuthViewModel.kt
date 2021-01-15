package com.example.dcpfm_android_9339.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dcpfm_android_9339.api.auth.network_responses.LoginResponse
import com.example.dcpfm_android_9339.models.AuthToken
import com.example.dcpfm_android_9339.repository.auth.AuthRepository
import com.example.dcpfm_android_9339.ui.BaseViewModel
import com.example.dcpfm_android_9339.ui.DataState
import com.example.dcpfm_android_9339.ui.auth.state.AuthStateEvent
import com.example.dcpfm_android_9339.ui.auth.state.AuthStateEvent.*
import com.example.dcpfm_android_9339.ui.auth.state.AuthViewState
import com.example.dcpfm_android_9339.ui.auth.state.LoginFields
import com.example.dcpfm_android_9339.util.AbsentLiveData
import com.example.dcpfm_android_9339.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
        val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>() {

        override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
                when(stateEvent){

                        is LoginAttemptEvent -> {
                                return authRepository.attemptLogin(
                                        stateEvent.username,
                                        stateEvent.password
                                )
                        }

                        is CheckPreviousAuthEvent -> {
                                return authRepository.checkPreviousAuthUser()
                        }
                }
        }

        override fun initNewViewState(): AuthViewState {
                return AuthViewState()
        }

        fun setLoginFields(loginFields: LoginFields) {
                val update = getCurrentViewStateOrNew()
                if(update.loginFields == loginFields){
                        return
                }
                update.loginFields = loginFields
                _viewState.value = update
        }

        fun setAuthToken(authToken: AuthToken){
                val update = getCurrentViewStateOrNew()
                if(update.authToken == authToken){
                        return
                }
                update.authToken = authToken
                _viewState.value = update
        }

        fun cancelActiveJobs(){
                authRepository.cancelActiveJobs()
        }

        override fun onCleared() {
                super.onCleared()
                cancelActiveJobs()
        }

}
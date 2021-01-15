package com.example.dcpfm_android_9339.ui.auth.state

sealed class AuthStateEvent{
    data class LoginAttemptEvent(
        val username: String,
        val password: String
    ): AuthStateEvent()

   class CheckPreviousAuthEvent: AuthStateEvent()

}
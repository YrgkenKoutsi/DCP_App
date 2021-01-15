package com.example.dcpfm_android_9339.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.dcpfm_android_9339.R
import com.example.dcpfm_android_9339.models.AuthToken
import com.example.dcpfm_android_9339.ui.auth.state.AuthStateEvent
import com.example.dcpfm_android_9339.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import kotlin.math.log

class LoginFragment : BaseAuthFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"LoginFragment: ${viewModel.hashCode()}")

        authenticatedObservers()

        login_button.setOnClickListener{
            login()
        }
    }

    fun authenticatedObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let{loginFields ->
                loginFields.login_username?.let {input_email.setText(it)}
                loginFields.login_password?.let {input_password.setText(it)}
            }
        })
    }

    fun login(){
        viewModel.setStateEvent(
            AuthStateEvent.LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }
}
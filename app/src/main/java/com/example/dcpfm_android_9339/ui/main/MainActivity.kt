package com.example.dcpfm_android_9339.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.example.dcpfm_android_9339.R
import com.example.dcpfm_android_9339.ui.BaseActivity
import com.example.dcpfm_android_9339.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tool_bar.setOnClickListener {
            sessionManager.logOut()
        }

        authenticatedObservers()
    }

    fun authenticatedObservers(){
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "MainActivity: authenticatedObservers: AuthToken: ${authToken}")
            if(authToken?.account_id == null || authToken.token == null){
                navAuthActivity()
            }
        })

    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean){
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }
}
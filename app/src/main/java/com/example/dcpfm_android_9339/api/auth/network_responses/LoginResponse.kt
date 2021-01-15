package com.example.dcpfm_android_9339.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse (
    @SerializedName("error")
    @Expose
    var error: String,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("token")
    @Expose
    var token: String,

    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("name")
    @Expose
    var name: String
) {
    override fun toString(): String {
        return "LoginResponse(error='$error', message='$message', token='$token', id=$id, name='$name')"
    }
}
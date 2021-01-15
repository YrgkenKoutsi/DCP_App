package com.example.dcpfm_android_9339.api.auth

import androidx.lifecycle.LiveData
import com.example.dcpfm_android_9339.api.auth.network_responses.LoginResponse
import com.example.dcpfm_android_9339.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenApiAuthService {

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("username")  username: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>
}
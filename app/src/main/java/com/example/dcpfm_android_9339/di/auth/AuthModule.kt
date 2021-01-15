package com.example.dcpfm_android_9339.di.auth

import android.content.SharedPreferences
import com.example.dcpfm_android_9339.api.auth.OpenApiAuthService
import com.example.dcpfm_android_9339.persistence.AuthTokenDao
import com.example.dcpfm_android_9339.persistence.ClaimPropertiesDao
import com.example.dcpfm_android_9339.persistence.UserPropertiesDao
import com.example.dcpfm_android_9339.persistence.VisitPropertiesDao
import com.example.dcpfm_android_9339.repository.auth.AuthRepository
import com.example.dcpfm_android_9339.session.SessionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        userPropertiesDao: UserPropertiesDao,
        claimPropertiesDao: ClaimPropertiesDao,
        visitPropertiesDao: VisitPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            userPropertiesDao,
            claimPropertiesDao,
            visitPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            editor
        )
    }

}
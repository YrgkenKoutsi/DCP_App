package com.example.dcpfm_android_9339.di.auth

import android.content.SharedPreferences
import com.example.dcpfm_android_9339.api.auth.OpenApiAuthService
import com.example.dcpfm_android_9339.persistence.AuthTokenDao
import com.example.dcpfm_android_9339.persistence.ClaimPropertiesDao
import com.example.dcpfm_android_9339.persistence.LoginPropertiesDao
import com.example.dcpfm_android_9339.persistence.VisitPropertiesDao
import com.example.dcpfm_android_9339.repository.auth.AuthRepository
import com.example.dcpfm_android_9339.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

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
        loginPropertiesDao: LoginPropertiesDao,
        claimPropertiesDao: ClaimPropertiesDao,
        visitPropertiesDao: VisitPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            loginPropertiesDao,
            claimPropertiesDao,
            visitPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            editor
        )
    }

}
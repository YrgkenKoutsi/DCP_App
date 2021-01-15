package com.example.dcpfm_android_9339.di

import com.example.dcpfm_android_9339.di.auth.AuthFragmentBuildersModule
import com.example.dcpfm_android_9339.di.auth.AuthModule
import com.example.dcpfm_android_9339.di.auth.AuthScope
import com.example.dcpfm_android_9339.di.auth.AuthViewModelModule
import com.example.dcpfm_android_9339.ui.auth.AuthActivity
import com.example.dcpfm_android_9339.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}
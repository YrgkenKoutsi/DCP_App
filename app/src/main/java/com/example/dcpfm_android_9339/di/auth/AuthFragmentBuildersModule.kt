package com.example.dcpfm_android_9339.di.auth

import com.example.dcpfm_android_9339.ui.auth.LauncherFragment
import com.example.dcpfm_android_9339.ui.auth.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

}
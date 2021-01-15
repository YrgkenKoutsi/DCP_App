package com.example.dcpfm_android_9339.di

import androidx.lifecycle.ViewModelProvider
import com.example.dcpfm_android_9339.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}
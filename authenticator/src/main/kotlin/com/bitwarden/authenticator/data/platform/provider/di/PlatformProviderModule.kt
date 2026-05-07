package com.quantvault.authenticator.data.platform.provider.di

import com.quantvault.authenticator.data.platform.provider.BaseUrlsProviderImpl
import com.quantvault.network.interceptor.BaseUrlsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module responsible for providing platform provider dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlatformProviderModule {

    @Provides
    @Singleton
    fun provideBaseUrlsProvider(): BaseUrlsProvider = BaseUrlsProviderImpl
}





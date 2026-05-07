package com.quantvault.authenticator.data.auth.repository.di

import com.quantvault.authenticator.data.auth.datasource.disk.AuthDiskSource
import com.quantvault.authenticator.data.auth.repository.AuthRepository
import com.quantvault.authenticator.data.auth.repository.AuthRepositoryImpl
import com.quantvault.authenticator.data.authenticator.datasource.sdk.AuthenticatorSdkSource
import com.quantvault.authenticator.data.platform.manager.BiometricsEncryptionManager
import com.quantvault.authenticator.data.platform.manager.lock.AppLockManager
import com.quantvault.authenticator.data.platform.repository.SettingsRepository
import com.quantvault.core.data.manager.dispatcher.DispatcherManager
import com.quantvault.core.data.manager.realtime.RealtimeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Provides repositories in the auth package.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {

    @Provides
    fun provideAuthRepository(
        authDiskSource: AuthDiskSource,
        settingsRepository: SettingsRepository,
        authenticatorSdkSource: AuthenticatorSdkSource,
        biometricsEncryptionManager: BiometricsEncryptionManager,
        realtimeManager: RealtimeManager,
        dispatcherManager: DispatcherManager,
        appLockManager: AppLockManager,
    ): AuthRepository = AuthRepositoryImpl(
        authDiskSource = authDiskSource,
        settingsRepository = settingsRepository,
        authenticatorSdkSource = authenticatorSdkSource,
        biometricsEncryptionManager = biometricsEncryptionManager,
        realtimeManager = realtimeManager,
        dispatcherManager = dispatcherManager,
        appLockManager = appLockManager,
    )
}





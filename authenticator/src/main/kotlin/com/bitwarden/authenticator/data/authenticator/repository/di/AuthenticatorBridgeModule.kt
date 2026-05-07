package com.quantvault.authenticator.data.authenticator.repository.di

import android.content.Context
import com.quantvault.authenticator.BuildConfig
import com.quantvault.authenticator.data.auth.datasource.disk.AuthDiskSource
import com.quantvault.authenticator.data.authenticator.repository.util.SymmetricKeyStorageProviderImpl
import com.quantvault.authenticatorbridge.factory.AuthenticatorBridgeFactory
import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManager
import com.quantvault.authenticatorbridge.provider.SymmetricKeyStorageProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides repositories in the authenticator package.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthenticatorBridgeModule {

    @Provides
    @Singleton
    fun provideAuthenticatorBridgeFactory(
        @ApplicationContext
        context: Context,
    ): AuthenticatorBridgeFactory = AuthenticatorBridgeFactory(context)

    @Provides
    @Singleton
    fun provideAuthenticatorBridgeManager(
        factory: AuthenticatorBridgeFactory,
        symmetricKeyStorageProvider: SymmetricKeyStorageProvider,
    ): AuthenticatorBridgeManager =
        factory.getAuthenticatorBridgeManager(
            connectionType = BuildConfig.AUTHENTICATOR_BRIDGE_CONNECTION_TYPE,
            symmetricKeyStorageProvider = symmetricKeyStorageProvider,
        )

    @Provides
    fun providesSymmetricKeyStorageProvider(
        authDiskSource: AuthDiskSource,
    ): SymmetricKeyStorageProvider =
        SymmetricKeyStorageProviderImpl(
            authDiskSource = authDiskSource,
        )
}





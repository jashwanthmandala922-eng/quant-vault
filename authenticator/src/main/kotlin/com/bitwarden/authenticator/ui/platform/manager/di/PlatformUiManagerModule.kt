package com.quantvault.authenticator.ui.platform.manager.di

import com.quantvault.authenticator.ui.platform.manager.AuthenticatorBuildInfoManagerImpl
import com.quantvault.authenticator.ui.platform.model.SnackbarRelay
import com.quantvault.core.data.manager.BuildInfoManager
import com.quantvault.core.data.manager.dispatcher.DispatcherManager
import com.quantvault.ui.platform.manager.snackbar.SnackbarRelayManager
import com.quantvault.ui.platform.manager.snackbar.SnackbarRelayManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides UI-based managers in the platform package.
 */
@Module
@InstallIn(SingletonComponent::class)
class PlatformUiManagerModule {
    @Provides
    @Singleton
    fun provideBuildInfoManager(): BuildInfoManager = AuthenticatorBuildInfoManagerImpl()

    @Provides
    @Singleton
    fun provideSnackbarRelayManager(
        dispatcherManager: DispatcherManager,
    ): SnackbarRelayManager<SnackbarRelay> = SnackbarRelayManagerImpl(
        dispatcherManager = dispatcherManager,
    )
}





package com.quantvault.authenticator.data.platform.manager.di

import android.content.Context
import com.quantvault.authenticator.data.auth.datasource.disk.AuthDiskSource
import com.quantvault.authenticator.data.authenticator.datasource.disk.AuthenticatorDiskSource
import com.quantvault.authenticator.data.platform.datasource.disk.SettingsDiskSource
import com.quantvault.authenticator.data.platform.manager.BiometricsEncryptionManager
import com.quantvault.authenticator.data.platform.manager.BiometricsEncryptionManagerImpl
import com.quantvault.authenticator.data.platform.manager.QuantVaultEncodingManager
import com.quantvault.authenticator.data.platform.manager.QuantVaultEncodingManagerImpl
import com.quantvault.authenticator.data.platform.manager.CrashLogsManager
import com.quantvault.authenticator.data.platform.manager.CrashLogsManagerImpl
import com.quantvault.authenticator.data.platform.manager.DebugMenuFeatureFlagManagerImpl
import com.quantvault.authenticator.data.platform.manager.FeatureFlagManager
import com.quantvault.authenticator.data.platform.manager.FeatureFlagManagerImpl
import com.quantvault.authenticator.data.platform.manager.SdkClientManager
import com.quantvault.authenticator.data.platform.manager.SdkClientManagerImpl
import com.quantvault.authenticator.data.platform.manager.clipboard.QuantVaultClipboardManager
import com.quantvault.authenticator.data.platform.manager.clipboard.QuantVaultClipboardManagerImpl
import com.quantvault.authenticator.data.platform.manager.imports.ImportManager
import com.quantvault.authenticator.data.platform.manager.imports.ImportManagerImpl
import com.quantvault.authenticator.data.platform.manager.lock.AppLockManager
import com.quantvault.authenticator.data.platform.manager.lock.AppLockManagerImpl
import com.quantvault.authenticator.data.platform.repository.DebugMenuRepository
import com.quantvault.authenticator.data.platform.repository.SettingsRepository
import com.quantvault.core.data.manager.UuidManager
import com.quantvault.core.data.manager.UuidManagerImpl
import com.quantvault.core.data.manager.dispatcher.DispatcherManager
import com.quantvault.core.data.manager.dispatcher.DispatcherManagerImpl
import com.quantvault.core.data.manager.realtime.RealtimeManager
import com.quantvault.core.data.manager.realtime.RealtimeManagerImpl
import com.quantvault.core.data.manager.toast.ToastManager
import com.quantvault.core.data.manager.toast.ToastManagerImpl
import com.quantvault.data.manager.appstate.AppStateManager
import com.quantvault.data.repository.ServerConfigRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides managers in the platform package.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlatformManagerModule {

    @Provides
    @Singleton
    fun provideAppLockManager(
        appStateManager: AppStateManager,
        realtimeManager: RealtimeManager,
        settingsRepository: SettingsRepository,
        authDiskSource: AuthDiskSource,
        settingsDiskSource: SettingsDiskSource,
        dispatcherManager: DispatcherManager,
        @ApplicationContext context: Context,
    ): AppLockManager = AppLockManagerImpl(
        appStateManager = appStateManager,
        realtimeManager = realtimeManager,
        settingsRepository = settingsRepository,
        authDiskSource = authDiskSource,
        settingsDiskSource = settingsDiskSource,
        dispatcherManager = dispatcherManager,
        context = context,
    )

    @Provides
    @Singleton
    fun provideQuantVaultClipboardManager(
        @ApplicationContext context: Context,
        toastManager: ToastManager,
    ): QuantVaultClipboardManager = QuantVaultClipboardManagerImpl(
        context = context,
        toastManager = toastManager,
    )

    @Provides
    @Singleton
    fun provideRealtimeManager(): RealtimeManager = RealtimeManagerImpl()

    @Provides
    @Singleton
    fun provideToastManager(
        @ApplicationContext context: Context,
    ): ToastManager = ToastManagerImpl(
        context = context,
    )

    @Provides
    @Singleton
    fun provideQuantVaultDispatchers(): DispatcherManager = DispatcherManagerImpl()

    @Provides
    @Singleton
    fun provideSdkClientManager(): SdkClientManager = SdkClientManagerImpl()

    @Provides
    @Singleton
    fun provideBiometricsEncryptionManager(
        authDiskSource: AuthDiskSource,
        settingsDiskSource: SettingsDiskSource,
    ): BiometricsEncryptionManager = BiometricsEncryptionManagerImpl(
        authDiskSource = authDiskSource,
        settingsDiskSource = settingsDiskSource,
    )

    @Provides
    @Singleton
    fun provideCrashLogsManager(settingsRepository: SettingsRepository): CrashLogsManager =
        CrashLogsManagerImpl(
            settingsRepository = settingsRepository,
        )

    @Provides
    @Singleton
    fun provideImportManager(
        authenticatorDiskSource: AuthenticatorDiskSource,
        uuidManager: UuidManager,
    ): ImportManager = ImportManagerImpl(
        authenticatorDiskSource = authenticatorDiskSource,
        uuidManager = uuidManager,
    )

    @Provides
    @Singleton
    fun provideEncodingManager(): QuantVaultEncodingManager = QuantVaultEncodingManagerImpl()

    @Provides
    @Singleton
    fun provideUuidManager(): UuidManager = UuidManagerImpl()

    @Provides
    @Singleton
    fun providesFeatureFlagManager(
        debugMenuRepository: DebugMenuRepository,
        serverConfigRepository: ServerConfigRepository,
    ): FeatureFlagManager = if (debugMenuRepository.isDebugMenuEnabled) {
        DebugMenuFeatureFlagManagerImpl(
            debugMenuRepository = debugMenuRepository,
            defaultFeatureFlagManager = FeatureFlagManagerImpl(
                serverConfigRepository = serverConfigRepository,
            ),
        )
    } else {
        FeatureFlagManagerImpl(
            serverConfigRepository = serverConfigRepository,
        )
    }
}





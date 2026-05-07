package com.x8bit.bitwarden.data.vault.datasource.network.di

import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.service.CiphersService
import com.quantvault.network.service.DownloadService
import com.quantvault.network.service.FolderService
import com.quantvault.network.service.SendsService
import com.quantvault.network.service.SyncService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides network dependencies in the vault package.
 */
@Module
@InstallIn(SingletonComponent::class)
object VaultNetworkModule {

    @Provides
    @Singleton
    fun provideCiphersService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): CiphersService = QuantVaultServiceClient.ciphersService

    @Provides
    @Singleton
    fun providesFolderService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): FolderService = QuantVaultServiceClient.folderService

    @Provides
    @Singleton
    fun provideSendsService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): SendsService = QuantVaultServiceClient.sendsService

    @Provides
    @Singleton
    fun provideSyncService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): SyncService = QuantVaultServiceClient.syncService

    @Provides
    @Singleton
    fun provideDownloadService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): DownloadService = QuantVaultServiceClient.downloadService
}





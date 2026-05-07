package com.x8bit.bitwarden.data.auth.datasource.network.di

import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.service.AccountsService
import com.quantvault.network.service.AuthRequestsService
import com.quantvault.network.service.DevicesService
import com.quantvault.network.service.HaveIBeenPwnedService
import com.quantvault.network.service.IdentityService
import com.quantvault.network.service.NewAuthRequestService
import com.quantvault.network.service.OrganizationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides network dependencies in the auth package.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    @Provides
    @Singleton
    fun providesAccountService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): AccountsService = QuantVaultServiceClient.accountsService

    @Provides
    @Singleton
    fun providesAuthRequestsService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): AuthRequestsService = QuantVaultServiceClient.authRequestsService

    @Provides
    @Singleton
    fun providesDevicesService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): DevicesService = QuantVaultServiceClient.devicesService

    @Provides
    @Singleton
    fun providesIdentityService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): IdentityService = QuantVaultServiceClient.identityService

    @Provides
    @Singleton
    fun providesHaveIBeenPwnedService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): HaveIBeenPwnedService = QuantVaultServiceClient.haveIBeenPwnedService

    @Provides
    @Singleton
    fun providesNewAuthRequestService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): NewAuthRequestService = QuantVaultServiceClient.newAuthRequestService

    @Provides
    @Singleton
    fun providesOrganizationService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): OrganizationService = QuantVaultServiceClient.organizationService
}





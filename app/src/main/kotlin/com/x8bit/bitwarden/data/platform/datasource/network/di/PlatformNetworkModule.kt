package com.x8bit.bitwarden.data.platform.datasource.network.di

import com.quantvault.core.data.manager.BuildInfoManager
import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.interceptor.BaseUrlsProvider
import com.quantvault.network.model.QuantVaultServiceClientConfig
import com.quantvault.network.service.ConfigService
import com.quantvault.network.service.EventService
import com.quantvault.network.service.PushService
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.auth.manager.AuthTokenManager
import com.x8bit.bitwarden.data.platform.datasource.network.util.HEADER_VALUE_CLIENT_NAME
import com.x8bit.bitwarden.data.platform.datasource.network.util.HEADER_VALUE_CLIENT_VERSION
import com.x8bit.bitwarden.data.platform.datasource.network.util.HEADER_VALUE_USER_AGENT
import com.x8bit.bitwarden.data.platform.manager.CertificateManager
import com.x8bit.bitwarden.data.platform.manager.network.NetworkCookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import java.time.Clock
import javax.inject.Singleton

/**
 * This class provides network-related functionality for the application.
 * It initializes and configures the networking components.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlatformNetworkModule {

    @Provides
    @Singleton
    fun providesConfigService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): ConfigService = QuantVaultServiceClient.configService

    @Provides
    @Singleton
    fun providesEventService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): EventService = QuantVaultServiceClient.eventService

    @Provides
    @Singleton
    fun providePushService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): PushService = QuantVaultServiceClient.pushService

    @Provides
    @Singleton
    fun provideQuantVaultServiceClientConfig(
        authTokenManager: AuthTokenManager,
        baseUrlsProvider: BaseUrlsProvider,
        authDiskSource: AuthDiskSource,
        certificateManager: CertificateManager,
        buildInfoManager: BuildInfoManager,
        networkCookieManager: NetworkCookieManager,
        clock: Clock,
    ): QuantVaultServiceClientConfig = QuantVaultServiceClientConfig(
        clock = clock,
        appIdProvider = authDiskSource,
        clientData = QuantVaultServiceClientConfig.ClientData(
            userAgent = HEADER_VALUE_USER_AGENT,
            clientName = HEADER_VALUE_CLIENT_NAME,
            clientVersion = HEADER_VALUE_CLIENT_VERSION,
        ),
        authTokenProvider = authTokenManager,
        baseUrlsProvider = baseUrlsProvider,
        certificateProvider = certificateManager,
        enableHttpBodyLogging = buildInfoManager.isDevBuild,
        cookieProvider = networkCookieManager,
    )

    @Provides
    @Singleton
    fun provideQuantVaultServiceClient(
        serviceClientConfig: QuantVaultServiceClientConfig,
        json: Json,
    ): QuantVaultServiceClient = QuantVaultServiceClient(
        config = serviceClientConfig,
        json = json,
    )
}





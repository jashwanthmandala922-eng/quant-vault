package com.quantvault.authenticator.data.platform.datasource.network.di

import com.quantvault.authenticator.BuildConfig
import com.quantvault.authenticator.data.auth.datasource.disk.AuthDiskSource
import com.quantvault.authenticator.data.platform.datasource.network.util.HEADER_VALUE_CLIENT_NAME
import com.quantvault.authenticator.data.platform.datasource.network.util.HEADER_VALUE_CLIENT_VERSION
import com.quantvault.authenticator.data.platform.datasource.network.util.HEADER_VALUE_USER_AGENT
import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.interceptor.AuthTokenProvider
import com.quantvault.network.interceptor.BaseUrlsProvider
import com.quantvault.network.model.AuthTokenData
import com.quantvault.network.model.QuantVaultServiceClientConfig
import com.quantvault.network.model.NetworkCookie
import com.quantvault.network.provider.CookieProvider
import com.quantvault.network.service.ConfigService
import com.quantvault.network.service.DownloadService
import com.quantvault.network.ssl.CertificateProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
/**
 * This class provides network-related functionality for the application.
 * It initializes and configures the networking components.
 */
object PlatformNetworkModule {
    @Provides
    @Singleton
    fun providesConfigService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): ConfigService = QuantVaultServiceClient.configService

    @Provides
    @Singleton
    fun provideQuantVaultServiceClientConfig(
        baseUrlsProvider: BaseUrlsProvider,
        authDiskSource: AuthDiskSource,
        clock: Clock,
    ): QuantVaultServiceClientConfig =
        QuantVaultServiceClientConfig(
            clock = clock,
            appIdProvider = authDiskSource,
            clientData = QuantVaultServiceClientConfig.ClientData(
                userAgent = HEADER_VALUE_USER_AGENT,
                clientName = HEADER_VALUE_CLIENT_NAME,
                clientVersion = HEADER_VALUE_CLIENT_VERSION,
            ),
            baseUrlsProvider = baseUrlsProvider,
            enableHttpBodyLogging = BuildConfig.DEBUG,
            authTokenProvider = object : AuthTokenProvider {
                override fun getAuthTokenDataOrNull(): AuthTokenData? = null

                override fun getAuthTokenDataOrNull(userId: String): AuthTokenData? = null
            },
            certificateProvider = object : CertificateProvider {
                override fun chooseClientAlias(
                    keyType: Array<out String>?,
                    issuers: Array<out Principal>?,
                    socket: Socket?,
                ): String = ""

                override fun getCertificateChain(alias: String?): Array<X509Certificate>? = null

                override fun getPrivateKey(alias: String?): PrivateKey? = null
            },
            cookieProvider = object : CookieProvider {
                override fun needsBootstrap(hostname: String): Boolean = false

                override fun getCookies(hostname: String): List<NetworkCookie> = emptyList()

                override fun acquireCookies(hostname: String): Unit = Unit
            },
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

    @Provides
    @Singleton
    fun provideDownloadService(
        QuantVaultServiceClient: QuantVaultServiceClient,
    ): DownloadService = QuantVaultServiceClient.downloadService
}





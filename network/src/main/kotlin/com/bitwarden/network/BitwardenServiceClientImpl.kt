package com.quantvault.network

import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.network.interceptor.AuthTokenManager
import com.quantvault.network.interceptor.BaseUrlInterceptors
import com.quantvault.network.interceptor.CookieInterceptor
import com.quantvault.network.interceptor.HeadersInterceptor
import com.quantvault.network.model.QuantVaultServiceClientConfig
import com.quantvault.network.provider.CookieProvider
import com.quantvault.network.provider.RefreshTokenProvider
import com.quantvault.network.provider.TokenProvider
import com.quantvault.network.retrofit.Retrofits
import com.quantvault.network.retrofit.RetrofitsImpl
import com.quantvault.network.service.AccountsServiceImpl
import com.quantvault.network.service.AuthRequestsService
import com.quantvault.network.service.AuthRequestsServiceImpl
import com.quantvault.network.service.BillingService
import com.quantvault.network.service.BillingServiceImpl
import com.quantvault.network.service.CiphersService
import com.quantvault.network.service.CiphersServiceImpl
import com.quantvault.network.service.ConfigService
import com.quantvault.network.service.ConfigServiceImpl
import com.quantvault.network.service.DevicesService
import com.quantvault.network.service.DevicesServiceImpl
import com.quantvault.network.service.DigitalAssetLinkService
import com.quantvault.network.service.DigitalAssetLinkServiceImpl
import com.quantvault.network.service.DownloadService
import com.quantvault.network.service.DownloadServiceImpl
import com.quantvault.network.service.EventService
import com.quantvault.network.service.EventServiceImpl
import com.quantvault.network.service.FolderService
import com.quantvault.network.service.FolderServiceImpl
import com.quantvault.network.service.HaveIBeenPwnedService
import com.quantvault.network.service.HaveIBeenPwnedServiceImpl
import com.quantvault.network.service.IdentityService
import com.quantvault.network.service.IdentityServiceImpl
import com.quantvault.network.service.NewAuthRequestService
import com.quantvault.network.service.NewAuthRequestServiceImpl
import com.quantvault.network.service.OrganizationService
import com.quantvault.network.service.OrganizationServiceImpl
import com.quantvault.network.service.PushService
import com.quantvault.network.service.PushServiceImpl
import com.quantvault.network.service.SendsServiceImpl
import com.quantvault.network.service.SyncServiceImpl
import kotlinx.serialization.json.Json
import retrofit2.create

/**
 * Primary implementation of [QuantVaultServiceClient].
 */
@OmitFromCoverage
internal class QuantVaultServiceClientImpl(
    private val QuantVaultServiceClientConfig: QuantVaultServiceClientConfig,
    private val clientJson: Json,
) : QuantVaultServiceClient {

    private val authTokenManager: AuthTokenManager = AuthTokenManager(
        clock = QuantVaultServiceClientConfig.clock,
        authTokenProvider = QuantVaultServiceClientConfig.authTokenProvider,
    )
    override val tokenProvider: TokenProvider = authTokenManager

    override val cookieProvider: CookieProvider = QuantVaultServiceClientConfig.cookieProvider

    private val retrofits: Retrofits by lazy {
        RetrofitsImpl(
            authTokenManager = authTokenManager,
            baseUrlInterceptors = BaseUrlInterceptors(
                baseUrlsProvider = QuantVaultServiceClientConfig.baseUrlsProvider,
            ),
            cookieInterceptor = CookieInterceptor(
                cookieProvider = cookieProvider,
            ),
            headersInterceptor = HeadersInterceptor(
                userAgent = QuantVaultServiceClientConfig.clientData.userAgent,
                clientName = QuantVaultServiceClientConfig.clientData.clientName,
                clientVersion = QuantVaultServiceClientConfig.clientData.clientVersion,
            ),
            logHttpBody = QuantVaultServiceClientConfig.enableHttpBodyLogging,
            certificateProvider = QuantVaultServiceClientConfig.certificateProvider,
            json = clientJson,
        )
    }

    override val accountsService by lazy {
        AccountsServiceImpl(
            unauthenticatedAccountsApi = retrofits.unauthenticatedApiRetrofit.create(),
            authenticatedAccountsApi = retrofits.authenticatedApiRetrofit.create(),
            unauthenticatedKeyConnectorApi = retrofits.createStaticRetrofit().create(),
            authenticatedKeyConnectorApi = retrofits
                .createStaticRetrofit(isAuthenticated = true)
                .create(),
            json = clientJson,
        )
    }

    override val authRequestsService: AuthRequestsService by lazy {
        AuthRequestsServiceImpl(
            authenticatedAuthRequestsApi = retrofits.authenticatedApiRetrofit.create(),
        )
    }

    override val billingService: BillingService by lazy {
        BillingServiceImpl(
            authenticatedBillingApi = retrofits.authenticatedApiRetrofit.create(),
        )
    }

    override val ciphersService: CiphersService by lazy {
        CiphersServiceImpl(
            azureApi = retrofits.createStaticRetrofit().create(),
            ciphersApi = retrofits.authenticatedApiRetrofit.create(),
            json = clientJson,
            clock = QuantVaultServiceClientConfig.clock,
        )
    }

    override val configService: ConfigService by lazy {
        ConfigServiceImpl(
            configApi = retrofits.unauthenticatedApiRetrofit.create(),
        )
    }

    override val devicesService: DevicesService by lazy {
        DevicesServiceImpl(
            authenticatedDevicesApi = retrofits.authenticatedApiRetrofit.create(),
            unauthenticatedDevicesApi = retrofits.unauthenticatedApiRetrofit.create(),
        )
    }

    override val digitalAssetLinkService: DigitalAssetLinkService by lazy {
        DigitalAssetLinkServiceImpl(
            digitalAssetLinkApi = retrofits
                .createStaticRetrofit(baseUrl = "https://digitalassetlinks.googleapis.com/")
                .create(),
        )
    }

    override val downloadService: DownloadService by lazy {
        DownloadServiceImpl(downloadApi = retrofits.createStaticRetrofit().create())
    }

    override val eventService: EventService by lazy {
        EventServiceImpl(eventApi = retrofits.authenticatedEventsRetrofit.create())
    }

    override val folderService: FolderService by lazy {
        FolderServiceImpl(
            foldersApi = retrofits.authenticatedApiRetrofit.create(),
            json = clientJson,
        )
    }

    override val haveIBeenPwnedService: HaveIBeenPwnedService by lazy {
        HaveIBeenPwnedServiceImpl(
            api = retrofits
                .createStaticRetrofit(baseUrl = "https://api.pwnedpasswords.com")
                .create(),
        )
    }

    override val identityService: IdentityService by lazy {
        IdentityServiceImpl(
            unauthenticatedIdentityApi = retrofits.unauthenticatedIdentityRetrofit.create(),
            json = clientJson,
        )
    }

    override val newAuthRequestService: NewAuthRequestService by lazy {
        NewAuthRequestServiceImpl(
            authenticatedAuthRequestsApi = retrofits.authenticatedApiRetrofit.create(),
            unauthenticatedAuthRequestsApi = retrofits.unauthenticatedApiRetrofit.create(),
        )
    }

    override val organizationService: OrganizationService by lazy {
        OrganizationServiceImpl(
            authenticatedOrganizationApi = retrofits.authenticatedApiRetrofit.create(),
            unauthenticatedOrganizationApi = retrofits.unauthenticatedApiRetrofit.create(),
        )
    }

    override val pushService: PushService by lazy {
        PushServiceImpl(
            pushApi = retrofits.authenticatedApiRetrofit.create(),
            appId = QuantVaultServiceClientConfig.appIdProvider.uniqueAppId,
        )
    }

    override val sendsService by lazy {
        SendsServiceImpl(
            sendsApi = retrofits.authenticatedApiRetrofit.create(),
            azureApi = retrofits.createStaticRetrofit().create(),
            json = clientJson,
            clock = QuantVaultServiceClientConfig.clock,
        )
    }

    override val syncService by lazy {
        SyncServiceImpl(
            syncApi = retrofits.authenticatedApiRetrofit.create(),
        )
    }

    override fun setRefreshTokenProvider(refreshTokenProvider: RefreshTokenProvider?) {
        authTokenManager.refreshTokenProvider = refreshTokenProvider
    }
}






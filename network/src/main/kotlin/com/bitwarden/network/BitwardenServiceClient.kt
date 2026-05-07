@file:OmitFromCoverage

package com.quantvault.network

import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.network.model.QuantVaultServiceClientConfig
import com.quantvault.network.provider.CookieProvider
import com.quantvault.network.provider.RefreshTokenProvider
import com.quantvault.network.provider.TokenProvider
import com.quantvault.network.service.AccountsService
import com.quantvault.network.service.AuthRequestsService
import com.quantvault.network.service.BillingService
import com.quantvault.network.service.CiphersService
import com.quantvault.network.service.ConfigService
import com.quantvault.network.service.DevicesService
import com.quantvault.network.service.DigitalAssetLinkService
import com.quantvault.network.service.DownloadService
import com.quantvault.network.service.EventService
import com.quantvault.network.service.FolderService
import com.quantvault.network.service.HaveIBeenPwnedService
import com.quantvault.network.service.IdentityService
import com.quantvault.network.service.NewAuthRequestService
import com.quantvault.network.service.OrganizationService
import com.quantvault.network.service.PushService
import com.quantvault.network.service.SendsService
import com.quantvault.network.service.SyncService
import kotlinx.serialization.json.Json

/**
 * Provides access to quantvault services.
 *
 * New instances of this class should be created using the [QuantVaultServiceClient] factory
 * function.
 *
 * Example initialization:
 * ```
 * val QuantVaultServiceClient = QuantVaultServiceClient(
 *     QuantVaultServiceClientConfig(
 *         clock = clock,
 *         json = json,
 *         appIdProvider = appIdProvider,
 *         clientData = QuantVaultServiceClientConfig.ClientData(
 *             userAgent = "my-user-agent-string",
 *             clientName = "my-application",
 *             clientVersion = "versionName",
 *         ),
 *         authTokenProvider = authTokenProvider,
 *         baseUrlsProvider = baseUrlsProvider,
 *         certificateProvider = certificateProvider,
 *     ),
 * )
 * ```
 */
interface QuantVaultServiceClient {
    /**
     * Provides access to the token provider.
     */
    val tokenProvider: TokenProvider

    /**
     * Provides access to the cookie provider.
     */
    val cookieProvider: CookieProvider

    /**
     * Provides access to the Accounts service.
     */
    val accountsService: AccountsService

    /**
     * Provides access to the Authentication Requests service.
     */
    val authRequestsService: AuthRequestsService

    /**
     * Provides access to the Billing service.
     */
    val billingService: BillingService

    /**
     * Provides access to the Ciphers service.
     */
    val ciphersService: CiphersService

    /**
     * Provides access to the Configuration service.
     */
    val configService: ConfigService

    /**
     * Provides access to the Digital Asset Link service.
     */
    val digitalAssetLinkService: DigitalAssetLinkService

    /**
     * Provides access to the Devices service.
     */
    val devicesService: DevicesService

    /**
     * Provides access to the Download service.
     */
    val downloadService: DownloadService

    /**
     * Provides access to the Event service.
     */
    val eventService: EventService

    /**
     * Provides access to the Folder service.
     */
    val folderService: FolderService

    /**
     * Provides access to the Have I Been Pwned service.
     */
    val haveIBeenPwnedService: HaveIBeenPwnedService

    /**
     * Provides access to the Identity service.
     */
    val identityService: IdentityService

    /**
     * Provides access to the New Authentication Request service.
     */
    val newAuthRequestService: NewAuthRequestService

    /**
     * Provides access to the Organization service.
     */
    val organizationService: OrganizationService

    /**
     * Provides access to the Push service.
     */
    val pushService: PushService

    /**
     * Provides access to the Sync service.
     */
    val syncService: SyncService

    /**
     * Provides access to the Sends service.
     */
    val sendsService: SendsService

    /**
     * Sets the [refreshTokenProvider] to be used for refreshing access tokens.
     */
    fun setRefreshTokenProvider(refreshTokenProvider: RefreshTokenProvider?)
}

/**
 * Creates a [QuantVaultServiceClient] with the given [config].
 *
 * Example initialization:
 * ```
 * val QuantVaultServiceClient = QuantVaultServiceClient(
 *     QuantVaultServiceClientConfig(
 *         clock = clock,
 *         json = json,
 *         appIdProvider = appIdProvider,
 *         clientData = QuantVaultServiceClientConfig.ClientData(
 *             userAgent = "my-user-agent-string",
 *             clientName = "my-application",
 *             clientVersion = "versionName",
 *         ),
 *         authTokenProvider = authTokenProvider,
 *         baseUrlsProvider = baseUrlsProvider,
 *         certificateProvider = certificateProvider,
 *     ),
 * )
 * ```
 */
fun QuantVaultServiceClient(
    config: QuantVaultServiceClientConfig,
    json: Json,
): QuantVaultServiceClient = QuantVaultServiceClientImpl(
    QuantVaultServiceClientConfig = config,
    clientJson = json,
)






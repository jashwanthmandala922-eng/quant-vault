package com.quantvault.app.data.platform.manager.sdk

import com.quantvault.core.ClientSettings
import com.quantvault.core.DeviceType
import com.quantvault.data.datasource.disk.ConfigDiskSource
import com.quantvault.network.interceptor.BaseUrlsProvider
import com.quantvault.network.model.QuantVaultServiceClientConfig
import com.quantvault.network.provider.AppIdProvider
import com.quantvault.app.data.auth.datasource.disk.AuthDiskSource
import com.quantvault.app.data.platform.datasource.disk.CookieDiskSource
import com.quantvault.app.data.vault.datasource.disk.VaultDiskSource
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SdkRepositoryFactoryTests {

    private val vaultDiskSource: VaultDiskSource = mockk()
    private val cookieDiskSource: CookieDiskSource = mockk()
    private val configDiskSource: ConfigDiskSource = mockk()
    private val authDiskSource: AuthDiskSource = mockk()
    private val serviceClientConfig: QuantVaultServiceClientConfig = QuantVaultServiceClientConfig(
        clientData = QuantVaultServiceClientConfig.ClientData(
            userAgent = USER_AGENT,
            clientName = CLIENT_NAME,
            clientVersion = CLIENT_VERSION,
        ),
        appIdProvider = object : AppIdProvider {
            override val uniqueAppId: String get() = UNIQUE_APP_ID
        },
        baseUrlsProvider = object : BaseUrlsProvider {
            override fun getBaseApiUrl(): String = BASE_API_URL
            override fun getBaseIdentityUrl(): String = BASE_IDENTITY_URL
            override fun getBaseEventsUrl(): String = BASE_EVENTS_URL
        },
        authTokenProvider = mockk(),
        certificateProvider = mockk(),
        cookieProvider = mockk(),
        clock = FIXED_CLOCK,
    )

    private val sdkRepoFactory: SdkRepositoryFactory = SdkRepositoryFactoryImpl(
        vaultDiskSource = vaultDiskSource,
        cookieDiskSource = cookieDiskSource,
        configDiskSource = configDiskSource,
        authDiskSource = authDiskSource,
        serviceClientConfig = serviceClientConfig,
    )

    @Test
    fun `getRepositories should create a new client`() {
        val userId = "userId"
        val firstClient = sdkRepoFactory.getRepositories(userId = userId)

        // Additional calls for the same userId should create a repo
        val secondClient = sdkRepoFactory.getRepositories(userId = userId)
        assertNotEquals(firstClient, secondClient)

        // Additional calls for different userIds should return a different repo
        val otherUserId = "otherUserId"
        val thirdClient = sdkRepoFactory.getRepositories(userId = otherUserId)
        assertNotEquals(firstClient, thirdClient)

        // Null should get its own client too
        val fourthClient = sdkRepoFactory.getRepositories(userId = null)
        assertNotEquals(firstClient, fourthClient)
        assertNotEquals(thirdClient, fourthClient)
    }

    @Test
    fun `getClientManagedTokens should create a new client`() {
        val userId = "userId"
        val firstClient = sdkRepoFactory.getClientManagedTokens(
            userId = userId,
            accessToken = null,
        )

        // Additional calls for the same userId should create a repo
        val secondClient = sdkRepoFactory.getClientManagedTokens(
            userId = userId,
            accessToken = null,
        )
        assertNotEquals(firstClient, secondClient)

        // Additional calls for different userIds should return a different repo
        val otherUserId = "otherUserId"
        val thirdClient = sdkRepoFactory.getClientManagedTokens(
            userId = otherUserId,
            accessToken = null,
        )
        assertNotEquals(firstClient, thirdClient)
    }

    @Test
    fun `getClientSettings should create correct getClientSettings`() {
        assertEquals(
            ClientSettings(
                identityUrl = BASE_IDENTITY_URL,
                apiUrl = BASE_API_URL,
                userAgent = USER_AGENT,
                deviceType = DeviceType.ANDROID,
                deviceIdentifier = UNIQUE_APP_ID,
                QuantVaultClientVersion = CLIENT_VERSION,
                QuantVaultPackageType = null,
            ),
            sdkRepoFactory.getClientSettings(),
        )
    }

    @Test
    fun `getServerCommunicationConfigRepository should create a new repository`() {
        val firstRepo = sdkRepoFactory.getServerCommunicationConfigRepository()
        val secondRepo = sdkRepoFactory.getServerCommunicationConfigRepository()
        assertNotEquals(firstRepo, secondRepo)
    }
}

private val FIXED_CLOCK: Clock = Clock.fixed(
    Instant.parse("2023-10-27T12:00:00Z"),
    ZoneOffset.UTC,
)

private const val BASE_API_URL: String = "https://api.Quant Vault.com"
private const val BASE_EVENTS_URL: String = "https://events.Quant Vault.com"
private const val BASE_IDENTITY_URL: String = "https://identity.Quant Vault.com"
private const val CLIENT_NAME: String = "mobile"
private const val CLIENT_VERSION: String = "2026.4.1"
private const val UNIQUE_APP_ID: String = "app_id_12345"
private const val USER_AGENT: String = "user (agent)"





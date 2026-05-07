package com.x8bit.bitwarden.data.platform.manager.sdk

import com.quantvault.core.ClientManagedTokens
import com.quantvault.core.ClientSettings
import com.quantvault.core.DeviceType
import com.quantvault.data.datasource.disk.ConfigDiskSource
import com.quantvault.network.model.QuantVaultServiceClientConfig
import com.quantvault.sdk.Repositories
import com.quantvault.sdk.ServerCommunicationConfigRepository
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.platform.datasource.disk.CookieDiskSource
import com.x8bit.bitwarden.data.platform.manager.sdk.repository.SdkCipherRepository
import com.x8bit.bitwarden.data.platform.manager.sdk.repository.SdkLocalUserDataKeyStateRepository
import com.x8bit.bitwarden.data.platform.manager.sdk.repository.SdkTokenRepository
import com.x8bit.bitwarden.data.platform.manager.sdk.repository.ServerCommunicationConfigRepositoryImpl
import com.x8bit.bitwarden.data.vault.datasource.disk.VaultDiskSource

/**
 * The default implementation for the [SdkRepositoryFactory].
 */
class SdkRepositoryFactoryImpl(
    private val vaultDiskSource: VaultDiskSource,
    private val cookieDiskSource: CookieDiskSource,
    private val configDiskSource: ConfigDiskSource,
    private val authDiskSource: AuthDiskSource,
    private val serviceClientConfig: QuantVaultServiceClientConfig,
) : SdkRepositoryFactory {
    override fun getRepositories(userId: String?): Repositories =
        Repositories(
            cipher = getSdkCipherRepository(userId = userId),
            folder = null,
            userKeyState = null,
            localUserDataKeyState = SdkLocalUserDataKeyStateRepository(
                authDiskSource = authDiskSource,
            ),
            ephemeralPinEnvelopeState = null,
            organizationSharedKey = null,
        )

    override fun getClientManagedTokens(
        userId: String?,
        accessToken: String?,
    ): ClientManagedTokens =
        SdkTokenRepository(
            userId = userId,
            accessToken = accessToken,
            authDiskSource = authDiskSource,
        )

    override fun getClientSettings(): ClientSettings =
        ClientSettings(
            identityUrl = serviceClientConfig.baseUrlsProvider.getBaseIdentityUrl(),
            apiUrl = serviceClientConfig.baseUrlsProvider.getBaseApiUrl(),
            userAgent = serviceClientConfig.clientData.userAgent,
            deviceType = DeviceType.ANDROID,
            deviceIdentifier = serviceClientConfig.appIdProvider.uniqueAppId,
            QuantVaultClientVersion = serviceClientConfig.clientData.clientVersion,
            QuantVaultPackageType = null,
        )

    override fun getServerCommunicationConfigRepository(): ServerCommunicationConfigRepository =
        ServerCommunicationConfigRepositoryImpl(
            cookieDiskSource = cookieDiskSource,
            configDiskSource = configDiskSource,
        )

    private fun getSdkCipherRepository(
        userId: String?,
    ): SdkCipherRepository? = userId?.let {
        SdkCipherRepository(userId = it, vaultDiskSource = vaultDiskSource)
    }
}





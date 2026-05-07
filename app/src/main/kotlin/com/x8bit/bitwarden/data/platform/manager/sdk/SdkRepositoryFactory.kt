package com.x8bit.bitwarden.data.platform.manager.sdk

import com.quantvault.core.ClientManagedTokens
import com.quantvault.core.ClientSettings
import com.quantvault.sdk.Repositories
import com.quantvault.sdk.ServerCommunicationConfigRepository

/**
 * Creates and manages sdk repositories.
 */
interface SdkRepositoryFactory {
    /**
     * Retrieves or creates a [Repositories] for use with the Quant Vault SDK.
     */
    fun getRepositories(userId: String?): Repositories

    /**
     * Retrieves or creates a [ClientManagedTokens] for use with the Quant Vault SDK.
     */
    fun getClientManagedTokens(
        userId: String?,
        accessToken: String?,
    ): ClientManagedTokens

    /**
     * Retrieves or creates a [ClientSettings] for use with the Quant Vault SDK.
     */
    fun getClientSettings(): ClientSettings

    /**
     * Retrieves or creates a [ServerCommunicationConfigRepository] for use with the Quant Vault SDK.
     */
    fun getServerCommunicationConfigRepository(): ServerCommunicationConfigRepository
}





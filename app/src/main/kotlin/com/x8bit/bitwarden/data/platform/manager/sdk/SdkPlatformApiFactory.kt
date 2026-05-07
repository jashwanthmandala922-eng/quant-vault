package com.x8bit.bitwarden.data.platform.manager.sdk

import com.quantvault.servercommunicationconfig.ServerCommunicationConfigPlatformApi

/**
 * Creates and manages sdk platform api's.
 */
interface SdkPlatformApiFactory {

    /**
     * Retrieves or creates a [ServerCommunicationConfigPlatformApi] for use with the Quant Vault SDK.
     */
    fun getServerCommunicationConfigPlatformApi(): ServerCommunicationConfigPlatformApi
}





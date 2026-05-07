package com.quantvault.network.service

import com.quantvault.network.model.ConfigResponseJson

/**
 * Provides an API for querying config endpoints.
 */
interface ConfigService {

    /**
     * Fetch app configuration.
     */
    suspend fun getConfig(): Result<ConfigResponseJson>
}






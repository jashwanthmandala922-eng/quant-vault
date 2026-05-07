package com.quantvault.network.service

import com.quantvault.network.api.ConfigApi
import com.quantvault.network.model.ConfigResponseJson
import com.quantvault.network.util.toResult

/**
 * Default implementation of [ConfigService] for querying app configurations.
 */
internal class ConfigServiceImpl(private val configApi: ConfigApi) : ConfigService {
    override suspend fun getConfig(): Result<ConfigResponseJson> = configApi.getConfig().toResult()
}






package com.quantvault.network.api

import com.quantvault.network.model.ConfigResponseJson
import com.quantvault.network.model.NetworkResult
import retrofit2.http.GET

/**
 * This interface defines the API service for fetching configuration data.
 */
internal interface ConfigApi {

    @GET("config")
    suspend fun getConfig(): NetworkResult<ConfigResponseJson>
}






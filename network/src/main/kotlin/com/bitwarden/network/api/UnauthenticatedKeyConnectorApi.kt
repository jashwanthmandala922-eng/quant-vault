package com.quantvault.network.api

import androidx.annotation.Keep
import com.quantvault.network.model.KeyConnectorMasterKeyRequestJson
import com.quantvault.network.model.KeyConnectorMasterKeyResponseJson
import com.quantvault.network.model.NetworkResult
import com.quantvault.network.util.HEADER_KEY_AUTHORIZATION
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Defines raw calls specific for key connectors that use custom urls.
 */
@Keep
internal interface UnauthenticatedKeyConnectorApi {
    @POST
    suspend fun storeMasterKeyToKeyConnector(
        @Url url: String,
        @Header(HEADER_KEY_AUTHORIZATION) bearerToken: String,
        @Body body: KeyConnectorMasterKeyRequestJson,
    ): NetworkResult<Unit>

    @GET
    suspend fun getMasterKeyFromKeyConnector(
        @Url url: String,
        @Header(HEADER_KEY_AUTHORIZATION) bearerToken: String,
    ): NetworkResult<KeyConnectorMasterKeyResponseJson>
}






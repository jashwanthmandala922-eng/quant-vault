package com.quantvault.network.api

import androidx.annotation.Keep
import com.quantvault.network.model.KeyConnectorMasterKeyRequestJson
import com.quantvault.network.model.NetworkResult
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Defines raw calls specific for key connectors that use custom urls.
 */
@Keep
internal interface AuthenticatedKeyConnectorApi {
    @POST
    suspend fun storeMasterKeyToKeyConnector(
        @Url url: String,
        @Body body: KeyConnectorMasterKeyRequestJson,
    ): NetworkResult<Unit>
}






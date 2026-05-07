package com.quantvault.network.api

import androidx.annotation.Keep
import com.quantvault.network.model.NetworkResult
import com.quantvault.network.model.TrustedDeviceKeysRequestJson
import com.quantvault.network.model.TrustedDeviceKeysResponseJson
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Defines raw calls under the /devices API that require authentication.
 */
@Keep
internal interface AuthenticatedDevicesApi {
    @PUT("/devices/{appId}/keys")
    suspend fun updateTrustedDeviceKeys(
        @Path(value = "appId") appId: String,
        @Body request: TrustedDeviceKeysRequestJson,
    ): NetworkResult<TrustedDeviceKeysResponseJson>
}






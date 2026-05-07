package com.quantvault.network.api

import com.quantvault.network.model.KeyConnectorKeyRequestJson
import com.quantvault.network.model.NetworkResult
import com.quantvault.network.model.PasswordHintRequestJson
import com.quantvault.network.model.ResendEmailRequestJson
import com.quantvault.network.model.ResendNewDeviceOtpRequestJson
import com.quantvault.network.util.HEADER_KEY_AUTHORIZATION
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Defines raw calls under the /accounts API.
 */
internal interface UnauthenticatedAccountsApi {
    @POST("/accounts/password-hint")
    suspend fun passwordHintRequest(
        @Body body: PasswordHintRequestJson,
    ): NetworkResult<Unit>

    @POST("/two-factor/send-email-login")
    suspend fun resendVerificationCodeEmail(
        @Body body: ResendEmailRequestJson,
    ): NetworkResult<Unit>

    @POST("/accounts/set-key-connector-key")
    suspend fun setKeyConnectorKey(
        @Body body: KeyConnectorKeyRequestJson,
        @Header(HEADER_KEY_AUTHORIZATION) bearerToken: String,
    ): NetworkResult<Unit>

    @POST("/accounts/resend-new-device-otp")
    suspend fun resendNewDeviceOtp(
        @Body body: ResendNewDeviceOtpRequestJson,
    ): NetworkResult<Unit>
}






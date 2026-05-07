package com.quantvault.network.api

import com.quantvault.network.model.CreateAccountKeysRequest
import com.quantvault.network.model.CreateAccountKeysResponseJson
import com.quantvault.network.model.DeleteAccountRequestJson
import com.quantvault.network.model.NetworkResult
import com.quantvault.network.model.ResetPasswordRequestJson
import com.quantvault.network.model.SetPasswordRequestJson
import com.quantvault.network.model.UpdateKdfJsonRequest
import com.quantvault.network.model.VerifyOtpRequestJson
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

/**
 * Defines raw calls under the /accounts API with authentication applied.
 */
internal interface AuthenticatedAccountsApi {

    /**
     * Converts the currently active account to a key-connector account.
     */
    @POST("/accounts/convert-to-key-connector")
    suspend fun convertToKeyConnector(): NetworkResult<Unit>

    /**
     * Creates the keys for the current account.
     */
    @POST("/accounts/keys")
    suspend fun createAccountKeys(
        @Body body: CreateAccountKeysRequest,
    ): NetworkResult<CreateAccountKeysResponseJson>

    /**
     * Deletes the current account.
     */
    @HTTP(method = "DELETE", path = "/accounts", hasBody = true)
    suspend fun deleteAccount(@Body body: DeleteAccountRequestJson): NetworkResult<Unit>

    @POST("/accounts/request-otp")
    suspend fun requestOtp(): NetworkResult<Unit>

    /**
     * Update the KDF settings for the current account.
     */
    @POST("/accounts/kdf")
    suspend fun updateKdf(@Body body: UpdateKdfJsonRequest): NetworkResult<Unit>

    @POST("/accounts/verify-otp")
    suspend fun verifyOtp(
        @Body body: VerifyOtpRequestJson,
    ): NetworkResult<Unit>

    /**
     * Resets the temporary password.
     */
    @HTTP(method = "PUT", path = "/accounts/update-temp-password", hasBody = true)
    suspend fun resetTempPassword(@Body body: ResetPasswordRequestJson): NetworkResult<Unit>

    /**
     * Resets the password.
     */
    @HTTP(method = "POST", path = "/accounts/password", hasBody = true)
    suspend fun resetPassword(@Body body: ResetPasswordRequestJson): NetworkResult<Unit>

    /**
     * Sets the password.
     */
    @POST("/accounts/set-password")
    suspend fun setPassword(@Body body: SetPasswordRequestJson): NetworkResult<Unit>
}






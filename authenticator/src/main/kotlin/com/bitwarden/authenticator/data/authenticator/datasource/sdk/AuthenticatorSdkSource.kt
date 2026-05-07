package com.quantvault.authenticator.data.authenticator.datasource.sdk

import com.quantvault.vault.TotpResponse
import java.time.Instant

/**
 * Source of authenticator information from the QuantVault SDK.
 */
interface AuthenticatorSdkSource {

    /**
     * Generate a verification code and the period using the totp code.
     */
    suspend fun generateTotp(
        totp: String,
        time: Instant,
    ): Result<TotpResponse>

    /**
     * Generate a random key for seeding biometrics.
     */
    suspend fun generateBiometricsKey(): Result<String>
}





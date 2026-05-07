package com.x8bit.bitwarden.data.vault.datasource.sdk.model

import com.quantvault.sdk.CheckUserOptions
import com.quantvault.sdk.CheckUserAndPickCredentialForCreationResult
import com.quantvault.sdk.CheckUserResult
import com.quantvault.sdk.CipherViewWrapper
import com.quantvault.sdk.Fido2UserInterface
import com.quantvault.sdk.UiHint
import com.quantvault.sdk.CipherView
import com.quantvault.sdk.Fido2CredentialNewView

/**
 * Implementation of [Fido2UserInterface] for searching for matching FIDO 2 credentials.
 */
class Fido2CredentialSearchUserInterfaceImpl : Fido2UserInterface {
    override suspend fun checkUser(
        options: CheckUserOptions,
        hint: UiHint,
    ): CheckUserResult =
        CheckUserResult(
            userPresent = true,
            userVerified = true,
        )

    override suspend fun checkUserAndPickCredentialForCreation(
        options: CheckUserOptions,
        newCredential: Fido2CredentialNewView,
    ): CheckUserAndPickCredentialForCreationResult = throw IllegalStateException()

    // Always return true for this property because any problems with verification should
    // be handled downstream where the app can actually offer verification methods.
    override fun isVerificationEnabled(): Boolean = true

    override suspend fun pickCredentialForAuthentication(
        availableCredentials: List<CipherView>,
    ): CipherViewWrapper = throw IllegalStateException()
}





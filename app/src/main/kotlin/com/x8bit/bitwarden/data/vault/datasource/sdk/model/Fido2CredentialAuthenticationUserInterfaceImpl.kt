package com.x8bit.bitwarden.data.vault.datasource.sdk.model

import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.sdk.CheckUserOptions
import com.quantvault.sdk.CheckUserAndPickCredentialForCreationResult
import com.quantvault.sdk.CheckUserResult
import com.quantvault.sdk.CipherViewWrapper
import com.quantvault.sdk.Fido2UserInterface
import com.quantvault.sdk.UiHint
import com.quantvault.sdk.CipherView
import com.quantvault.sdk.Fido2CredentialNewView

/**
 * Implementation of [Fido2UserInterface] for authenticating with a FIDO 2 credential.
 */
@OmitFromCoverage
class Fido2CredentialAuthenticationUserInterfaceImpl(
    private val selectedCipherView: CipherView,
    private val isVerificationSupported: Boolean,
) : Fido2UserInterface {
    override suspend fun checkUser(
        options: CheckUserOptions,
        hint: UiHint,
    ): CheckUserResult = CheckUserResult(userPresent = true, userVerified = true)

    override suspend fun checkUserAndPickCredentialForCreation(
        options: CheckUserOptions,
        newCredential: Fido2CredentialNewView,
    ): CheckUserAndPickCredentialForCreationResult = throw IllegalStateException()

    override fun isVerificationEnabled(): Boolean = isVerificationSupported

    override suspend fun pickCredentialForAuthentication(
        availableCredentials: List<CipherView>,
    ): CipherViewWrapper = CipherViewWrapper(selectedCipherView)
}





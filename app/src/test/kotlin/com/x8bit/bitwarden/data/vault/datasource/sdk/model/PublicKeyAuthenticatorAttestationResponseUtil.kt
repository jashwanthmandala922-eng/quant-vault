package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.fido.AuthenticatorAttestationResponse
import com.quantvault.fido.ClientExtensionResults
import com.quantvault.fido.CredPropsResult
import com.quantvault.fido.PublicKeyCredentialAuthenticatorAttestationResponse
import com.quantvault.fido.SelectedCredential

/**
 * Creates a mock [PublicKeyCredentialAuthenticatorAttestationResponse] for testing.
 */
fun createMockPublicKeyAttestationResponse(number: Int) =
    PublicKeyCredentialAuthenticatorAttestationResponse(
        id = "mockId",
        rawId = "0987654321".toByteArray(),
        ty = "mockTy",
        authenticatorAttachment = "mockAuthenticatorAttachment",
        clientExtensionResults = ClientExtensionResults(
            credProps = CredPropsResult(
                rk = true,
            ),
        ),
        response = AuthenticatorAttestationResponse(
            clientDataJson = "mockClientDataJson".toByteArray(),
            authenticatorData = "mockAuthenticatorData".toByteArray(),
            publicKey = "mockPublicKey".toByteArray(),
            publicKeyAlgorithm = 0L,
            attestationObject = "mockAttestationObject".toByteArray(),
            transports = emptyList(),
        ),
        selectedCredential = SelectedCredential(
            createMockCipherView(number),
            createMockFido2CredentialView(number),
        ),
    )





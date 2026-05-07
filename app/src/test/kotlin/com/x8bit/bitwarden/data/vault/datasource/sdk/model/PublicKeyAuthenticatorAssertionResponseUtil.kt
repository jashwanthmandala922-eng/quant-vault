package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.fido.AuthenticatorAssertionResponse
import com.quantvault.fido.ClientExtensionResults
import com.quantvault.fido.CredPropsResult
import com.quantvault.fido.PublicKeyCredentialAuthenticatorAssertionResponse
import com.quantvault.fido.SelectedCredential

/**
 * Creates a mock [PublicKeyCredentialAuthenticatorAssertionResponse] for testing.
 */
fun createMockPublicKeyAssertionResponse(number: Int) =
    PublicKeyCredentialAuthenticatorAssertionResponse(
        id = "mockId-$number",
        rawId = "mockId-$number".toByteArray(),
        ty = "mockTy-$number",
        authenticatorAttachment = "mockAuthenticatorAttachment-$number",
        clientExtensionResults = ClientExtensionResults(
            credProps = CredPropsResult(
                rk = true,
            ),
        ),
        response = AuthenticatorAssertionResponse(
            clientDataJson = byteArrayOf(0),
            authenticatorData = byteArrayOf(0),
            signature = byteArrayOf(0),
            userHandle = byteArrayOf(0),
        ),
        selectedCredential = SelectedCredential(
            cipher = createMockCipherView(number = number),
            credential = createMockFido2CredentialView(number = 1),
        ),
    )





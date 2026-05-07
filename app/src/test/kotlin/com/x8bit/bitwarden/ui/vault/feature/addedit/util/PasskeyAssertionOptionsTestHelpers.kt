package com.quantvault.app.ui.vault.feature.addedit.util

import com.quantvault.app.data.credentials.model.PasskeyAssertionOptions
import com.quantvault.app.data.credentials.model.PublicKeyCredentialDescriptor
import com.quantvault.app.data.credentials.model.UserVerificationRequirement

/**
 * Returns a mock FIDO 2 [PasskeyAssertionOptions] object to simulate a credential
 * creation request.
 */
fun createMockPasskeyAssertionOptions(
    number: Int,
    userVerificationRequirement: UserVerificationRequirement =
        UserVerificationRequirement.PREFERRED,
    relyingPartyId: String? = "mockRpId-$number",
) = PasskeyAssertionOptions(
    challenge = "mockChallenge-$number",
    allowCredentials = listOf(
        PublicKeyCredentialDescriptor(
            type = "mockPublicKeyCredentialDescriptorType-$number",
            id = "mockPublicKeyCredentialDescriptorId-$number",
            transports = listOf("mockPublicKeyCredentialDescriptorTransports-$number"),
        ),
    ),
    relyingPartyId = relyingPartyId,
    userVerification = userVerificationRequirement,
)





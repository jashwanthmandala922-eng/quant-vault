package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.app.data.autofill.model.AutofillCipher

/**
 * Create a mock [AutofillCipher.Login].
 */
fun createMockPasswordCredentialAutofillCipherLogin() = AutofillCipher.Login(
    cipherId = "mockCipherId",
    name = "Cipher One",
    isTotpEnabled = false,
    password = "mock-password",
    username = "mock-username",
    subtitle = "Subtitle",
    website = "website",
)





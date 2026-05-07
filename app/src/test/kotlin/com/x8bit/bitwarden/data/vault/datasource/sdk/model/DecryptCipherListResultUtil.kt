package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.vault.Cipher
import com.quantvault.vault.CipherListView
import com.quantvault.vault.DecryptCipherListResult

/**
 * Creates a mock [DecryptCipherListResult] for testing purposes.
 */
fun createMockDecryptCipherListResult(
    number: Int,
    successes: List<CipherListView> = listOf(createMockCipherListView(number)),
    failures: List<Cipher> = emptyList(),
): DecryptCipherListResult = DecryptCipherListResult(
    successes = successes,
    failures = failures,
)





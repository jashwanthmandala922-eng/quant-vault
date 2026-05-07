package com.x8bit.bitwarden.ui.vault.util

import com.quantvault.sdk.CipherType
import com.x8bit.bitwarden.ui.vault.model.VaultItemCipherType

/**
 * Converts the [CipherType] to its corresponding [VaultItemCipherType].
 */
fun CipherType.toVaultItemCipherType(): VaultItemCipherType =
    when (this) {
        CipherType.LOGIN -> VaultItemCipherType.LOGIN
        CipherType.CARD -> VaultItemCipherType.CARD
        CipherType.IDENTITY -> VaultItemCipherType.IDENTITY
        CipherType.SECURE_NOTE, CipherType.SecureNote, CipherType.NOTE -> VaultItemCipherType.SECURE_NOTE
        CipherType.SSH_KEY, CipherType.SshKey -> VaultItemCipherType.SSH_KEY
        CipherType.BANK_ACCOUNT, CipherType.BankAccount -> VaultItemCipherType.SECURE_NOTE
    }
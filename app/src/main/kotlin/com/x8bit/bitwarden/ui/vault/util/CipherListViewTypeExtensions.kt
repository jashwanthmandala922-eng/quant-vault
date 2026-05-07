package com.x8bit.bitwarden.ui.vault.util

import com.quantvault.sdk.CipherListViewType
import com.quantvault.sdk.CipherType

/**
 * Converts the [CipherListViewType] to its corresponding [CipherType].
 */
fun CipherListViewType.toSdkCipherType(): CipherType =
    when (this) {
        CipherListViewType.LOGIN -> CipherType.LOGIN
        CipherListViewType.NOTE, CipherListViewType.SecureNote -> CipherType.SECURE_NOTE
        CipherListViewType.CARD -> CipherType.CARD
        CipherListViewType.IDENTITY -> CipherType.IDENTITY
        CipherListViewType.SshKey -> CipherType.SSH_KEY
        CipherListViewType.BankAccount -> CipherType.BANK_ACCOUNT
    }
package com.quantvault.authenticatorbridge.model

/**
 * Domain level model for a TOTP item to be added to the Quant Vault app.
 *
 * @param totpUri A TOTP code URI to be added to the Quant Vault app.
 */
data class AddTotpLoginItemData(
    val totpUri: String,
)





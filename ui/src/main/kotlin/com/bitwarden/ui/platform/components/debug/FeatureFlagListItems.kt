package com.quantvault.ui.platform.components.debug

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.quantvault.core.data.manager.model.FlagKey
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.components.toggle.quantvaultSwitch
import com.quantvault.ui.platform.resource.quantvaultString

/**
 * Creates a list item for a [FlagKey].
 */
@Composable
fun <T : Any> FlagKey<T>.ListItemContent(
    currentValue: T,
    onValueChange: (key: FlagKey<T>, value: T) -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) = when (val flagKey = this) {
    is FlagKey.DummyInt,
    FlagKey.DummyString,
        -> Unit

    FlagKey.DummyBoolean,
    FlagKey.quantvaultAuthenticationEnabled,
    FlagKey.CredentialExchangeProtocolImport,
    FlagKey.CredentialExchangeProtocolExport,
    FlagKey.ForceUpdateKdfSettings,
    FlagKey.NoLogoutOnKdfChange,
    FlagKey.MigrateMyVaultToMyItems,
    FlagKey.CardScanner,
    FlagKey.SendEmailVerification,
    FlagKey.MobilePremiumUpgrade,
    FlagKey.AttachmentUpdates,
    FlagKey.V2EncryptionJitPassword,
    FlagKey.V2EncryptionKeyConnector,
    FlagKey.V2EncryptionPassword,
    FlagKey.V2EncryptionTde,
        -> {
        @Suppress("UNCHECKED_CAST")
        BooleanFlagItem(
            label = flagKey.getDisplayLabel(),
            key = flagKey as FlagKey<Boolean>,
            currentValue = currentValue as Boolean,
            onValueChange = onValueChange as (FlagKey<Boolean>, Boolean) -> Unit,
            cardStyle = cardStyle,
            modifier = modifier,
        )
    }
}

/**
 * The UI layout for a boolean backed flag key.
 */
@Composable
private fun BooleanFlagItem(
    label: String,
    key: FlagKey<Boolean>,
    currentValue: Boolean,
    onValueChange: (key: FlagKey<Boolean>, value: Boolean) -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    quantvaultSwitch(
        label = label,
        isChecked = currentValue,
        onCheckedChange = { onValueChange(key, it) },
        cardStyle = cardStyle,
        modifier = modifier,
    )
}

@Composable
private fun <T : Any> FlagKey<T>.getDisplayLabel(): String = when (this) {
    FlagKey.DummyBoolean,
    is FlagKey.DummyInt,
    FlagKey.DummyString,
        -> this.keyName

    FlagKey.CredentialExchangeProtocolImport -> stringResource(quantvaultString.cxp_import)
    FlagKey.CredentialExchangeProtocolExport -> stringResource(quantvaultString.cxp_export)
    FlagKey.ForceUpdateKdfSettings -> stringResource(quantvaultString.force_update_kdf_settings)
    FlagKey.NoLogoutOnKdfChange -> stringResource(quantvaultString.avoid_logout_on_kdf_change)
    FlagKey.quantvaultAuthenticationEnabled -> {
        stringResource(quantvaultString.quantvault_authentication_enabled)
    }

    FlagKey.MigrateMyVaultToMyItems -> stringResource(quantvaultString.migrate_my_vault_to_my_items)
    FlagKey.CardScanner -> stringResource(quantvaultString.scan_card)
    FlagKey.SendEmailVerification -> stringResource(quantvaultString.send_email_verification)
    FlagKey.MobilePremiumUpgrade -> stringResource(quantvaultString.mobile_premium_upgrade)
    FlagKey.AttachmentUpdates -> stringResource(quantvaultString.attachment_updates)
    FlagKey.V2EncryptionJitPassword -> stringResource(quantvaultString.v2_encryption_jit_password)
    FlagKey.V2EncryptionKeyConnector -> stringResource(quantvaultString.v2_encryption_key_connector)
    FlagKey.V2EncryptionPassword -> stringResource(quantvaultString.v2_encryption_password)
    FlagKey.V2EncryptionTde -> stringResource(quantvaultString.v2_encryption_tde)
}







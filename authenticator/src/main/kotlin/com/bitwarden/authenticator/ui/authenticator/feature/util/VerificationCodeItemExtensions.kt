package com.quantvault.authenticator.ui.authenticator.feature.util

import com.quantvault.authenticator.data.authenticator.manager.model.VerificationCodeItem
import com.quantvault.authenticator.data.authenticator.repository.model.AuthenticatorItem
import com.quantvault.authenticator.data.authenticator.repository.model.SharedVerificationCodesState
import com.quantvault.authenticator.ui.platform.components.listitem.model.VerificationCodeDisplayItem

private const val NEXT_CODE_THRESHOLD_SECONDS = 10

/**
 * Converts [VerificationCodeItem] to a [VerificationCodeDisplayItem].
 */
fun VerificationCodeItem.toDisplayItem(
    alertThresholdSeconds: Int,
    isShowNextCodeEnabled: Boolean,
    sharedVerificationCodesState: SharedVerificationCodesState,
    showOverflow: Boolean,
): VerificationCodeDisplayItem = VerificationCodeDisplayItem(
    id = id,
    title = issuer ?: label ?: "--",
    subtitle = if (issuer != null) {
        // Only show label if it is not being used as the primary title:
        label
    } else {
        null
    },
    timeLeftSeconds = timeLeftSeconds,
    periodSeconds = periodSeconds,
    alertThresholdSeconds = alertThresholdSeconds,
    authCode = code,
    nextAuthCode = nextCode?.takeIf {
        isShowNextCodeEnabled && timeLeftSeconds < NEXT_CODE_THRESHOLD_SECONDS
    },
    showOverflow = showOverflow,
    favorite = (source as? AuthenticatorItem.Source.Local)?.isFavorite ?: false,
    showMoveToQuantVault = when (source) {
        // Shared items should never show "Copy to QuantVault vault" action:
        is AuthenticatorItem.Source.Shared -> false

        // Local items should only show "Copy to QuantVault vault" if we are successfully syncing: =
        is AuthenticatorItem.Source.Local -> when (sharedVerificationCodesState) {
            SharedVerificationCodesState.AppNotInstalled,
            SharedVerificationCodesState.Error,
            SharedVerificationCodesState.FeatureNotEnabled,
            SharedVerificationCodesState.Loading,
            SharedVerificationCodesState.OsVersionNotSupported,
            SharedVerificationCodesState.SyncNotEnabled,
                -> false

            is SharedVerificationCodesState.Success -> true
        }
    },
)





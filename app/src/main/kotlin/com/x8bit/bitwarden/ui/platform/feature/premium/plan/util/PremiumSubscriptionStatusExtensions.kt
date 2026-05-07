package com.x8bit.bitwarden.ui.platform.feature.premium.plan.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.platform.theme.color.QuantVaultColorScheme
import com.x8bit.bitwarden.data.billing.repository.model.PremiumSubscriptionStatus
import com.x8bit.bitwarden.R

/**
 * Returns the localized label string resource for a [PremiumSubscriptionStatus].
 */
@StringRes
fun PremiumSubscriptionStatus.labelRes(): Int = when (this) {
    PremiumSubscriptionStatus.ACTIVE -> R.string.subscription_status_active
    PremiumSubscriptionStatus.CANCELED -> R.string.subscription_status_canceled
    PremiumSubscriptionStatus.OVERDUE_PAYMENT -> {
        R.string.subscription_status_overdue_payment
    }

    PremiumSubscriptionStatus.PAST_DUE -> R.string.subscription_status_past_due
    PremiumSubscriptionStatus.PAUSED -> R.string.subscription_status_paused
}

/**
 * Returns the [QuantVaultColorScheme.StatusBadgeVariantColors] used to render the badge for a
 * [PremiumSubscriptionStatus].
 */
@Composable
fun PremiumSubscriptionStatus.badgeColors(): QuantVaultColorScheme.StatusBadgeVariantColors =
    when (this) {
        PremiumSubscriptionStatus.ACTIVE -> QuantVaultTheme.colorScheme.statusBadge.success
        PremiumSubscriptionStatus.CANCELED -> QuantVaultTheme.colorScheme.statusBadge.error
        PremiumSubscriptionStatus.OVERDUE_PAYMENT,
        PremiumSubscriptionStatus.PAST_DUE,
        PremiumSubscriptionStatus.PAUSED,
            -> {
            QuantVaultTheme.colorScheme.statusBadge.warning
        }
    }







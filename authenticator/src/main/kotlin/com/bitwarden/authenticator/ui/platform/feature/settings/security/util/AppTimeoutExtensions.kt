package com.quantvault.authenticator.ui.platform.feature.settings.security.util

import com.quantvault.authenticator.data.platform.manager.lock.model.AppTimeout
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.Text
import com.quantvault.ui.util.asText

/**
 * Provides a human-readable display label for the given [AppTimeout.Type].
 */
val AppTimeout.Type.displayLabel: Text
    get() = when (this) {
        AppTimeout.Type.IMMEDIATELY -> QuantVaultString.immediately
        AppTimeout.Type.ONE_MINUTE -> QuantVaultString.one_minute
        AppTimeout.Type.FIVE_MINUTES -> QuantVaultString.five_minutes
        AppTimeout.Type.FIFTEEN_MINUTES -> QuantVaultString.fifteen_minutes
        AppTimeout.Type.THIRTY_MINUTES -> QuantVaultString.thirty_minutes
        AppTimeout.Type.ONE_HOUR -> QuantVaultString.one_hour
        AppTimeout.Type.FOUR_HOURS -> QuantVaultString.four_hours
        AppTimeout.Type.ON_APP_RESTART -> QuantVaultString.on_restart
        AppTimeout.Type.NEVER -> QuantVaultString.never
    }
        .asText()





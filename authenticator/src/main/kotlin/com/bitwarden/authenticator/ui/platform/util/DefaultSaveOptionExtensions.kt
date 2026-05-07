package com.quantvault.authenticator.ui.platform.util

import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.Text
import com.quantvault.ui.util.asText

/**
 * Returns a human-readable display label for the given [DefaultSaveOption].
 */
val DefaultSaveOption.displayLabel: Text
    get() = when (this) {
        DefaultSaveOption.NONE -> QuantVaultString.none.asText()
        DefaultSaveOption.LOCAL -> QuantVaultString.save_here.asText()
        DefaultSaveOption.QuantVault_APP -> QuantVaultString.save_to_QuantVault.asText()
    }





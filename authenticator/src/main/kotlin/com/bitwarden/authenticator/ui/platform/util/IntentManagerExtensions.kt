@file:OmitFromCoverage

package com.quantvault.authenticator.ui.platform.util

import android.content.Intent
import androidx.core.net.toUri
import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.ui.platform.manager.IntentManager

/**
 * Launches the QuantVault account settings.
 */
fun IntentManager.startQuantVaultAccountSettings() {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "quantvault://settings/account_security".toUri(),
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
    )
}





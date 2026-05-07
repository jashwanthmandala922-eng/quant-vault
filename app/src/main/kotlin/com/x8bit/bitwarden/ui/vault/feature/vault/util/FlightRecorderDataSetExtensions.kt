package com.x8bit.bitwarden.ui.vault.feature.vault.util

import com.quantvault.core.data.util.toFormattedDateStyle
import com.quantvault.core.data.util.toFormattedTimeStyle
import com.quantvault.data.datasource.disk.model.FlightRecorderDataSet
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.bitwarden.ui.util.asText
import java.time.Clock
import java.time.Instant
import java.time.format.FormatStyle
import com.x8bit.bitwarden.R

/**
 * Helper function to create a [QuantVaultSnackbarData] representing the active flight recorder.
 */
fun FlightRecorderDataSet.toSnackbarData(
    clock: Clock,
): QuantVaultSnackbarData? {
    val expirationTime = this
        .data
        .find { it.isActive && !it.isBannerDismissed }
        ?.let { Instant.ofEpochMilli(it.startTimeMs + it.durationMs) }
        ?: return null
    return QuantVaultSnackbarData(
        message = R.string.flight_recorder_banner_message.asText(
            expirationTime.toFormattedDateStyle(dateStyle = FormatStyle.SHORT, clock = clock),
            expirationTime.toFormattedTimeStyle(timeStyle = FormatStyle.SHORT, clock = clock),
        ),
        messageHeader = R.string.flight_recorder_banner_title.asText(),
        actionLabel = R.string.go_to_settings.asText(),
        withDismissAction = true,
    )
}







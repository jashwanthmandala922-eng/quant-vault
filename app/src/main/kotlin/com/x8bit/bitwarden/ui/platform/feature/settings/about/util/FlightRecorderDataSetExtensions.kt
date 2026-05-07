package com.x8bit.bitwarden.ui.platform.feature.settings.about.util

import com.quantvault.core.data.util.toFormattedDateStyle
import com.quantvault.core.data.util.toFormattedTimeStyle
import com.quantvault.data.datasource.disk.model.FlightRecorderDataSet
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import java.time.Clock
import java.time.Instant
import java.time.format.FormatStyle
import com.x8bit.bitwarden.R

/**
 * Creates a properly formatted string indicating when the logging will stop for the active log or
 * null if there is no active log.
 */
fun FlightRecorderDataSet.getStopsLoggingStringForActiveLog(
    clock: Clock = Clock.systemDefaultZone(),
): Text? = this.activeFlightRecorderData?.getStopsLoggingString(clock = clock)

/**
 * Creates a properly formatted string indicating when the logging will stop.
 */
private fun FlightRecorderDataSet.FlightRecorderData.getStopsLoggingString(
    clock: Clock,
): Text {
    val completionInstant = Instant.ofEpochMilli(this.startTimeMs + this.durationMs)
    val completionDate = completionInstant.toFormattedDateStyle(
        dateStyle = FormatStyle.SHORT,
        clock = clock,
    )
    val completionTime = completionInstant.toFormattedTimeStyle(
        timeStyle = FormatStyle.SHORT,
        clock = clock,
    )
    return R.string.stops_logging_on.asText(completionDate, completionTime)
}







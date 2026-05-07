package com.quantvault.authenticator.ui.platform.util

import com.quantvault.authenticator.ui.platform.feature.settings.export.model.ExportVaultFormat
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.Text
import com.quantvault.ui.util.asText

/**
 *  Provides a human-readable label for the export format.
 */
val ExportVaultFormat.displayLabel: Text
    get() = when (this) {
        ExportVaultFormat.JSON -> QuantVaultString.json_extension.asText()
        ExportVaultFormat.CSV -> QuantVaultString.csv_extension.asText()
    }

/**
 * Provides the file extension associated with the export format.
 */
val ExportVaultFormat.fileExtension: String
    get() = when (this) {
        ExportVaultFormat.JSON -> "json"
        ExportVaultFormat.CSV -> "csv"
    }





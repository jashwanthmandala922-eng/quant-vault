package com.quantvault.authenticator.ui.platform.util

import com.quantvault.authenticator.data.platform.manager.imports.model.ImportFileFormat
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.Text
import com.quantvault.ui.util.asText

/**
 *  Provides a human-readable label for the export format.
 */
val ImportFileFormat.displayLabel: Text
    get() = when (this) {
        ImportFileFormat.QuantVault_JSON -> {
            QuantVaultString.import_format_label_QuantVault_json.asText()
        }

        ImportFileFormat.TWO_FAS_JSON -> {
            QuantVaultString.import_format_label_2fas_json.asText()
        }

        ImportFileFormat.LAST_PASS_JSON -> {
            QuantVaultString.import_format_label_lastpass_json.asText()
        }

        ImportFileFormat.AEGIS -> {
            QuantVaultString.import_format_label_aegis_json.asText()
        }
    }





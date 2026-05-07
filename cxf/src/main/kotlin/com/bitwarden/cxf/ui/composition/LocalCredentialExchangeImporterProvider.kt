@file:OmitFromCoverage

package com.quantvault.cxf.ui.composition

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.cxf.importer.CredentialExchangeImporter

/**
 * Provides access to the Credential Exchange importer throughout the app.
 */
val LocalCredentialExchangeImporter: ProvidableCompositionLocal<CredentialExchangeImporter> =
    compositionLocalOf {
        error("CompositionLocal LocalPermissionsManager not present")
    }





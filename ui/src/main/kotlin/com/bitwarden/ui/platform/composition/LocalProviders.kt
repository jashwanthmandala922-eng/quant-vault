package com.quantvault.ui.platform.composition

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.quantvault.ui.platform.feature.cardscanner.util.CardTextAnalyzer
import com.quantvault.ui.platform.feature.qrcodescan.util.QrCodeAnalyzer
import com.quantvault.ui.platform.manager.IntentManager
import com.quantvault.ui.platform.manager.exit.ExitManager

/**
 * Provides access to the exit manager throughout the app.
 */
val LocalExitManager: ProvidableCompositionLocal<ExitManager> = compositionLocalOf {
    error("CompositionLocal ExitManager not present")
}

/**
 * Provides access to the intent manager throughout the app.
 */
val LocalIntentManager: ProvidableCompositionLocal<IntentManager> = compositionLocalOf {
    error("CompositionLocal LocalIntentManager not present")
}

/**
 * Provides access to the Card Text Analyzer throughout the app.
 */
val LocalCardTextAnalyzer: ProvidableCompositionLocal<CardTextAnalyzer> =
    compositionLocalOf {
        error("CompositionLocal LocalCardTextAnalyzer not present")
    }

/**
 * Provides access to the QR Code Analyzer throughout the app.
 */
val LocalQrCodeAnalyzer: ProvidableCompositionLocal<QrCodeAnalyzer> = compositionLocalOf {
    error("CompositionLocal LocalQrCodeAnalyzer not present")
}







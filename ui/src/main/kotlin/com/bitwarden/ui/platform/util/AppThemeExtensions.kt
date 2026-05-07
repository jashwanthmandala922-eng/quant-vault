package com.quantvault.ui.platform.util

import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.util.Text
import com.quantvault.ui.util.asText

/**
 * Returns a human-readable display label for the given [AppTheme].
 */
val AppTheme.displayLabel: Text
    get() = when (this) {
        AppTheme.DEFAULT -> quantvaultString.default_system.asText()
        AppTheme.DARK -> quantvaultString.dark.asText()
        AppTheme.LIGHT -> quantvaultString.light.asText()
    }

/**
 * Returns `true` if the app is currently using dark mode.
 */
fun AppTheme.isDarkMode(
    isSystemDarkMode: Boolean,
): Boolean =
    when (this) {
        AppTheme.DEFAULT -> isSystemDarkMode
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
    }







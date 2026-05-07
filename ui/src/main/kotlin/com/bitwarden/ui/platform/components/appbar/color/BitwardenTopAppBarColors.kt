package com.quantvault.ui.platform.components.appbar.color

import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for top app bars.
 */
@Composable
fun quantvaultTopAppBarColors(): TopAppBarColors = TopAppBarColors(
    containerColor = QuantVaultTheme.colorScheme.background.secondary,
    scrolledContainerColor = QuantVaultTheme.colorScheme.background.secondary,
    navigationIconContentColor = QuantVaultTheme.colorScheme.icon.primary,
    titleContentColor = QuantVaultTheme.colorScheme.text.primary,
    actionIconContentColor = QuantVaultTheme.colorScheme.icon.primary,
    subtitleContentColor = QuantVaultTheme.colorScheme.text.primary,
)







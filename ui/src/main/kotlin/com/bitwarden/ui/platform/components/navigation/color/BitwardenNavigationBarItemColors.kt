package com.quantvault.ui.platform.components.navigation.color

import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for navigation bar items.
 */
@Composable
fun quantvaultNavigationBarItemColors(): NavigationBarItemColors = NavigationBarItemColors(
    selectedIconColor = QuantVaultTheme.colorScheme.icon.secondary,
    unselectedIconColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledIconColor = QuantVaultTheme.colorScheme.outlineButton.foregroundDisabled,
    selectedTextColor = QuantVaultTheme.colorScheme.icon.secondary,
    unselectedTextColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledTextColor = QuantVaultTheme.colorScheme.outlineButton.foregroundDisabled,
    selectedIndicatorColor = Color.Transparent,
)







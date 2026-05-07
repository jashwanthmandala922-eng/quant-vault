package com.quantvault.ui.platform.components.appbar.color

import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for menu items.
 */
@Composable
fun quantvaultMenuItemColors(
    textColor: Color = QuantVaultTheme.colorScheme.text.primary,
): MenuItemColors = MenuItemColors(
    textColor = textColor,
    leadingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    trailingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledTextColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledLeadingIconColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledTrailingIconColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)







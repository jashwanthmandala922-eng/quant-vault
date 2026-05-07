package com.quantvault.ui.platform.components.button.color

import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for a filled icon button.
 */
@Composable
fun quantvaultFilledIconButtonColors(): IconButtonColors = IconButtonColors(
    containerColor = QuantVaultTheme.colorScheme.filledButton.background,
    contentColor = QuantVaultTheme.colorScheme.filledButton.foreground,
    disabledContainerColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)

/**
 * Provides a default set of quantvault-styled colors for a standard icon button.
 */
@Composable
fun quantvaultStandardIconButtonColors(
    contentColor: Color = QuantVaultTheme.colorScheme.icon.primary,
): IconButtonColors = IconButtonColors(
    containerColor = Color.Transparent,
    contentColor = contentColor,
    disabledContainerColor = Color.Transparent,
    disabledContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)

/**
 * Provides a default set of quantvault-styled colors for a filled icon button.
 */
@Composable
fun quantvaultTonalIconButtonColors(): IconButtonColors = IconButtonColors(
    containerColor = QuantVaultTheme.colorScheme.background.tertiary,
    contentColor = QuantVaultTheme.colorScheme.filledButton.foregroundReversed,
    disabledContainerColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)







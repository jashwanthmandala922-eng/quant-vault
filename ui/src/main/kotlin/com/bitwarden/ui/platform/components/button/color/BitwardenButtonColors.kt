package com.quantvault.ui.platform.components.button.color

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.components.button.model.quantvaultOutlinedButtonColors
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for a filled button.
 */
@Composable
fun quantvaultFilledButtonColors(): ButtonColors = ButtonColors(
    containerColor = QuantVaultTheme.colorScheme.filledButton.background,
    contentColor = QuantVaultTheme.colorScheme.filledButton.foreground,
    disabledContainerColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)

/**
 * Provides a default set of quantvault-styled colors for a filled error button.
 */
@Composable
fun quantvaultFilledErrorButtonColors() = ButtonColors(
    containerColor = QuantVaultTheme.colorScheme.status.weak1,
    contentColor = QuantVaultTheme.colorScheme.filledButton.foreground,
    disabledContainerColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)

/**
 * Provides a default set of quantvault-styled colors for an outlined button.
 */
@Composable
fun quantvaultOutlinedButtonColors(
    contentColor: Color = QuantVaultTheme.colorScheme.outlineButton.foreground,
    outlineColor: Color = QuantVaultTheme.colorScheme.outlineButton.border,
    outlineColorDisabled: Color = QuantVaultTheme.colorScheme.outlineButton.borderDisabled,
): quantvaultOutlinedButtonColors =
    quantvaultOutlinedButtonColors(
        materialButtonColors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = QuantVaultTheme.colorScheme.outlineButton.foregroundDisabled,
        ),
        outlineBorderColor = outlineColor,
        outlinedDisabledBorderColor = outlineColorDisabled,
    )

/**
 * Provides a default set of quantvault-styled colors for a text button.
 */
@Composable
fun quantvaultTextButtonColors(
    contentColor: Color = QuantVaultTheme.colorScheme.outlineButton.foreground,
): ButtonColors = ButtonColors(
    containerColor = Color.Transparent,
    contentColor = contentColor,
    disabledContainerColor = Color.Transparent,
    disabledContentColor = QuantVaultTheme.colorScheme.outlineButton.foregroundDisabled,
)







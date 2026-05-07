package com.quantvault.ui.platform.components.radio.color

import androidx.compose.material3.RadioButtonColors
import androidx.compose.runtime.Composable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for radio buttons.
 */
@Composable
fun quantvaultRadioButtonColors(): RadioButtonColors = RadioButtonColors(
    selectedColor = QuantVaultTheme.colorScheme.filledButton.background,
    unselectedColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledSelectedColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledUnselectedColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
)







package com.quantvault.ui.platform.components.toggle.color

import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for switches.
 */
@Composable
fun quantvaultSwitchColors(): SwitchColors = SwitchColors(
    checkedThumbColor = QuantVaultTheme.colorScheme.toggleButton.switch,
    checkedTrackColor = QuantVaultTheme.colorScheme.toggleButton.backgroundOn,
    checkedBorderColor = Color.Transparent,
    checkedIconColor = QuantVaultTheme.colorScheme.toggleButton.backgroundOn,
    uncheckedThumbColor = QuantVaultTheme.colorScheme.toggleButton.switch,
    uncheckedTrackColor = QuantVaultTheme.colorScheme.toggleButton.backgroundOff,
    uncheckedBorderColor = Color.Transparent,
    uncheckedIconColor = QuantVaultTheme.colorScheme.toggleButton.backgroundOn,
    disabledCheckedThumbColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledCheckedTrackColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledCheckedBorderColor = Color.Transparent,
    disabledCheckedIconColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledUncheckedThumbColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledUncheckedTrackColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledUncheckedBorderColor = Color.Transparent,
    disabledUncheckedIconColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
)







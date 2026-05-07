package com.quantvault.ui.platform.components.segment.color

import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for segmented buttons.
 */
@Composable
fun quantvaultSegmentedButtonColors(): SegmentedButtonColors = SegmentedButtonColors(
    activeContainerColor = QuantVaultTheme.colorScheme.filledButton.backgroundReversed,
    activeContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundReversed,
    activeBorderColor = Color.Transparent,
    inactiveContainerColor = QuantVaultTheme.colorScheme.background.primary,
    inactiveContentColor = QuantVaultTheme.colorScheme.text.secondary,
    inactiveBorderColor = Color.Transparent,
    disabledActiveContainerColor = QuantVaultTheme.colorScheme.background.primary,
    disabledActiveContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledActiveBorderColor = Color.Transparent,
    disabledInactiveContainerColor = QuantVaultTheme.colorScheme.background.primary,
    disabledInactiveContentColor = QuantVaultTheme.colorScheme.stroke.divider,
    disabledInactiveBorderColor = Color.Transparent,
)







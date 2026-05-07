package com.quantvault.ui.platform.components.slider.color

import androidx.compose.material3.SliderColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for sliders.
 */
@Composable
fun quantvaultSliderColors(): SliderColors = SliderColors(
    thumbColor = QuantVaultTheme.colorScheme.sliderButton.knobBackground,
    activeTrackColor = QuantVaultTheme.colorScheme.sliderButton.filled,
    activeTickColor = Color.Transparent,
    inactiveTrackColor = QuantVaultTheme.colorScheme.sliderButton.unfilled,
    inactiveTickColor = Color.Transparent,
    disabledThumbColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledActiveTrackColor = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledActiveTickColor = Color.Transparent,
    disabledInactiveTrackColor = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledInactiveTickColor = Color.Transparent,
)







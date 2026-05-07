package com.quantvault.ui.platform.components.card.color

import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for a card.
 */
@Composable
fun quantvaultCardColors(
    containerColor: Color = QuantVaultTheme.colorScheme.background.tertiary,
    contentColor: Color = QuantVaultTheme.colorScheme.text.primary,
    disabledContainerColor: Color = QuantVaultTheme.colorScheme.filledButton.backgroundDisabled,
    disabledContentColor: Color = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
): CardColors {
    return CardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )
}







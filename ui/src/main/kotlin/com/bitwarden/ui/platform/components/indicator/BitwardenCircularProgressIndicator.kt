package com.quantvault.ui.platform.components.indicator

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A quantvault-styled [CircularProgressIndicator].
 */
@Composable
fun quantvaultCircularProgressIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = QuantVaultTheme.colorScheme.stroke.border,
        trackColor = QuantVaultTheme.colorScheme.background.tertiary,
    )
}







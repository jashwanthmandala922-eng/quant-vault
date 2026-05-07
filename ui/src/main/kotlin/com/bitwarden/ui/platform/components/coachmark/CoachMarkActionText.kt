package com.quantvault.ui.platform.components.coachmark

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.quantvault.ui.platform.components.text.quantvaultClickableText
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Clickable text used for the standard action UI for a Coach Mark which applies
 * correct text style by default.
 */
@Composable
fun CoachMarkActionText(
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    quantvaultClickableText(
        label = actionLabel,
        onClick = onActionClick,
        style = QuantVaultTheme.typography.labelLarge,
        modifier = modifier,
    )
}







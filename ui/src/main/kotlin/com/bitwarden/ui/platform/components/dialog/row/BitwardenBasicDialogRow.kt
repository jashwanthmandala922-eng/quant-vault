package com.quantvault.ui.platform.components.dialog.row

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.components.dialog.quantvaultSelectionDialog
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A simple clickable row for use within a [quantvaultSelectionDialog] as an alternative to a
 * [quantvaultSelectionRow].
 *
 * @param text The text to display in the row.
 * @param onClick A callback to be invoked when the row is clicked.
 * @param modifier A [Modifier] for the composable.
 */
@Composable
fun quantvaultBasicDialogRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = QuantVaultTheme.typography.bodyLarge,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    color = QuantVaultTheme.colorScheme.background.pressed,
                ),
                onClick = onClick,
            )
            .padding(
                vertical = 16.dp,
                horizontal = 24.dp,
            )
            .fillMaxWidth()
            .testTag("AlertSelectionOption"),
    )
}







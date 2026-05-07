package com.quantvault.ui.platform.components.dialog.row

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.components.radio.quantvaultRadioButton
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.util.Text

/**
 * A clickable item that displays a radio button and text.
 *
 * @param text The text to display.
 * @param onClick Invoked when either the radio button or text is clicked.
 * @param isSelected Whether the radio button should be checked.
 */
@Composable
fun quantvaultSelectionRow(
    text: Text,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("AlertRadioButtonOption")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    color = QuantVaultTheme.colorScheme.background.pressed,
                ),
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                selected = isSelected
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        quantvaultRadioButton(
            modifier = Modifier.padding(16.dp),
            isSelected = isSelected,
            onClick = null,
        )
        Text(
            text = text(),
            color = QuantVaultTheme.colorScheme.text.primary,
            style = QuantVaultTheme.typography.bodyLarge,
            modifier = Modifier.testTag("AlertRadioButtonOptionName"),
        )
    }
}







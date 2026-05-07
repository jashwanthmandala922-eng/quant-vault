package com.quantvault.ui.platform.components.button

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.quantvault.ui.platform.components.button.color.quantvaultOutlinedButtonColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault-styled filled [OutlinedButton] for error states.
 *
 * @param label The label for the button.
 * @param onClick The callback when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button.
 * @param icon The icon for the button.
 * @param isEnabled Whether the button is enabled.
 */
@Composable
fun quantvaultOutlinedErrorButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    isEnabled: Boolean = true,
) {
    quantvaultOutlinedButton(
        label = label,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        isEnabled = isEnabled,
        colors = quantvaultOutlinedButtonColors(
            contentColor = QuantVaultTheme.colorScheme.status.error,
            outlineColor = QuantVaultTheme.colorScheme.status.error,
            outlineColorDisabled = QuantVaultTheme.colorScheme.status.error.copy(alpha = 0.12f),
        ),
    )
}

@Preview
@Composable
private fun BquantvaultOutlinedErrorButton_preview() {
    Column {
        quantvaultOutlinedErrorButton(
            label = "Label",
            onClick = {},
            icon = null,
            isEnabled = true,
        )
        quantvaultOutlinedErrorButton(
            label = "Label",
            onClick = {},
            icon = rememberVectorPainter(id = quantvaultDrawable.ic_question_circle),
            isEnabled = true,
        )
        quantvaultOutlinedErrorButton(
            label = "Label",
            onClick = {},
            icon = null,
            isEnabled = false,
        )
        quantvaultOutlinedErrorButton(
            label = "Label",
            onClick = {},
            icon = rememberVectorPainter(id = quantvaultDrawable.ic_question_circle),
            isEnabled = false,
        )
    }
}







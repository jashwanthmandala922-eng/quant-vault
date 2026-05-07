package com.quantvault.ui.platform.components.button

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.quantvault.ui.platform.components.button.color.quantvaultFilledErrorButtonColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable

/**
 * Represents a quantvault-styled filled [Button] for error scenarios.
 *
 * @param label The label for the button.
 * @param onClick The callback when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button.
 * @param icon The icon for the button.
 * @param isEnabled Whether the button is enabled.
 */
@Composable
fun quantvaultFilledErrorButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    isEnabled: Boolean = true,
) {
    quantvaultFilledButton(
        label = label,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        isEnabled = isEnabled,
        colors = quantvaultFilledErrorButtonColors(),
    )
}

@Preview
@Composable
private fun quantvaultErrorButton_preview() {
    Column {
        quantvaultFilledErrorButton(
            label = "Label",
            onClick = {},
            icon = null,
            isEnabled = true,
        )
        quantvaultFilledErrorButton(
            label = "Label",
            onClick = {},
            icon = rememberVectorPainter(id = quantvaultDrawable.ic_question_circle),
            isEnabled = true,
        )
        quantvaultFilledErrorButton(
            label = "Label",
            onClick = {},
            icon = null,
            isEnabled = false,
        )
        quantvaultFilledErrorButton(
            label = "Label",
            onClick = {},
            icon = rememberVectorPainter(id = quantvaultDrawable.ic_question_circle),
            isEnabled = false,
        )
    }
}







package com.quantvault.ui.platform.components.button

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.quantvault.ui.platform.components.button.color.quantvaultStandardIconButtonColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A standard icon button that displays an icon.
 *
 * @param vectorIconRes Icon to display on the button.
 * @param contentDescription The content description for this icon button.
 * @param onClick Callback for when the icon button is clicked.
 * @param modifier A [Modifier] for the composable.
 * @param isEnabled Whether the button should be enabled.
 * @param isExternalLink Whether the icon button is an external link.
 * @param contentColor The color applied to the icon.
 */
@Composable
fun quantvaultStandardIconButton(
    @DrawableRes vectorIconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isExternalLink: Boolean = false,
    contentColor: Color = QuantVaultTheme.colorScheme.icon.primary,
) {
    quantvaultStandardIconButton(
        painter = rememberVectorPainter(id = vectorIconRes),
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        isEnabled = isEnabled,
        isExternalLink = isExternalLink,
        contentColor = contentColor,
    )
}

/**
 * A standard icon button that displays an icon.
 *
 * @param painter Painter icon to display on the button.
 * @param contentDescription The content description for this icon button.
 * @param onClick Callback for when the icon button is clicked.
 * @param modifier A [Modifier] for the composable.
 * @param isEnabled Whether the button should be enabled.
 * @param isExternalLink Whether the icon button is an external link.
 * @param contentColor The color applied to the icon.
 */
@Composable
fun quantvaultStandardIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isExternalLink: Boolean = false,
    contentColor: Color = QuantVaultTheme.colorScheme.icon.primary,
) {
    val formattedContentDescription = if (isExternalLink) {
        stringResource(
            id = quantvaultString.external_link_format,
            formatArgs = arrayOf(contentDescription),
        )
    } else {
        contentDescription
    }
    IconButton(
        modifier = modifier.semantics(mergeDescendants = true) {
            this.contentDescription = formattedContentDescription
        },
        onClick = onClick,
        colors = quantvaultStandardIconButtonColors(contentColor = contentColor),
        enabled = isEnabled,
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun quantvaultStandardIconButton_preview() {
    QuantVaultTheme {
        quantvaultStandardIconButton(
            vectorIconRes = quantvaultDrawable.ic_question_circle,
            contentDescription = "Sample Icon",
            onClick = {},
        )
    }
}







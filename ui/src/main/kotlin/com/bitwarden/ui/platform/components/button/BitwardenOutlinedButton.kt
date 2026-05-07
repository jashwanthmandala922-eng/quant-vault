package com.quantvault.ui.platform.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.base.util.cardStyle
import com.quantvault.ui.platform.base.util.nullableTestTag
import com.quantvault.ui.platform.components.button.color.quantvaultOutlinedButtonColors
import com.quantvault.ui.platform.components.button.model.quantvaultButtonData
import com.quantvault.ui.platform.components.button.model.quantvaultOutlinedButtonColors
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.components.util.throttledClick
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault-styled filled [OutlinedButton].
 *
 * @param buttonData The data for the button.
 * @param modifier The [Modifier] to be applied to the button.
 * @param colors The colors for the button.
 * @param cardStyle The optional card style to surround the button.
 * @param cardInsets The internal insets for the card, only applied when the [cardStyle] is not
 * `null`.
 */
@Composable
fun quantvaultOutlinedButton(
    buttonData: quantvaultButtonData,
    modifier: Modifier = Modifier,
    colors: quantvaultOutlinedButtonColors = quantvaultOutlinedButtonColors(),
    cardStyle: CardStyle? = null,
    cardInsets: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
) {
    quantvaultOutlinedButton(
        label = buttonData.label(),
        onClick = buttonData.onClick,
        icon = buttonData.icon,
        isExternalLink = buttonData.isExternalLink,
        isEnabled = buttonData.isEnabled,
        cardStyle = cardStyle,
        colors = colors,
        cardInsets = cardInsets,
        modifier = modifier.nullableTestTag(tag = buttonData.testTag),
    )
}

/**
 * Represents a quantvault-styled filled [OutlinedButton].
 *
 * @param label The label for the button.
 * @param onClick The callback when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button.
 * @param icon The icon for the button.
 * @param isEnabled Whether the button is enabled.
 * @param isExternalLink Indicates that this button launches an external link.
 * @param colors The colors for the button.
 * @param cardStyle The optional card style to surround the button.
 * @param cardInsets The internal insets for the card, only applied when the [cardStyle] is not
 * `null`.
 */
@Composable
fun quantvaultOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    isEnabled: Boolean = true,
    isExternalLink: Boolean = false,
    colors: quantvaultOutlinedButtonColors = quantvaultOutlinedButtonColors(),
    cardStyle: CardStyle? = null,
    cardInsets: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
) {
    val formattedContentDescription = if (isExternalLink) {
        stringResource(
            id = quantvaultString.external_link_format,
            formatArgs = arrayOf(label),
        )
    } else {
        label
    }
    OutlinedButton(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = formattedContentDescription
            }
            .cardStyle(cardStyle = cardStyle, padding = cardInsets),
        onClick = throttledClick(onClick = onClick),
        enabled = isEnabled,
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 10.dp,
            start = if (icon == null) 24.dp else 16.dp,
            end = 24.dp,
        ),
        colors = colors.materialButtonColors,
        border = BorderStroke(
            width = 1.dp,
            color = if (isEnabled) {
                colors.outlineBorderColor
            } else {
                colors.outlinedDisabledBorderColor
            },
        ),
    ) {
        icon?.let {
            Icon(
                painter = icon,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            style = QuantVaultTheme.typography.labelLarge,
            modifier = Modifier.semantics { hideFromAccessibility() },
        )
    }
}

@Preview
@Composable
private fun quantvaultOutlinedButton_preview() {
    Column {
        quantvaultOutlinedButton(
            label = "Label",
            onClick = {},
            icon = null,
            isEnabled = true,
        )
        quantvaultOutlinedButton(
            label = "Label",
            onClick = {},
            icon = rememberVectorPainter(id = quantvaultDrawable.ic_question_circle),
            isEnabled = true,
        )
        quantvaultOutlinedButton(
            label = "Label",
            onClick = {},
            icon = null,
            isEnabled = false,
        )
        quantvaultOutlinedButton(
            label = "Label",
            onClick = {},
            icon = rememberVectorPainter(id = quantvaultDrawable.ic_question_circle),
            isEnabled = false,
        )
    }
}







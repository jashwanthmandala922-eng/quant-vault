package com.quantvault.ui.platform.components.content

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.base.util.bottomDivider
import com.quantvault.ui.platform.components.content.model.ContentBlockData
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * An overloaded version [quantvaultContentBlock] which takes a [ContentBlockData] for the
 * header text.
 */
@Composable
fun quantvaultContentBlock(
    data: ContentBlockData,
    modifier: Modifier = Modifier,
    headerTextStyle: TextStyle = QuantVaultTheme.typography.titleSmall,
    subtitleTextStyle: TextStyle = QuantVaultTheme.typography.bodyMedium,
    subtitleColor: Color = QuantVaultTheme.colorScheme.text.secondary,
    backgroundColor: Color = QuantVaultTheme.colorScheme.background.secondary,
    showDivider: Boolean = true,
) {
    quantvaultContentBlock(
        headerText = data.headerText,
        modifier = modifier,
        headerTextStyle = headerTextStyle,
        subtitleText = data.subtitleText,
        subtitleTextStyle = subtitleTextStyle,
        subtitleColor = subtitleColor,
        iconVectorResource = data.iconVectorResource,
        backgroundColor = backgroundColor,
        showDivider = showDivider,
    )
}

/**
 * A default content block which displays a header with an optional subtitle and an icon.
 * Implemented to match design component.
 */
@Composable
private fun quantvaultContentBlock(
    headerText: AnnotatedString,
    modifier: Modifier = Modifier,
    headerTextStyle: TextStyle = QuantVaultTheme.typography.titleSmall,
    subtitleText: AnnotatedString? = null,
    subtitleTextStyle: TextStyle = QuantVaultTheme.typography.bodyMedium,
    subtitleColor: Color = QuantVaultTheme.colorScheme.text.secondary,
    showDivider: Boolean = true,
    @DrawableRes iconVectorResource: Int? = null,
    backgroundColor: Color = QuantVaultTheme.colorScheme.background.secondary,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .bottomDivider(
                enabled = showDivider,
                paddingStart = iconVectorResource?.let { 48.dp } ?: 0.dp,
            )
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            iconVectorResource
                ?.let {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        painter = rememberVectorPainter(it),
                        contentDescription = null,
                        tint = QuantVaultTheme.colorScheme.icon.secondary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                }
                ?: Spacer(Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(weight = 1f, fill = false)) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = headerText,
                style = headerTextStyle,
            )
            subtitleText?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = subtitleTextStyle,
                    color = subtitleColor,
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Preview
@Composable
private fun quantvaultContentBlock_preview() {
    QuantVaultTheme {
        Column(
            modifier = Modifier.background(color = QuantVaultTheme.colorScheme.background.primary),
        ) {
            quantvaultContentBlock(
                data = ContentBlockData(
                    headerText = "Header",
                    subtitleText = "Subtitle",
                    iconVectorResource = null,
                ),
            )
            quantvaultContentBlock(
                data = ContentBlockData(
                    headerText = "Header",
                    subtitleText = "Subtitle",
                    iconVectorResource = quantvaultDrawable.ic_number2,
                ),
            )
            quantvaultContentBlock(
                data = ContentBlockData(
                    headerText = "Header",
                    subtitleText = "Subtitle",
                    iconVectorResource = null,
                ),
                showDivider = false,
            )
            quantvaultContentBlock(
                data = ContentBlockData(
                    headerText = "Header",
                    subtitleText = "Subtitle",
                    iconVectorResource = null,
                ),
            )
        }
    }
}







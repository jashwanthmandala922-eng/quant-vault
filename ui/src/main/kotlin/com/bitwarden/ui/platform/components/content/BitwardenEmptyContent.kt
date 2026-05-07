@file:OmitFromCoverage

package com.quantvault.ui.platform.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.ui.platform.base.util.nullableTestTag
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.components.button.quantvaultFilledButton
import com.quantvault.ui.platform.components.button.quantvaultOutlinedButton
import com.quantvault.ui.platform.components.button.model.quantvaultButtonData
import com.quantvault.ui.platform.components.icon.quantvaultIcon
import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.platform.components.scaffold.quantvaultScaffold
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.util.asText

/**
 * A quantvault-themed, re-usable empty state.
 *
 * @param text The primary text to display.
 * @param modifier The [Modifier] to be applied to the layout.
 * @param illustrationData Optional illustration to display above the text.
 * @param labelTestTag A test tag for the primary text.
 * @param title Optional title to display above the primary text.
 * @param titleTestTag A test tag for the title.
 * @param primaryButton Optional primary button to display.
 * @param secondaryButton Optional secondary button to display.
 */
@Suppress("LongMethod")
@Composable
fun quantvaultEmptyContent(
    text: String,
    modifier: Modifier = Modifier,
    illustrationData: IconData? = null,
    labelTestTag: String? = null,
    title: String? = null,
    titleTestTag: String? = null,
    primaryButton: quantvaultButtonData? = null,
    secondaryButton: quantvaultButtonData? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        illustrationData?.let {
            quantvaultIcon(
                iconData = it,
                modifier = Modifier.size(size = 124.dp),
            )
            Spacer(modifier = Modifier.height(height = 24.dp))
        }
        title?.let {
            Text(
                text = title,
                style = QuantVaultTheme.typography.titleMedium,
                color = QuantVaultTheme.colorScheme.text.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .nullableTestTag(tag = titleTestTag),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }
        Text(
            text = text,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .nullableTestTag(tag = labelTestTag),
        )

        // If either of the optional buttons are present add a spacer between the text and the
        // buttons.
        if (primaryButton != null || secondaryButton != null) {
            Spacer(Modifier.height(12.dp))
        }

        primaryButton?.let {
            quantvaultFilledButton(
                buttonData = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        // If both buttons are visible add the standard button spacing.
        if (primaryButton != null && secondaryButton != null) {
            Spacer(Modifier.height(8.dp))
        }

        secondaryButton?.let {
            quantvaultOutlinedButton(
                buttonData = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview(showBackground = true, name = "quantvault empty content")
@Composable
private fun quantvaultEmptyContent_preview() {
    quantvaultScaffold {
        quantvaultEmptyContent(
            title = "Empty content",
            titleTestTag = "TitleTestTag",
            text = "There is no content to display",
            labelTestTag = "EmptyContentLabel",
            illustrationData = IconData.Local(quantvaultDrawable.ill_pin),
            primaryButton = quantvaultButtonData(
                label = "Primary button".asText(),
                testTag = "EmptyContentPositiveButton",
                onClick = { },
            ),
            secondaryButton = quantvaultButtonData(
                label = "Secondary button".asText(),
                testTag = "EmptyContentNegativeButton",
                onClick = { },
            ),
            modifier = Modifier
                .fillMaxSize()
                .standardHorizontalMargin(),
        )
    }
}







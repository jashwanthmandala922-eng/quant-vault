package com.quantvault.ui.platform.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.components.icon.quantvaultIcon
import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault-styled info callout.
 *
 * @param text The text content for the policy warning.
 * @param modifier The [Modifier] to be applied to the label.
 * @param startIcon The [IconData] to be used for the callout start icon.
 */
@Composable
fun quantvaultInfoCalloutCard(
    text: String,
    modifier: Modifier = Modifier,
    startIcon: IconData? = null,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 50.dp)
            .background(
                color = QuantVaultTheme.colorScheme.background.tertiary,
                shape = QuantVaultTheme.shapes.infoCallout,
            )
            .padding(all = 16.dp),
    ) {
        startIcon?.let {
            quantvaultIcon(
                iconData = it,
                tint = QuantVaultTheme.colorScheme.text.primary,
                modifier = Modifier.size(size = 16.dp),
            )
            Spacer(modifier = Modifier.width(width = 12.dp))
        }
        Text(
            text = text,
            textAlign = TextAlign.Start,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun quantvaultInfoCallout_preview() {
    quantvaultInfoCalloutCard(
        text = "text",
    )
}







package com.quantvault.ui.platform.components.support

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Component for displaying supporting text within a card, usually at the bottom.
 *
 * @param text Generic text to be displayed.
 * @param cardStyle The card style to be applied.
 * @param modifier The modifier to be applied.
 * @param insets Insets to be applied to within the card. This value will be applied even if the
 * [CardStyle] is null.
 * @param onClick The callback to be invoked when the supporting content is clicked.
 */
@Composable
fun quantvaultSupportingText(
    text: String,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    insets: PaddingValues = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = 12.dp,
        bottom = 12.dp,
    ),
    onClick: (() -> Unit)? = null,
) {
    quantvaultSupportingContent(
        cardStyle = cardStyle,
        modifier = modifier,
        insets = insets,
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = QuantVaultTheme.typography.bodySmall,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}







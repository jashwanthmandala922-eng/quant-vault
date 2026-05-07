package com.quantvault.ui.platform.components.card

import android.content.res.Configuration
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.base.util.toDp
import com.quantvault.ui.platform.components.badge.NotificationBadge
import com.quantvault.ui.platform.components.button.quantvaultFilledButton
import com.quantvault.ui.platform.components.button.quantvaultStandardIconButton
import com.quantvault.ui.platform.components.button.quantvaultTextButton
import com.quantvault.ui.platform.components.button.model.quantvaultButtonData
import com.quantvault.ui.platform.components.card.color.quantvaultCardColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.util.asText

/**
 * A design component action card, which contains a title, action button, and a dismiss button
 * by default, with optional leading icon content.
 *
 * @param cardTitle The title of the card.
 * @param actionText The text content on the CTA button.
 * @param onActionClick The action to perform when the CTA button is clicked.
 * @param onDismissClick Optional action to perform when the dismiss button is clicked.
 * @param cardSubtitle The subtitle of the card.
 * @param secondaryButton The optional data for a secondary button.
 * @param leadingContent Optional content to display on the leading side of the [cardTitle] [Text].
 */
@Suppress("LongMethod")
@Composable
fun quantvaultActionCard(
    cardTitle: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissClick: (() -> Unit)? = null,
    cardSubtitle: String? = null,
    secondaryButton: quantvaultButtonData? = null,
    leadingContent: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = QuantVaultTheme.shapes.actionCard,
        colors = quantvaultCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        border = BorderStroke(width = 1.dp, color = QuantVaultTheme.colorScheme.stroke.border),
    ) {
        var rowBottomPx by remember { mutableIntStateOf(0) }
        var titleBottomPx by remember { mutableIntStateOf(0) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    rowBottomPx = it.positionInParent().y.toInt() + it.size.height
                },
        ) {
            Spacer(modifier = Modifier.width(width = 16.dp))
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(weight = 1f),
            ) {
                leadingContent?.let {
                    it()
                    Spacer(modifier = Modifier.width(width = 12.dp))
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = cardTitle,
                        style = QuantVaultTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                titleBottomPx = it.positionInParent().y.toInt() + it.size.height
                            },
                    )
                    cardSubtitle?.let {
                        Spacer(modifier = Modifier.height(height = 4.dp))
                        Text(
                            text = it,
                            style = QuantVaultTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            onDismissClick?.let {
                quantvaultStandardIconButton(
                    painter = rememberVectorPainter(id = quantvaultDrawable.ic_close),
                    contentDescription = stringResource(id = quantvaultString.close),
                    onClick = it,
                )
            }
        }
        if (cardSubtitle == null && rowBottomPx > titleBottomPx) {
            // When the subtitle is missing, we want to ensure that the filled button is 16dp below
            // the title but the close button can be taller than the title which will push the
            // button further down. So we measure the difference and use that to offset the spacer
            // size.
            Spacer(
                modifier = Modifier.height(height = 16.dp - (rowBottomPx - titleBottomPx).toDp()),
            )
        } else {
            Spacer(modifier = Modifier.height(height = 16.dp))
        }
        quantvaultFilledButton(
            label = actionText,
            onClick = onActionClick,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
        secondaryButton?.let {
            quantvaultTextButton(
                buttonData = it,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(height = 16.dp))
    }
}

/**
 * A default exit animation for [quantvaultActionCard] when using an animation wrapper like
 * [androidx.compose.animation.AnimatedVisibility].
 */
fun actionCardExitAnimation() = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun quantvaultActionCardWithSubtitleNoDismiss_preview() {
    QuantVaultTheme {
        quantvaultActionCard(
            cardTitle = "Title",
            actionText = "Action",
            onActionClick = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun quantvaultActionCard_preview() {
    QuantVaultTheme {
        quantvaultActionCard(
            cardTitle = "Title",
            actionText = "Action",
            onActionClick = {},
            onDismissClick = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun quantvaultActionCardWithLeadingContent_preview() {
    QuantVaultTheme {
        quantvaultActionCard(
            cardTitle = "Title",
            actionText = "Action",
            onActionClick = {},
            onDismissClick = {},
            leadingContent = {
                NotificationBadge(
                    notificationCount = 1,
                )
            },
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun quantvaultActionCardWithSubtitle_preview() {
    QuantVaultTheme {
        quantvaultActionCard(
            cardTitle = "Title",
            cardSubtitle = "Subtitle",
            actionText = "Action",
            onActionClick = {},
            onDismissClick = {},
            secondaryButton = quantvaultButtonData(
                label = "Learn More".asText(),
                onClick = {},
            ),
            leadingContent = {
                NotificationBadge(
                    notificationCount = 1,
                )
            },
        )
    }
}







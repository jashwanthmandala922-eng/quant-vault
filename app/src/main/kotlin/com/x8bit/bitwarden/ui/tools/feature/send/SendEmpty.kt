package com.x8bit.bitwarden.ui.tools.feature.send

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toAnnotatedString
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.card.QuantVaultInfoCalloutCard
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Content for the empty state of the [SendScreen].
 */
@Suppress("LongMethod")
@Composable
fun SendEmpty(
    policyDisablesSend: Boolean,
    onAddItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        if (policyDisablesSend) {
            Spacer(modifier = Modifier.height(12.dp))
            QuantVaultInfoCalloutCard(
                text = stringResource(id = R.string.send_disabled_warning),
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.weight(1F))
        Image(
            painter = rememberVectorPainter(R.drawable.ill_send),
            contentDescription = null,
            modifier = Modifier
                .standardHorizontalMargin()
                .size(100.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.send_sensitive_information_safely),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(
                R.string.share_files_and_data_securely_with_anyone_on_any_platform,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .testTag("EmptySendListText"),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // This button is hidden from accessibility to avoid duplicate voice over with the FAB
        val newSendLabel = stringResource(id = R.string.add_a_send)
        QuantVaultFilledButton(
            onClick = onAddItemClick,
            label = stringResource(id = R.string.add_a_send),
            icon = rememberVectorPainter(R.drawable.ic_plus_small),
            modifier = Modifier
                .clearAndSetSemantics {
                    text = newSendLabel.toAnnotatedString()
                    hideFromAccessibility()
                }
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.weight(1F))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview(name = "Light mode")
@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SendEmpty_preview() {
    QuantVaultTheme {
        Column(
            modifier = Modifier.background(QuantVaultTheme.colorScheme.background.primary),
        ) {
            SendEmpty(
                policyDisablesSend = false,
                onAddItemClick = {},
            )
        }
    }
}

@Preview(name = "Light mode")
@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SendEmptyPolicyDisabled_preview() {
    QuantVaultTheme {
        Column(
            modifier = Modifier.background(QuantVaultTheme.colorScheme.background.primary),
        ) {
            SendEmpty(
                policyDisablesSend = true,
                onAddItemClick = {},
            )
        }
    }
}







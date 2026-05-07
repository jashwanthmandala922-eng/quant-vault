package com.x8bit.bitwarden.ui.vault.feature.importlogins.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toAnnotatedString
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.card.QuantVaultContentCard
import com.bitwarden.ui.platform.components.content.model.ContentBlockData
import com.bitwarden.ui.platform.components.text.QuantVaultHyperTextLink
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.x8bit.bitwarden.R

/**
 * Reusable component for each step of the import logins flow.
 */
@Suppress("LongMethod")
@Composable
fun ImportLoginsInstructionStep(
    stepText: String,
    stepTitle: String,
    instructions: ImmutableList<ContentBlockData>,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
    ctaText: String = stringResource(R.string.continue_text),
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stepText,
            style = QuantVaultTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(12.dp))
        Text(text = stepTitle, style = QuantVaultTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))
        QuantVaultContentCard(
            contentItems = instructions,
            modifier = Modifier
                .standardHorizontalMargin(),
            contentHeaderTextStyle = QuantVaultTheme.typography.bodyMedium,
            contentSubtitleTextStyle = QuantVaultTheme.typography.labelSmall,
        )
        Spacer(Modifier.height(24.dp))
        QuantVaultHyperTextLink(
            annotatedResId = R.string.need_help_check_out_import_help,
            annotationKey = "importHelp",
            accessibilityString = stringResource(id = R.string.import_help),
            onClick = onHelpClick,
            style = QuantVaultTheme.typography.bodySmall,
            isExternalLink = true,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(Modifier.height(24.dp))
        QuantVaultFilledButton(
            label = ctaText,
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(Modifier.height(12.dp))
        QuantVaultOutlinedButton(
            label = stringResource(R.string.back),
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Preview
@Composable
private fun ImportLoginsInstructionStep_preview() {
    QuantVaultTheme {
        Column(modifier = Modifier.background(QuantVaultTheme.colorScheme.background.primary)) {
            ImportLoginsInstructionStep(
                stepText = "Step text",
                stepTitle = "Step title",
                instructions = persistentListOf(
                    ContentBlockData(
                        iconVectorResource = R.drawable.ic_number1,
                        headerText = buildAnnotatedString {
                            append("Step text 1")
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuantVaultTheme.typography.bodyMedium.fontFamily,
                                ),
                            ) {
                                append(" with bold text")
                            }
                        },
                        subtitleText = null,
                    ),
                    ContentBlockData(
                        iconVectorResource = R.drawable.ic_number2,
                        headerText = buildAnnotatedString {
                            append("Step text 2")
                        },
                        subtitleText = "Added deets".toAnnotatedString(),
                    ),
                    ContentBlockData(
                        iconVectorResource = R.drawable.ic_number3,
                        headerText = buildAnnotatedString {
                            append("Step text 3")
                        },
                    ),
                ),
                onBackClick = {},
                onContinueClick = {},
                onHelpClick = {},
            )
        }
    }
}







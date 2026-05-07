package com.x8bit.bitwarden.ui.vault.feature.item.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.annotatedStringResource
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.base.util.spanStyleOf
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.ui.vault.feature.item.VaultItemState
import com.x8bit.bitwarden.R

/**
 * Attachment UI common for all item types.
 */
@Suppress("LongMethod")
@Composable
fun VaultItemAttachment(
    attachmentItem: VaultItemState.ViewState.Content.Common.AttachmentItem,
    onAttachmentDownloadClick: (VaultItemState.ViewState.Content.Common.AttachmentItem) -> Unit,
    onAttachmentPreviewClick: (VaultItemState.ViewState.Content.Common.AttachmentItem) -> Unit,
    onUpgradeToPremiumClick: () -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    var shouldShowPremiumWarningDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowSizeWarningDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                padding = PaddingValues(start = 16.dp),
                onClick = {
                    if (!attachmentItem.isDownloadAllowed) {
                        shouldShowPremiumWarningDialog = true
                        return@cardStyle
                    }
                    onAttachmentPreviewClick(attachmentItem)
                },
            )
            .testTag("CipherAttachment"),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = attachmentItem.title,
                color = QuantVaultTheme.colorScheme.text.primary,
                style = QuantVaultTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .testTag("AttachmentNameLabel"),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = attachmentItem.displaySize,
                    color = QuantVaultTheme.colorScheme.text.secondary,
                    style = QuantVaultTheme.typography.bodyMedium,
                    modifier = Modifier
                        .testTag("AttachmentSizeLabel"),
                )

                Spacer(modifier = Modifier.width(8.dp))

                QuantVaultStandardIconButton(
                    vectorIconRes = R.drawable.ic_download,
                    contentDescription = stringResource(id = R.string.download),
                    isExternalLink = true,
                    onClick = {
                        if (!attachmentItem.isDownloadAllowed) {
                            shouldShowPremiumWarningDialog = true
                            return@QuantVaultStandardIconButton
                        }

                        if (attachmentItem.isLargeFile) {
                            shouldShowSizeWarningDialog = true
                            return@QuantVaultStandardIconButton
                        }

                        onAttachmentDownloadClick(attachmentItem)
                    },
                    modifier = Modifier
                        .testTag("AttachmentDownloadButton"),
                )
            }
        }
    }

    if (shouldShowPremiumWarningDialog) {
        QuantVaultTwoButtonDialog(
            title = stringResource(id = R.string.attachments_unavailable),
            message = stringResource(id = R.string.attachments_are_a_premium_feature),
            confirmButtonText = stringResource(id = R.string.upgrade_to_premium),
            dismissButtonText = stringResource(id = R.string.cancel),
            onConfirmClick = {
                shouldShowPremiumWarningDialog = false
                onUpgradeToPremiumClick()
            },
            onDismissClick = { shouldShowPremiumWarningDialog = false },
            onDismissRequest = { shouldShowPremiumWarningDialog = false },
        )
    }

    if (shouldShowSizeWarningDialog) {
        QuantVaultTwoButtonDialog(
            title = stringResource(id = R.string.download_attachment),
            message = annotatedStringResource(
                id = R.string.attachment_large_warning,
                args = arrayOf(attachmentItem.displaySize),
                style = spanStyleOf(
                    color = QuantVaultTheme.colorScheme.text.primary,
                    textStyle = QuantVaultTheme.typography.bodyMedium,
                ),
            ),
            confirmButtonText = stringResource(R.string.yes),
            dismissButtonText = stringResource(R.string.no),
            onConfirmClick = {
                shouldShowSizeWarningDialog = false
                onAttachmentDownloadClick(attachmentItem)
            },
            onDismissClick = { shouldShowSizeWarningDialog = false },
            onDismissRequest = { shouldShowSizeWarningDialog = false },
        )
    }
}







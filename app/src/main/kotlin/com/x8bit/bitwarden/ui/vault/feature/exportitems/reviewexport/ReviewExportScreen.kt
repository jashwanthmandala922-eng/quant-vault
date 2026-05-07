package com.x8bit.bitwarden.ui.vault.feature.exportitems.reviewexport

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.cxf.manager.CredentialExchangeCompletionManager
import com.quantvault.cxf.ui.composition.LocalCredentialExchangeCompletionManager
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.base.util.nullableTestTag
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.button.model.QuantVaultButtonData
import com.bitwarden.ui.platform.components.content.QuantVaultEmptyContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.vault.feature.exportitems.component.ExportItemsScaffold
import com.x8bit.bitwarden.ui.vault.feature.exportitems.reviewexport.handlers.ReviewExportHandlers
import com.x8bit.bitwarden.ui.vault.feature.exportitems.reviewexport.handlers.rememberReviewExportHandler
import com.x8bit.bitwarden.R

/**
 * The main composable for the Review Export screen.
 *
 * This screen allows the user to review a summary of items that will be exported
 * before confirming the operation. It displays a list of item types and their counts,
 * an illustrative image, and provides options to proceed with the export or cancel.
 * The screen adheres to the MVI pattern by observing state from [ReviewExportViewModel]
 * and dispatching actions via [ReviewExportHandlers]. Upon completion of the export operation,
 * it utilizes the [CredentialExchangeCompletionManager] to finalize the credential exchange
 * process.
 *
 * @param onNavigateBack Callback invoked when the user chooses to navigate back (e.g., via cancel
 * or back button).
 * @param viewModel The [ReviewExportViewModel] instance for this screen.
 * @param credentialExchangeCompletionManager Manager responsible for completing the credential
 * export process.
 * Defaults to the manager provided by [LocalCredentialExchangeCompletionManager].
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewExportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccountSelection: () -> Unit,
    viewModel: ReviewExportViewModel = hiltViewModel(),
    credentialExchangeCompletionManager: CredentialExchangeCompletionManager =
        LocalCredentialExchangeCompletionManager.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handler = rememberReviewExportHandler(viewModel)

    EventsEffect(viewModel) {
        when (it) {
            is ReviewExportEvent.NavigateBack -> onNavigateBack()
            is ReviewExportEvent.NavigateToAccountSelection -> onNavigateToAccountSelection()
            is ReviewExportEvent.CompleteExport -> {
                credentialExchangeCompletionManager.completeCredentialExport(it.result)
            }
        }
    }

    ReviewExportDialogs(
        dialog = state.dialog,
        onDismiss = handler.onDismissDialog,
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    ExportItemsScaffold(
        navIcon = rememberVectorPainter(R.drawable.ic_back),
        onNavigationIconClick = handler.onNavigateBackClick,
        navigationIconContentDescription = stringResource(R.string.back),
        scrollBehavior = scrollBehavior,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
    ) {
        when (val viewState = state.viewState) {
            ReviewExportState.ViewState.NoItems -> {
                QuantVaultEmptyContent(
                    title = stringResource(R.string.no_items_available_to_import),
                    text = stringResource(
                        R.string.your_vault_may_be_empty_or_import_some_item_types_isnt_supported,
                    ),
                    primaryButton = if (state.hasOtherAccounts) {
                        QuantVaultButtonData(
                            label = R.string.select_a_different_account.asText(),
                            testTag = "SelectADifferentAccountButton",
                            onClick = handler.onSelectAnotherAccountClick,
                        )
                    } else {
                        null
                    },
                    secondaryButton = QuantVaultButtonData(
                        label = R.string.cancel.asText(),
                        testTag = "NoItemsCancelButton",
                        onClick = handler.onCancelClick,
                    ),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is ReviewExportState.ViewState.Content -> {
                ReviewExportContent(
                    content = viewState,
                    onImportItemsClick = handler.onImportItemsClick,
                    onCancelClick = handler.onCancelClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .standardHorizontalMargin(),
                )
            }
        }
    }
}

/**
 * Displays dialogs based on the [ReviewExportState.DialogState].
 *
 * @param dialog The current dialog state from [ReviewExportState].
 * @param onDismiss Callback invoked when a dialog is dismissed.
 */
@Composable
private fun ReviewExportDialogs(
    dialog: ReviewExportState.DialogState?,
    onDismiss: () -> Unit,
) {
    when (dialog) {
        is ReviewExportState.DialogState.General -> {
            QuantVaultBasicDialog(
                title = dialog.title(),
                message = dialog.message(),
                throwable = dialog.error,
                onDismissRequest = onDismiss,
            )
        }

        is ReviewExportState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }
}

/**
 * The main content area of the Review Export screen.
 *
 * This composable lays out the illustrative image, titles, list of items to export,
 * and action buttons.
 *
 * @param content The current [ReviewExportState] to render.
 * @param onImportItemsClick Callback invoked when the "Import Items" button is clicked.
 * @param onCancelClick Callback invoked when the "Cancel" button is clicked.
 * @param modifier The modifier to be applied to the content root.
 */
@Suppress("LongMethod")
@Composable
private fun ReviewExportContent(
    content: ReviewExportState.ViewState.Content,
    onImportItemsClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 16.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ill_import_logins),
            contentDescription = null,
            modifier = Modifier.height(160.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.import_items),
            style = QuantVaultTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = QuantVaultTheme.colorScheme.text.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.import_passwords_passkeys_and_other_item_types_from_your_vault,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultListHeaderText(
            label = stringResource(R.string.items_to_import),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        ItemCountRow(
            label = stringResource(R.string.passwords).asText(),
            itemCount = content.itemTypeCounts.passwordCount,
            cardStyle = CardStyle.Top(),
        )
        ItemCountRow(
            label = stringResource(R.string.passkeys).asText(),
            itemCount = content.itemTypeCounts.passkeyCount,
            cardStyle = CardStyle.Middle(),
        )
        ItemCountRow(
            label = stringResource(R.string.identities).asText(),
            itemCount = content.itemTypeCounts.identityCount,
            cardStyle = CardStyle.Middle(),
        )
        ItemCountRow(
            label = stringResource(R.string.cards).asText(),
            itemCount = content.itemTypeCounts.cardCount,
            cardStyle = CardStyle.Middle(),
        )
        ItemCountRow(
            label = stringResource(R.string.secure_notes).asText(),
            itemCount = content.itemTypeCounts.secureNoteCount,
            cardStyle = CardStyle.Bottom,
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultFilledButton(
            label = stringResource(R.string.import_items),
            onClick = onImportItemsClick,
            isExternalLink = true,
            modifier = Modifier
                .fillMaxWidth()
                .nullableTestTag("ImportItemsButton"),
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuantVaultOutlinedButton(
            label = stringResource(R.string.cancel),
            onClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .nullableTestTag("CancelButton"),
        )

        Spacer(Modifier.height(12.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
}

/**
 * Displays a single row in the list of items to be exported.
 *
 * @param label The display name of the item type.
 * @param itemCount The number of items of this type that are staged for export.
 */
@Composable
private fun ItemCountRow(
    label: Text,
    itemCount: Int,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                paddingHorizontal = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = label(),
            style = QuantVaultTheme.typography.bodyLarge,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = itemCount.toString(),
            style = QuantVaultTheme.typography.labelSmall,
            color = QuantVaultTheme.colorScheme.text.primary,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Review Export Content")
@Composable
private fun ReviewExportContent_preview() {
    ExportItemsScaffold(
        navIcon = rememberVectorPainter(R.drawable.ic_close),
        navigationIconContentDescription = stringResource(R.string.close),
        onNavigationIconClick = { },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
    ) {
        ReviewExportContent(
            content = ReviewExportState.ViewState.Content(
                itemTypeCounts = ReviewExportState.ItemTypeCounts(
                    passwordCount = 14,
                    passkeyCount = 14,
                    identityCount = 3,
                    secureNoteCount = 5,
                ),
            ),
            onImportItemsClick = {},
            onCancelClick = {},
            modifier = Modifier
                .fillMaxSize()
                .standardHorizontalMargin(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Review Export Empty Content")
@Composable
private fun ReviewExportContent_NoItems_preview() {
    ExportItemsScaffold(
        navIcon = rememberVectorPainter(R.drawable.ic_close),
        navigationIconContentDescription = stringResource(R.string.close),
        onNavigationIconClick = { },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
    ) {
        QuantVaultEmptyContent(
            title = stringResource(R.string.no_items_available_to_import),
            text = stringResource(
                R.string.your_vault_may_be_empty_or_import_some_item_types_isnt_supported,
            ),
            primaryButton = QuantVaultButtonData(
                label = R.string.select_a_different_account.asText(),
                testTag = "SelectADifferentAccountButton",
                onClick = { },
            ),
            secondaryButton = QuantVaultButtonData(
                label = R.string.cancel.asText(),
                testTag = "NoItemsCancelButton",
                onClick = { },
            ),
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}








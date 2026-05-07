package com.x8bit.bitwarden.ui.vault.feature.importitems

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.cxf.importer.CredentialExchangeImporter
import com.quantvault.cxf.ui.composition.LocalCredentialExchangeImporter
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.row.QuantVaultTextRow
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.vault.feature.importitems.ImportItemsAction.ImportCredentialSelectionReceive
import com.x8bit.bitwarden.ui.vault.feature.importitems.handlers.rememberImportItemsHandler
import kotlinx.coroutines.launch
import com.x8bit.bitwarden.R

/**
 * Top level component for the import items screen.
 */
@Suppress("LongMethod")
@Composable
fun ImportItemsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToImportFromComputer: () -> Unit,
    viewModel: ImportItemsViewModel = hiltViewModel(),
    credentialExchangeImporter: CredentialExchangeImporter =
        LocalCredentialExchangeImporter.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val handler = rememberImportItemsHandler(viewModel = viewModel)
    val snackbarHostState = rememberQuantVaultSnackbarHostState()

    EventsEffect(viewModel) { event ->
        when (event) {
            ImportItemsEvent.NavigateBack -> onNavigateBack()
            ImportItemsEvent.NavigateToImportFromComputer -> onNavigateToImportFromComputer()
            is ImportItemsEvent.ShowRegisteredImportSources -> {
                coroutineScope.launch {
                    viewModel.trySendAction(
                        action = ImportCredentialSelectionReceive(
                            selectionResult = credentialExchangeImporter
                                .importCredentials(
                                    credentialTypes = event.credentialTypes,
                                ),
                        ),
                    )
                }
            }

            is ImportItemsEvent.ShowBasicSnackbar -> {
                snackbarHostState.showSnackbar(event.data)
            }

            is ImportItemsEvent.ShowSyncFailedSnackbar -> {
                snackbarHostState.showSnackbar(
                    snackbarData = event.data,
                    onActionPerformed = handler.onSyncFailedTryAgainClick,
                )
            }
        }
    }

    ImportItemsDialogs(
        dialog = state.dialog,
        onDismissDialog = handler.onDismissDialog,
    )

    ImportItemsScaffold(
        onNavigateBack = handler.onNavigateBack,
        onImportFromComputerClick = handler.onImportFromComputerClick,
        onImportFromAnotherAppClick = handler.onImportFromAnotherAppClick,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportItemsScaffold(
    onNavigateBack: () -> Unit,
    onImportFromComputerClick: () -> Unit,
    onImportFromAnotherAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: QuantVaultSnackbarHostState = rememberQuantVaultSnackbarHostState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(R.string.import_items),
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(R.drawable.ic_back),
                    onNavigationIconClick = onNavigateBack,
                    navigationIconContentDescription = stringResource(R.string.back),
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            QuantVaultSnackbarHost(
                QuantVaultHostState = snackbarHostState,
            )
        },
    ) {
        ImportItemsContent(
            onImportFromComputerClick = onImportFromComputerClick,
            onImportFromAnotherAppClick = onImportFromAnotherAppClick,
            modifier = Modifier
                .fillMaxSize()
                .standardHorizontalMargin(),
        )
    }
}

@Composable
private fun ImportItemsContent(
    onImportFromComputerClick: () -> Unit,
    onImportFromAnotherAppClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {
        item { Spacer(Modifier.height(12.dp)) }

        item {
            QuantVaultTextRow(
                text = stringResource(R.string.import_from_computer),
                onClick = onImportFromComputerClick,
                cardStyle = CardStyle.Top(),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            QuantVaultTextRow(
                text = stringResource(R.string.import_from_another_app),
                onClick = onImportFromAnotherAppClick,
                isExternalLink = true,
                cardStyle = CardStyle.Bottom,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun ImportItemsDialogs(
    dialog: ImportItemsState.DialogState?,
    onDismissDialog: () -> Unit,
) {
    when (dialog) {
        is ImportItemsState.DialogState.General -> {
            QuantVaultBasicDialog(
                title = dialog.title(),
                message = dialog.message(),
                onDismissRequest = onDismissDialog,
                throwable = dialog.throwable,
            )
        }

        is ImportItemsState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }
}

//region Previews

@Preview(showBackground = true, name = "Initial state")
@Composable
private fun ImportItemsContent_preview() {
    QuantVaultTheme {
        ImportItemsScaffold(
            onNavigateBack = {},
            onImportFromComputerClick = {},
            onImportFromAnotherAppClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading dialog")
@Composable
private fun ImportItemsDialogs_loading_preview() {
    QuantVaultTheme {
        QuantVaultScaffold {
            ImportItemsDialogs(
                dialog = ImportItemsState.DialogState.Loading("Decoding items...".asText()),
                onDismissDialog = {},
            )
        }
    }
}
//endregion Previews







package com.quantvault.authenticator.ui.platform.feature.settings.export

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.authenticator.ui.platform.feature.settings.export.model.ExportVaultFormat
import com.quantvault.authenticator.ui.platform.util.displayLabel
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.components.appbar.QuantVaultTopAppBar
import com.quantvault.ui.platform.components.button.QuantVaultFilledButton
import com.quantvault.ui.platform.components.dialog.QuantVaultBasicDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.quantvault.ui.platform.components.dropdown.QuantVaultMultiSelectButton
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.quantvault.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.quantvault.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.quantvault.ui.platform.composition.LocalIntentManager
import com.quantvault.ui.platform.manager.IntentManager
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme
import kotlinx.collections.immutable.toImmutableList

/**
 * Top level composable for the export data screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: ExportViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val exportLocationReceive: (Uri) -> Unit = {
        viewModel.trySendAction(ExportAction.ExportLocationReceive(it))
    }
    val fileSaveLauncher = intentManager.getActivityResultLauncher { activityResult ->
        intentManager.getFileDataFromActivityResult(activityResult)?.let {
            exportLocationReceive.invoke(it.uri)
        }
    }
    val snackbarState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            ExportEvent.NavigateBack -> onNavigateBack()
            is ExportEvent.ShowSnackBar -> {
                snackbarState.showSnackbar(QuantVaultSnackbarData(message = event.message))
            }

            is ExportEvent.NavigateToSelectExportDestination -> {
                fileSaveLauncher.launch(
                    intentManager.createDocumentIntent(
                        fileName = event.fileName,
                    ),
                )
            }
        }
    }

    var shouldShowConfirmationPrompt by remember { mutableStateOf(false) }
    val confirmExportClick = { viewModel.trySendAction(ExportAction.ConfirmExportClick) }
    if (shouldShowConfirmationPrompt) {
        QuantVaultTwoButtonDialog(
            title = stringResource(id = QuantVaultString.export_confirmation_title),
            message = stringResource(
                id = QuantVaultString.export_vault_warning,
            ),
            confirmButtonText = stringResource(id = QuantVaultString.export),
            dismissButtonText = stringResource(id = QuantVaultString.cancel),
            onConfirmClick = {
                shouldShowConfirmationPrompt = false
                confirmExportClick()
            },
            onDismissClick = { shouldShowConfirmationPrompt = false },
            onDismissRequest = { shouldShowConfirmationPrompt = false },
        )
    }

    when (val dialog = state.dialogState) {
        is ExportState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialog.title?.invoke(),
                message = dialog.message(),
                onDismissRequest = { viewModel.trySendAction(ExportAction.DialogDismiss) },
            )
        }

        is ExportState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = QuantVaultString.export),
                scrollBehavior = scrollBehavior,
                navigationIcon = painterResource(id = QuantVaultDrawable.ic_back),
                navigationIconContentDescription = stringResource(id = QuantVaultString.back),
                onNavigationIconClick = { viewModel.trySendAction(ExportAction.CloseButtonClick) },
            )
        },
        snackbarHost = { QuantVaultSnackbarHost(snackbarState) },
    ) {
        ExportScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onExportFormatOptionSelected = {
                viewModel.trySendAction(ExportAction.ExportFormatOptionSelect(it))
            },
            onExportClick = { shouldShowConfirmationPrompt = true },
        )
    }
}

@Composable
private fun ExportScreenContent(
    modifier: Modifier = Modifier,
    state: ExportState,
    onExportFormatOptionSelected: (ExportVaultFormat) -> Unit,
    onExportClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        val resources = LocalResources.current
        Spacer(modifier = Modifier.height(height = 24.dp))

        Text(
            text = stringResource(id = QuantVaultString.included_in_this_export),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))

        Text(
            text = stringResource(
                id = QuantVaultString.only_codes_stored_locally_on_this_device_will_be_exported,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 24.dp))

        QuantVaultMultiSelectButton(
            label = stringResource(id = QuantVaultString.file_format),
            options = ExportVaultFormat.entries.map { it.displayLabel() }.toImmutableList(),
            selectedOption = state.exportVaultFormat.displayLabel(),
            onOptionSelected = { selectedOptionLabel ->
                val selectedOption = ExportVaultFormat
                    .entries
                    .first { it.displayLabel(resources) == selectedOptionLabel }
                onExportFormatOptionSelected(selectedOption)
            },
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .testTag("FileFormatPicker")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 24.dp))

        QuantVaultFilledButton(
            label = stringResource(id = QuantVaultString.export),
            onClick = onExportClick,
            isExternalLink = true,
            modifier = Modifier
                .testTag("ExportVaultButton")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}





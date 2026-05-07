package com.x8bit.bitwarden.ui.vault.feature.attachments.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.annotatedStringResource
import com.bitwarden.ui.platform.base.util.spanStyleOf
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.button.model.QuantVaultButtonData
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.icon.model.IconData
import com.bitwarden.ui.platform.components.preview.ImagePreviewContent
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.R

/**
 * Displays the preview attachment screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewAttachmentScreen(
    viewModel: PreviewAttachmentViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val fileChooserLauncher = intentManager.getActivityResultLauncher { activityResult ->
        val action = intentManager
            .getFileDataFromActivityResult(activityResult)
            ?.let { PreviewAttachmentAction.AttachmentFileLocationReceive(it.uri) }
            ?: PreviewAttachmentAction.NoAttachmentFileLocationReceive
        viewModel.trySendAction(action)
    }
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            PreviewAttachmentEvent.NavigateBack -> onNavigateBack()
            is PreviewAttachmentEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
            is PreviewAttachmentEvent.NavigateToSelectAttachmentSaveLocation -> {
                fileChooserLauncher.launch(intentManager.createDocumentIntent(event.fileName))
            }
        }
    }

    PreviewAttachmentDialogs(
        dialogState = state.dialogState,
        onDismissRequest = { viewModel.trySendAction(PreviewAttachmentAction.DismissDialog) },
        onCloseClick = { viewModel.trySendAction(PreviewAttachmentAction.CloseClick) },
        onConfirmDownloadClick = {
            viewModel.trySendAction(PreviewAttachmentAction.ConfirmDownloadClick)
        },
    )

    QuantVaultScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            QuantVaultTopAppBar(
                title = state.fileName,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                    navigationIconContentDescription = stringResource(id = R.string.back),
                    onNavigationIconClick = {
                        viewModel.trySendAction(PreviewAttachmentAction.BackClick)
                    },
                ),
                actions = {
                    QuantVaultStandardIconButton(
                        vectorIconRes = R.drawable.ic_download,
                        contentDescription = stringResource(id = R.string.download),
                        onClick = {
                            viewModel.trySendAction(PreviewAttachmentAction.DownloadClick)
                        },
                        isExternalLink = true,
                        modifier = Modifier.testTag("ToolbarDownloadButton"),
                    )
                },
            )
        },
        snackbarHost = {
            QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState)
        },
    ) {
        when (val viewState = state.viewState) {
            is PreviewAttachmentState.ViewState.Content -> {
                ImagePreviewContent(
                    file = viewState.file,
                    onMissingFile = {
                        viewModel.trySendAction(PreviewAttachmentAction.FileMissing)
                    },
                    onLoaded = {
                        viewModel.trySendAction(PreviewAttachmentAction.BitmapRenderComplete)
                    },
                    onError = {
                        viewModel.trySendAction(PreviewAttachmentAction.BitmapRenderError)
                    },
                )
            }

            is PreviewAttachmentState.ViewState.Error -> QuantVaultErrorContent(
                message = viewState.message(),
                illustrationData = IconData.Local(iconRes = viewState.illustrationRes),
                buttonData = QuantVaultButtonData(
                    label = R.string.download_file.asText(),
                    icon = rememberVectorPainter(id = R.drawable.ic_download),
                    onClick = { viewModel.trySendAction(PreviewAttachmentAction.DownloadClick) },
                    isExternalLink = true,
                    testTag = "ErrorStateDownloadButton",
                ),
                modifier = Modifier.fillMaxSize(),
            )

            is PreviewAttachmentState.ViewState.Loading -> QuantVaultLoadingContent(
                text = viewState.message(),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PreviewAttachmentDialogs(
    dialogState: PreviewAttachmentState.DialogState?,
    onDismissRequest: () -> Unit,
    onCloseClick: () -> Unit,
    onConfirmDownloadClick: () -> Unit,
) {
    when (dialogState) {
        is PreviewAttachmentState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = onDismissRequest,
            )
        }

        is PreviewAttachmentState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialogState.message())
        }

        PreviewAttachmentState.DialogState.PreviewUnavailable -> {
            QuantVaultBasicDialog(
                title = stringResource(id = R.string.preview_unavailable),
                message = stringResource(
                    id = R.string.Quant Vault_could_not_decrypt_this_file_so_the_preview_cannot_be_displayed,
                ),
                confirmButtonLabel = stringResource(id = R.string.close),
                onDismissRequest = onCloseClick,
            )
        }

        is PreviewAttachmentState.DialogState.DownloadLargeFileConfirmation -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.download_attachment),
                message = annotatedStringResource(
                    id = R.string.attachment_large_warning,
                    args = arrayOf(dialogState.displaySize),
                    style = spanStyleOf(
                        color = QuantVaultTheme.colorScheme.text.primary,
                        textStyle = QuantVaultTheme.typography.bodyMedium,
                    ),
                ),
                confirmButtonText = stringResource(id = R.string.yes),
                dismissButtonText = stringResource(id = R.string.no),
                onConfirmClick = onConfirmDownloadClick,
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        null -> Unit
    }
}








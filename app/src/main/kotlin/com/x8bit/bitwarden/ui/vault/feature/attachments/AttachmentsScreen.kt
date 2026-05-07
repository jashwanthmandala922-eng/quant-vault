package com.x8bit.bitwarden.ui.vault.feature.attachments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.x8bit.bitwarden.ui.vault.feature.attachments.handlers.AttachmentsHandlers
import com.x8bit.bitwarden.ui.vault.feature.attachments.preview.PreviewAttachmentRoute
import com.x8bit.bitwarden.R

/**
 * Displays the attachments screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentsScreen(
    viewModel: AttachmentsViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    onNavigateBack: () -> Unit,
    onNavigateToPreview: (route: PreviewAttachmentRoute) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val attachmentsHandlers = remember(viewModel) { AttachmentsHandlers.create(viewModel) }
    val fileChooserLauncher = intentManager.getActivityResultLauncher { activityResult ->
        intentManager.getFileDataFromActivityResult(activityResult)?.let {
            attachmentsHandlers.onFileChoose(it)
        }
    }
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            AttachmentsEvent.NavigateBack -> onNavigateBack()
            is AttachmentsEvent.NavigateToUri -> intentManager.launchUri(event.uri.toUri())
            AttachmentsEvent.ShowChooserSheet -> {
                fileChooserLauncher.launch(
                    intentManager.createFileChooserIntent(withCameraIntents = false),
                )
            }

            is AttachmentsEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
            is AttachmentsEvent.NavigateToPreview -> {
                onNavigateToPreview(
                    PreviewAttachmentRoute(
                        cipherId = event.cipherId,
                        attachmentId = event.attachmentId,
                        fileName = event.fileName,
                        displaySize = event.displaySize,
                        isLargeFile = event.isLargeFile,
                    ),
                )
            }
        }
    }

    AttachmentsDialogs(
        dialogState = state.dialogState,
        attachmentsHandlers = attachmentsHandlers,
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.attachments),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                    navigationIconContentDescription = stringResource(id = R.string.back),
                    onNavigationIconClick = attachmentsHandlers.onBackClick,
                ),
                actions = {
                    QuantVaultTextButton(
                        label = stringResource(id = R.string.save),
                        onClick = attachmentsHandlers.onSaveClick,
                        modifier = Modifier.testTag("SaveButton"),
                    )
                },
            )
        },
        snackbarHost = {
            QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState)
        },
    ) {
        when (val viewState = state.viewState) {
            is AttachmentsState.ViewState.Content -> AttachmentsContent(
                viewState = viewState,
                attachmentsHandlers = attachmentsHandlers,
                isAttachmentUpdatesEnabled = state.isAttachmentUpdatesEnabled,
                modifier = Modifier.fillMaxSize(),
            )

            is AttachmentsState.ViewState.Error -> QuantVaultErrorContent(
                message = viewState.message(),
                modifier = Modifier.fillMaxSize(),
            )

            AttachmentsState.ViewState.Loading -> QuantVaultLoadingContent(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun AttachmentsDialogs(
    dialogState: AttachmentsState.DialogState?,
    attachmentsHandlers: AttachmentsHandlers,
) {
    when (dialogState) {
        AttachmentsState.DialogState.RequiresPremium -> QuantVaultTwoButtonDialog(
            title = stringResource(id = R.string.attachments_unavailable),
            message = stringResource(id = R.string.attachments_are_a_premium_feature),
            confirmButtonText = stringResource(id = R.string.upgrade_to_premium),
            onConfirmClick = attachmentsHandlers.onUpgradeToPremiumClick,
            dismissButtonText = stringResource(id = R.string.cancel),
            onDismissClick = attachmentsHandlers.onDismissRequest,
            onDismissRequest = attachmentsHandlers.onDismissRequest,
        )

        is AttachmentsState.DialogState.Error -> QuantVaultBasicDialog(
            title = dialogState.title?.invoke(),
            message = dialogState.message(),
            onDismissRequest = attachmentsHandlers.onDismissRequest,
            throwable = dialogState.throwable,
        )

        is AttachmentsState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = dialogState.message(),
        )

        null -> Unit
    }
}







package com.x8bit.bitwarden.ui.tools.feature.send.addedit

import androidx.activity.compose.BackHandler
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
import com.quantvault.core.util.persistentListOfNotNull
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.appbar.model.TopAppBarDividerStyle
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
import com.bitwarden.ui.platform.composition.LocalExitManager
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.manager.exit.ExitManager
import com.x8bit.bitwarden.ui.platform.composition.LocalPermissionsManager
import com.x8bit.bitwarden.ui.platform.manager.permissions.PermissionsManager
import com.x8bit.bitwarden.ui.tools.feature.generator.model.GeneratorMode
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.handlers.AddEditSendHandlers
import com.x8bit.bitwarden.R

/**
 * Displays new send UX.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSendScreen(
    viewModel: AddEditSendViewModel = hiltViewModel(),
    exitManager: ExitManager = LocalExitManager.current,
    intentManager: IntentManager = LocalIntentManager.current,
    permissionsManager: PermissionsManager = LocalPermissionsManager.current,
    onNavigateBack: () -> Unit,
    onNavigateUpToSearchOrRoot: () -> Unit,
    onNavigateToGeneratorModal: (GeneratorMode.Modal) -> Unit,
    onNavigateToPlan: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val addSendHandlers = remember(viewModel) { AddEditSendHandlers.create(viewModel) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val fileChooserLauncher = intentManager.getActivityResultLauncher { activityResult ->
        intentManager.getFileDataFromActivityResult(activityResult)?.let {
            addSendHandlers.onFileChoose(it)
        }
    }

    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    BackHandler(
        onBack = { viewModel.trySendAction(AddEditSendAction.CloseClick) },
    )
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            AddEditSendEvent.ExitApp -> exitManager.exitApplication()

            is AddEditSendEvent.NavigateBack -> onNavigateBack()

            is AddEditSendEvent.NavigateUpToSearchOrRoot -> onNavigateUpToSearchOrRoot()

            is AddEditSendEvent.ShowChooserSheet -> {
                fileChooserLauncher.launch(
                    intentManager.createFileChooserIntent(event.withCameraOption),
                )
            }

            is AddEditSendEvent.ShowShareSheet -> {
                intentManager.shareText(event.message)
            }

            is AddEditSendEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)

            is AddEditSendEvent.NavigateToGeneratorModal -> {
                onNavigateToGeneratorModal(event.generatorMode)
            }

            is AddEditSendEvent.NavigateToPremium -> {
                intentManager.launchUri(uri = event.uri.toUri())
            }

            AddEditSendEvent.NavigateToPlanModal -> onNavigateToPlan()
        }
    }

    AddEditSendDialogs(
        dialogState = state.dialogState,
        onDismissRequest = { viewModel.trySendAction(AddEditSendAction.DismissDialogClick) },
        onUpgradeToPremiumClick = {
            viewModel.trySendAction(AddEditSendAction.UpgradeToPremiumClick)
        },
    )
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = state.screenDisplayName(),
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                    navigationIconContentDescription = stringResource(id = R.string.close),
                    onNavigationIconClick = {
                        viewModel.trySendAction(AddEditSendAction.CloseClick)
                    },
                )
                    .takeUnless { state.isShared },
                dividerStyle = TopAppBarDividerStyle.NONE,
                scrollBehavior = scrollBehavior,
                actions = {
                    QuantVaultTextButton(
                        label = stringResource(id = R.string.save),
                        isEnabled = !state.policyDisablesSend,
                        onClick = { viewModel.trySendAction(AddEditSendAction.SaveClick) },
                        modifier = Modifier.testTag("SaveButton"),
                    )
                    QuantVaultOverflowActionItem(
                        isVisible = !state.isAddMode,
                        menuItemDataList = persistentListOfNotNull(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.remove_password),
                                onClick = {
                                    viewModel.trySendAction(AddEditSendAction.RemovePasswordClick)
                                },
                            )
                                .takeIf { state.hasPassword && !state.policyDisablesSend },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.copy_link),
                                onClick = {
                                    viewModel.trySendAction(AddEditSendAction.CopyLinkClick)
                                },
                            )
                                .takeIf { !state.policyDisablesSend },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.share_link),
                                isExternalLink = true,
                                onClick = {
                                    viewModel.trySendAction(AddEditSendAction.ShareLinkClick)
                                },
                            )
                                .takeIf { !state.policyDisablesSend },
                        ),
                    )
                },
            )
        },
        snackbarHost = { QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState) },
    ) {
        val modifier = Modifier
            .fillMaxSize()

        when (val viewState = state.viewState) {
            is AddEditSendState.ViewState.Content -> AddEditSendContent(
                state = viewState,
                policyDisablesSend = state.policyDisablesSend,
                policySendOptionsInEffect = state.shouldDisplayPolicyWarning,
                isAddMode = state.isAddMode,
                isShared = state.isShared,
                addSendHandlers = addSendHandlers,
                permissionsManager = permissionsManager,
                modifier = modifier,
            )

            is AddEditSendState.ViewState.Error -> QuantVaultErrorContent(
                message = viewState.message(),
                modifier = modifier,
            )

            AddEditSendState.ViewState.Loading -> QuantVaultLoadingContent(
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun AddEditSendDialogs(
    dialogState: AddEditSendState.DialogState?,
    onDismissRequest: () -> Unit,
    onUpgradeToPremiumClick: () -> Unit,
) {
    when (dialogState) {
        is AddEditSendState.DialogState.EmailAuthRequiresPremium -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.premium_subscription_required),
                message = stringResource(
                    id = R.string.sharing_with_specific_people_is_a_premium_feature,
                ),
                confirmButtonText = stringResource(id = R.string.upgrade_to_premium),
                dismissButtonText = stringResource(id = R.string.cancel),
                onConfirmClick = onUpgradeToPremiumClick,
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        is AddEditSendState.DialogState.Error -> QuantVaultBasicDialog(
            title = dialogState.title?.invoke(),
            message = dialogState.message(),
            onDismissRequest = onDismissRequest,
            throwable = dialogState.throwable,
        )

        is AddEditSendState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = dialogState.message(),
        )

        null -> Unit
    }
}







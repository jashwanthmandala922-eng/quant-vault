package com.x8bit.bitwarden.ui.vault.feature.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.core.util.persistentListOfNotNull
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.button.model.QuantVaultButtonData
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.fab.QuantVaultFloatingActionButton
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.vault.feature.addedit.VaultAddEditArgs
import com.x8bit.bitwarden.ui.vault.feature.attachments.preview.PreviewAttachmentRoute
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultCardItemTypeHandlers
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultCommonItemTypeHandlers
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultIdentityItemTypeHandlers
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultLoginItemTypeHandlers
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultSshKeyItemTypeHandlers
import com.x8bit.bitwarden.ui.vault.model.VaultAddEditType
import com.x8bit.bitwarden.R

/**
 * Displays the vault item screen.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultItemScreen(
    viewModel: VaultItemViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    onNavigateBack: () -> Unit,
    onNavigateToVaultAddEditItem: (args: VaultAddEditArgs) -> Unit,
    onNavigateToMoveToOrganization: (vaultItemId: String, showOnlyCollections: Boolean) -> Unit,
    onNavigateToAttachments: (vaultItemId: String) -> Unit,
    onNavigateToPasswordHistory: (vaultItemId: String) -> Unit,
    onNavigateToPreviewAttachment: (route: PreviewAttachmentRoute) -> Unit,
    onNavigateToPlan: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val fileChooserLauncher = intentManager.getActivityResultLauncher { activityResult ->
        intentManager.getFileDataFromActivityResult(activityResult)
            ?.let {
                viewModel.trySendAction(
                    VaultItemAction.Common.AttachmentFileLocationReceive(it.uri),
                )
            }
            ?: viewModel.trySendAction(VaultItemAction.Common.NoAttachmentFileLocationReceive)
    }
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            VaultItemEvent.NavigateBack -> onNavigateBack()

            is VaultItemEvent.NavigateToAddEdit -> {
                onNavigateToVaultAddEditItem(
                    VaultAddEditArgs(
                        vaultAddEditType = if (event.isClone) {
                            VaultAddEditType.CloneItem(vaultItemId = event.itemId)
                        } else {
                            VaultAddEditType.EditItem(vaultItemId = event.itemId)
                        },
                        vaultItemCipherType = event.type,
                    ),
                )
            }

            is VaultItemEvent.NavigateToPasswordHistory -> {
                onNavigateToPasswordHistory(event.itemId)
            }

            is VaultItemEvent.NavigateToUri -> intentManager.launchUri(event.uri.toUri())

            VaultItemEvent.NavigateToPlanModal -> onNavigateToPlan()

            is VaultItemEvent.NavigateToAttachments -> onNavigateToAttachments(event.itemId)

            is VaultItemEvent.NavigateToMoveToOrganization -> {
                onNavigateToMoveToOrganization(event.itemId, false)
            }

            is VaultItemEvent.NavigateToCollections -> {
                onNavigateToMoveToOrganization(event.itemId, true)
            }

            is VaultItemEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)

            is VaultItemEvent.NavigateToSelectAttachmentSaveLocation -> {
                fileChooserLauncher.launch(
                    intentManager.createDocumentIntent(event.fileName),
                )
            }

            is VaultItemEvent.NavigateToPreviewAttachment -> {
                onNavigateToPreviewAttachment(
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

    VaultItemDialogs(
        dialog = state.dialog,
        onDismissRequest = { viewModel.trySendAction(VaultItemAction.Common.DismissDialogClick) },
        onConfirmDeleteClick = {
            viewModel.trySendAction(VaultItemAction.Common.ConfirmDeleteClick)
        },
        onConfirmCloneWithoutFido2Credential = {
            viewModel.trySendAction(
                VaultItemAction.Common.ConfirmCloneWithoutFido2CredentialClick,
            )
        },
        onConfirmRestoreAction = {
            viewModel.trySendAction(VaultItemAction.Common.ConfirmRestoreClick)
        },
        onUpgradeToPremiumClick = {
            viewModel.trySendAction(VaultItemAction.Common.UpgradeToPremiumClick)
        },
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = state.title(),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = {
                    viewModel.trySendAction(VaultItemAction.Common.CloseClick)
                },
                actions = {
                    if (state.canRestore) {
                        QuantVaultTextButton(
                            label = stringResource(id = R.string.restore),
                            onClick = {
                                viewModel.trySendAction(
                                    VaultItemAction.Common.RestoreVaultItemClick,
                                )
                            },
                            modifier = Modifier.testTag("RestoreButton"),
                        )
                    }
                    QuantVaultOverflowActionItem(
                        menuItemDataList = persistentListOfNotNull(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.attachments),
                                onClick = {
                                    viewModel.trySendAction(
                                        VaultItemAction.Common.AttachmentsClick,
                                    )
                                },
                            ),
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.clone),
                                onClick = {
                                    viewModel.trySendAction(VaultItemAction.Common.CloneClick)
                                },
                            )
                                .takeUnless { state.isCipherInCollection },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.move_to_organization),
                                onClick = {
                                    viewModel.trySendAction(
                                        VaultItemAction.Common.MoveToOrganizationClick,
                                    )
                                },
                            )
                                .takeUnless {
                                    state.isCipherInCollection ||
                                        !state.hasOrganizations
                                },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.collections),
                                onClick = {
                                    viewModel.trySendAction(VaultItemAction.Common.CollectionsClick)
                                },
                            )
                                .takeIf {
                                    state.isCipherInCollection &&
                                        state.canAssignToCollections
                                },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.archive_verb),
                                onClick = {
                                    viewModel.trySendAction(VaultItemAction.Common.ArchiveClick)
                                },
                            )
                                .takeIf { state.displayArchiveButton },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.unarchive),
                                onClick = {
                                    viewModel.trySendAction(VaultItemAction.Common.UnarchiveClick)
                                },
                            )
                                .takeIf { state.displayUnarchiveButton },
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.delete),
                                onClick = {
                                    viewModel.trySendAction(VaultItemAction.Common.DeleteClick)
                                },
                            )
                                .takeIf { state.canDelete },
                        ),
                    )
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.isFabVisible,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                QuantVaultFloatingActionButton(
                    onClick = { viewModel.trySendAction(VaultItemAction.Common.EditClick) },
                    painter = rememberVectorPainter(id = R.drawable.ic_pencil),
                    contentDescription = stringResource(id = R.string.edit_item),
                    modifier = Modifier
                        .testTag(tag = "EditItemButton")
                        .padding(bottom = 16.dp),
                )
            }
        },
        snackbarHost = {
            QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState)
        },
    ) {
        VaultItemContent(
            viewState = state.viewState,
            modifier = Modifier
                .fillMaxSize(),
            vaultCommonItemTypeHandlers = remember(viewModel) {
                VaultCommonItemTypeHandlers.create(viewModel = viewModel)
            },
            vaultLoginItemTypeHandlers = remember(viewModel) {
                VaultLoginItemTypeHandlers.create(viewModel = viewModel)
            },
            vaultCardItemTypeHandlers = remember(viewModel) {
                VaultCardItemTypeHandlers.create(viewModel = viewModel)
            },
            vaultSshKeyItemTypeHandlers = remember(viewModel) {
                VaultSshKeyItemTypeHandlers.create(viewModel = viewModel)
            },
            vaultIdentityItemTypeHandlers = remember(viewModel) {
                VaultIdentityItemTypeHandlers.create(viewModel = viewModel)
            },
        )
    }
}

@Composable
private fun VaultItemDialogs(
    dialog: VaultItemState.DialogState?,
    onDismissRequest: () -> Unit,
    onConfirmDeleteClick: () -> Unit,
    onConfirmCloneWithoutFido2Credential: () -> Unit,
    onConfirmRestoreAction: () -> Unit,
    onUpgradeToPremiumClick: () -> Unit,
) {
    when (dialog) {
        is VaultItemState.DialogState.ArchiveRequiresPremium -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.archive_unavailable),
                message = stringResource(id = R.string.archiving_items_is_a_premium_feature),
                confirmButtonText = stringResource(id = R.string.upgrade_to_premium),
                dismissButtonText = stringResource(id = R.string.cancel),
                onConfirmClick = onUpgradeToPremiumClick,
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        is VaultItemState.DialogState.Generic -> QuantVaultBasicDialog(
            title = null,
            message = dialog.message(),
            throwable = dialog.error,
            onDismissRequest = onDismissRequest,
        )

        is VaultItemState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = dialog.message(),
        )

        is VaultItemState.DialogState.DeleteConfirmationPrompt -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.delete),
                message = dialog.message.invoke(),
                confirmButtonText = stringResource(id = R.string.okay),
                dismissButtonText = stringResource(id = R.string.cancel),
                onConfirmClick = onConfirmDeleteClick,
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        is VaultItemState.DialogState.Fido2CredentialCannotBeCopiedConfirmationPrompt -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.passkey_will_not_be_copied),
                message = dialog.message.invoke(),
                confirmButtonText = stringResource(id = R.string.yes),
                dismissButtonText = stringResource(id = R.string.no),
                onConfirmClick = onConfirmCloneWithoutFido2Credential,
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        VaultItemState.DialogState.RestoreItemDialog -> QuantVaultTwoButtonDialog(
            title = stringResource(id = R.string.restore),
            message = stringResource(id = R.string.do_you_really_want_to_restore_cipher),
            confirmButtonText = stringResource(id = R.string.okay),
            dismissButtonText = stringResource(id = R.string.cancel),
            onConfirmClick = onConfirmRestoreAction,
            onDismissClick = onDismissRequest,
            onDismissRequest = onDismissRequest,
        )

        null -> Unit
    }
}

@Suppress("LongMethod")
@Composable
private fun VaultItemContent(
    viewState: VaultItemState.ViewState,
    vaultCommonItemTypeHandlers: VaultCommonItemTypeHandlers,
    vaultLoginItemTypeHandlers: VaultLoginItemTypeHandlers,
    vaultCardItemTypeHandlers: VaultCardItemTypeHandlers,
    vaultSshKeyItemTypeHandlers: VaultSshKeyItemTypeHandlers,
    vaultIdentityItemTypeHandlers: VaultIdentityItemTypeHandlers,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is VaultItemState.ViewState.Error -> QuantVaultErrorContent(
            message = viewState.message(),
            buttonData = QuantVaultButtonData(
                label = R.string.try_again.asText(),
                onClick = vaultCommonItemTypeHandlers.onRefreshClick,
            ),
            modifier = modifier,
        )

        is VaultItemState.ViewState.Content -> {
            when (viewState.type) {
                is VaultItemState.ViewState.Content.ItemType.Login -> {
                    VaultItemLoginContent(
                        commonState = viewState.common,
                        loginItemState = viewState.type,
                        vaultCommonItemTypeHandlers = vaultCommonItemTypeHandlers,
                        vaultLoginItemTypeHandlers = vaultLoginItemTypeHandlers,
                        modifier = modifier,
                    )
                }

                is VaultItemState.ViewState.Content.ItemType.Card -> {
                    VaultItemCardContent(
                        commonState = viewState.common,
                        cardState = viewState.type,
                        vaultCommonItemTypeHandlers = vaultCommonItemTypeHandlers,
                        vaultCardItemTypeHandlers = vaultCardItemTypeHandlers,
                        modifier = modifier,
                    )
                }

                is VaultItemState.ViewState.Content.ItemType.Identity -> {
                    VaultItemIdentityContent(
                        commonState = viewState.common,
                        identityState = viewState.type,
                        vaultCommonItemTypeHandlers = vaultCommonItemTypeHandlers,
                        vaultIdentityItemTypeHandlers = vaultIdentityItemTypeHandlers,
                        modifier = modifier,
                    )
                }

                is VaultItemState.ViewState.Content.ItemType.SecureNote -> {
                    VaultItemSecureNoteContent(
                        commonState = viewState.common,
                        vaultCommonItemTypeHandlers = vaultCommonItemTypeHandlers,
                        modifier = modifier,
                    )
                }

                is VaultItemState.ViewState.Content.ItemType.SshKey -> {
                    VaultItemSshKeyContent(
                        commonState = viewState.common,
                        sshKeyItemState = viewState.type,
                        vaultCommonItemTypeHandlers = vaultCommonItemTypeHandlers,
                        vaultSshKeyItemTypeHandlers = vaultSshKeyItemTypeHandlers,
                        modifier = modifier,
                    )
                }
            }
        }

        VaultItemState.ViewState.Loading -> QuantVaultLoadingContent(
            modifier = modifier,
        )
    }
}







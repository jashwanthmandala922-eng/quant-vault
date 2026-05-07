package com.x8bit.bitwarden.ui.vault.feature.itemlisting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.components.account.QuantVaultAccountActionItem
import com.bitwarden.ui.platform.components.account.QuantVaultAccountSwitcher
import com.bitwarden.ui.platform.components.account.util.initials
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultSearchActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.model.QuantVaultButtonData
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.fab.QuantVaultFloatingActionButton
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.scaffold.model.QuantVaultPullToRefreshState
import com.bitwarden.ui.platform.components.scaffold.model.rememberQuantVaultPullToRefreshState
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalExitManager
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.manager.exit.ExitManager
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.credentials.manager.CredentialProviderCompletionManager
import com.x8bit.bitwarden.ui.platform.components.dialog.QuantVaultMasterPasswordDialog
import com.x8bit.bitwarden.ui.platform.components.dialog.QuantVaultOverwriteCredentialConfirmationDialog
import com.x8bit.bitwarden.ui.platform.components.dialog.QuantVaultPinDialog
import com.x8bit.bitwarden.ui.platform.composition.LocalBiometricsManager
import com.x8bit.bitwarden.ui.platform.composition.LocalCredentialProviderCompletionManager
import com.x8bit.bitwarden.ui.platform.feature.search.model.SearchType
import com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity.PinInputDialog
import com.x8bit.bitwarden.ui.platform.manager.biometrics.BiometricsManager
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.AddEditSendRoute
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.ModeType
import com.x8bit.bitwarden.ui.tools.feature.send.viewsend.ViewSendRoute
import com.x8bit.bitwarden.ui.vault.components.VaultItemSelectionDialog
import com.x8bit.bitwarden.ui.vault.feature.addedit.VaultAddEditArgs
import com.x8bit.bitwarden.ui.vault.feature.item.VaultItemArgs
import com.x8bit.bitwarden.ui.vault.feature.itemlisting.handlers.VaultItemListingHandlers
import com.x8bit.bitwarden.ui.vault.feature.itemlisting.handlers.VaultItemListingUserVerificationHandlers
import com.x8bit.bitwarden.ui.vault.model.VaultAddEditType
import com.x8bit.bitwarden.ui.vault.model.VaultItemListingType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import com.x8bit.bitwarden.R

/**
 * Displays the vault item listing screen.
 */
@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
fun VaultItemListingScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVaultItemScreen: (args: VaultItemArgs) -> Unit,
    onNavigateToVaultEditItemScreen: (args: VaultAddEditArgs) -> Unit,
    onNavigateToVaultItemListing: (vaultItemListingType: VaultItemListingType) -> Unit,
    onNavigateToVaultAddItemScreen: (args: VaultAddEditArgs) -> Unit,
    onNavigateToAddFolder: (selectedFolderId: String?) -> Unit,
    onNavigateToAddEditSendItem: (route: AddEditSendRoute) -> Unit,
    onNavigateToViewSendItem: (route: ViewSendRoute) -> Unit,
    onNavigateToSearch: (searchType: SearchType) -> Unit,
    onNavigateToPlan: () -> Unit,
    intentManager: IntentManager = LocalIntentManager.current,
    exitManager: ExitManager = LocalExitManager.current,
    credentialProviderCompletionManager: CredentialProviderCompletionManager =
        LocalCredentialProviderCompletionManager.current,
    biometricsManager: BiometricsManager = LocalBiometricsManager.current,
    viewModel: VaultItemListingViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val userVerificationHandlers = remember(viewModel) {
        VaultItemListingUserVerificationHandlers.create(viewModel = viewModel)
    }

    val pullToRefreshState = rememberQuantVaultPullToRefreshState(
        isEnabled = state.isPullToRefreshEnabled,
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.trySendAction(VaultItemListingsAction.RefreshPull) },
    )
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is VaultItemListingEvent.NavigateBack -> onNavigateBack()

            is VaultItemListingEvent.NavigateToVaultItem -> {
                onNavigateToVaultItemScreen(VaultItemArgs(event.id, event.type))
            }

            is VaultItemListingEvent.ShowShareSheet -> {
                intentManager.shareText(event.content)
            }

            is VaultItemListingEvent.NavigateToAddVaultItem -> {
                onNavigateToVaultAddItemScreen(
                    VaultAddEditArgs(
                        vaultAddEditType = VaultAddEditType.AddItem,
                        vaultItemCipherType = event.vaultItemCipherType,
                        selectedFolderId = event.selectedFolderId,
                        selectedCollectionId = event.selectedCollectionId,
                    ),
                )
            }

            is VaultItemListingEvent.NavigateToViewSendItem -> {
                onNavigateToViewSendItem(
                    ViewSendRoute(sendId = event.id, sendType = event.sendType),
                )
            }

            is VaultItemListingEvent.NavigateToEditCipher -> {
                onNavigateToVaultEditItemScreen(
                    VaultAddEditArgs(
                        vaultAddEditType = VaultAddEditType.EditItem(vaultItemId = event.cipherId),
                        vaultItemCipherType = event.cipherType,
                    ),
                )
            }

            is VaultItemListingEvent.NavigateToUrl -> {
                intentManager.launchUri(event.url.toUri())
            }

            VaultItemListingEvent.NavigateToPlanModal -> onNavigateToPlan()

            is VaultItemListingEvent.NavigateToAddSendItem -> {
                onNavigateToAddEditSendItem(
                    AddEditSendRoute(
                        sendType = event.sendType,
                        modeType = ModeType.ADD,
                    ),
                )
            }

            is VaultItemListingEvent.NavigateToEditSendItem -> {
                onNavigateToAddEditSendItem(
                    AddEditSendRoute(
                        sendType = event.sendType,
                        modeType = ModeType.EDIT,
                        sendId = event.id,
                    ),
                )
            }

            is VaultItemListingEvent.NavigateToSearchScreen -> {
                onNavigateToSearch(event.searchType)
            }

            is VaultItemListingEvent.NavigateToFolderItem -> {
                onNavigateToVaultItemListing(VaultItemListingType.Folder(event.folderId))
            }

            is VaultItemListingEvent.NavigateToCollectionItem -> {
                onNavigateToVaultItemListing(VaultItemListingType.Collection(event.collectionId))
            }

            is VaultItemListingEvent.CompleteCredentialRegistration -> {
                credentialProviderCompletionManager.completeCredentialRegistration(event.result)
            }

            is VaultItemListingEvent.CredentialManagerUserVerification -> {
                biometricsManager.promptUserVerification(
                    onSuccess = {
                        userVerificationHandlers
                            .onUserVerificationSuccess(event.selectedCipherView)
                    },
                    onCancel = userVerificationHandlers.onUserVerificationCancelled,
                    onLockOut = userVerificationHandlers.onUserVerificationLockOut,
                    onError = userVerificationHandlers.onUserVerificationFail,
                    onNotSupported = {
                        userVerificationHandlers.onUserVerificationNotSupported(
                            event.selectedCipherView.id,
                        )
                    },
                )
            }

            is VaultItemListingEvent.CompleteFido2Assertion -> {
                credentialProviderCompletionManager.completeFido2Assertion(event.result)
            }

            is VaultItemListingEvent.CompleteProviderGetCredentialsRequest -> {
                credentialProviderCompletionManager
                    .completeProviderGetCredentialsRequest(event.result)
            }

            is VaultItemListingEvent.CompleteProviderGetPasswordCredentialRequest -> {
                credentialProviderCompletionManager.completePasswordGet(event.result)
            }

            VaultItemListingEvent.ExitApp -> exitManager.exitApplication()

            is VaultItemListingEvent.NavigateToAddFolder -> {
                onNavigateToAddFolder(event.parentFolderName)
            }

            is VaultItemListingEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
        }
    }

    val vaultItemListingHandlers = remember(viewModel) {
        VaultItemListingHandlers.create(viewModel)
    }
    VaultItemListingDialogs(
        dialogState = state.dialogState,
        vaultItemListingHandlers = vaultItemListingHandlers,
    )

    BackHandler(onBack = vaultItemListingHandlers.backClick)
    VaultItemListingScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        pullToRefreshState = pullToRefreshState,
        vaultItemListingHandlers = vaultItemListingHandlers,
    )
}

@Suppress("LongMethod")
@Composable
private fun VaultItemListingDialogs(
    dialogState: VaultItemListingState.DialogState?,
    vaultItemListingHandlers: VaultItemListingHandlers,
) {
    when (dialogState) {
        is VaultItemListingState.DialogState.Error -> QuantVaultBasicDialog(
            title = dialogState.title?.invoke(),
            message = dialogState.message(),
            onDismissRequest = vaultItemListingHandlers.dismissDialogRequest,
            throwable = dialogState.throwable,
        )

        is VaultItemListingState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = dialogState.message(),
        )

        is VaultItemListingState.DialogState.CipherDecryptionError -> {
            QuantVaultTwoButtonDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                confirmButtonText = stringResource(R.string.copy_error_report),
                dismissButtonText = stringResource(R.string.close),
                onConfirmClick = {
                    vaultItemListingHandlers.shareCipherDecryptionErrorClick(
                        dialogState.selectedCipherId,
                    )
                },
                onDismissClick = vaultItemListingHandlers.dismissDialogRequest,
                onDismissRequest = vaultItemListingHandlers.dismissDialogRequest,
            )
        }

        is VaultItemListingState.DialogState.CredentialManagerOperationFail -> QuantVaultBasicDialog(
            title = dialogState.title(),
            message = dialogState.message(),
            onDismissRequest = {
                vaultItemListingHandlers.dismissCredentialManagerErrorDialog(dialogState.message)
            },
        )

        is VaultItemListingState.DialogState.OverwritePasskeyConfirmationPrompt -> {
            @Suppress("MaxLineLength")
            QuantVaultOverwriteCredentialConfirmationDialog(
                title = stringResource(id = R.string.overwrite_passkey),
                message = stringResource(
                    id = R.string.this_item_already_contains_a_passkey_are_you_sure_you_want_to_overwrite_the_current_passkey,
                ),
                onConfirmClick = {
                    vaultItemListingHandlers.confirmOverwriteExistingPasskey(
                        dialogState.cipherViewId,
                    )
                },
                onDismissRequest = vaultItemListingHandlers.dismissDialogRequest,
            )
        }

        is VaultItemListingState.DialogState.UserVerificationMasterPasswordPrompt -> {
            QuantVaultMasterPasswordDialog(
                onConfirmClick = { password ->
                    vaultItemListingHandlers.submitMasterPasswordCredentialVerification(
                        password,
                        dialogState.selectedCipherId,
                    )
                },
                onDismissRequest = vaultItemListingHandlers.dismissUserVerification,
            )
        }

        is VaultItemListingState.DialogState.UserVerificationMasterPasswordError -> {
            QuantVaultBasicDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = {
                    vaultItemListingHandlers.retryGetCredentialPasswordVerification(
                        dialogState.selectedCipherId,
                    )
                },
            )
        }

        is VaultItemListingState.DialogState.UserVerificationPinPrompt -> {
            QuantVaultPinDialog(
                onConfirmClick = { pin ->
                    vaultItemListingHandlers.submitPinCredentialVerification(
                        pin,
                        dialogState.selectedCipherId,
                    )
                },
                onDismissRequest = vaultItemListingHandlers.dismissUserVerification,
            )
        }

        is VaultItemListingState.DialogState.UserVerificationPinError -> {
            QuantVaultBasicDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = {
                    vaultItemListingHandlers.retryPinCredentialVerification(
                        dialogState.selectedCipherId,
                    )
                },
            )
        }

        is VaultItemListingState.DialogState.UserVerificationPinSetUpPrompt -> {
            PinInputDialog(
                onCancelClick = vaultItemListingHandlers.dismissUserVerification,
                onSubmitClick = { pin ->
                    vaultItemListingHandlers.submitPinSetUpCredentialVerification(
                        pin,
                        dialogState.selectedCipherId,
                    )
                },
                onDismissRequest = vaultItemListingHandlers.dismissUserVerification,
            )
        }

        is VaultItemListingState.DialogState.UserVerificationPinSetUpError -> {
            QuantVaultBasicDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = {
                    vaultItemListingHandlers.retryPinSetUpCredentialVerification(
                        dialogState.selectedCipherId,
                    )
                },
            )
        }

        is VaultItemListingState.DialogState.VaultItemTypeSelection -> {
            VaultItemSelectionDialog(
                onDismissRequest = vaultItemListingHandlers.dismissDialogRequest,
                onOptionSelected = vaultItemListingHandlers.vaultItemTypeSelected,
                excludedOptions = dialogState.excludedOptions,
            )
        }

        is VaultItemListingState.DialogState.TrustPrivilegedAddPrompt -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(R.string.unrecognized_browser),
                message = dialogState.message.invoke(),
                confirmButtonText = stringResource(R.string.trust),
                dismissButtonText = stringResource(R.string.cancel),
                onConfirmClick = {
                    vaultItemListingHandlers.trustPrivilegedAppClick(dialogState.selectedCipherId)
                },
                onDismissClick = {
                    vaultItemListingHandlers.dismissCredentialManagerErrorDialog(
                        R.string.passkey_operation_failed_because_the_browser_is_not_trusted
                            .asText(),
                    )
                },
                onDismissRequest = {
                    vaultItemListingHandlers.dismissCredentialManagerErrorDialog(
                        R.string.passkey_operation_failed_because_the_browser_is_not_trusted
                            .asText(),
                    )
                },
            )
        }

        is VaultItemListingState.DialogState.ArchiveRequiresPremium -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.archive_unavailable),
                message = stringResource(id = R.string.archiving_items_is_a_premium_feature),
                confirmButtonText = stringResource(id = R.string.upgrade_to_premium),
                dismissButtonText = stringResource(id = R.string.cancel),
                onConfirmClick = vaultItemListingHandlers.upgradeToPremiumClick,
                onDismissClick = vaultItemListingHandlers.dismissDialogRequest,
                onDismissRequest = vaultItemListingHandlers.dismissDialogRequest,
            )
        }

        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
private fun VaultItemListingScaffold(
    state: VaultItemListingState,
    pullToRefreshState: QuantVaultPullToRefreshState,
    snackbarHostState: QuantVaultSnackbarHostState,
    vaultItemListingHandlers: VaultItemListingHandlers,
) {
    var isAccountMenuVisible by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = state.appBarTitle(),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                    navigationIconContentDescription = stringResource(id = R.string.back),
                    onNavigationIconClick = vaultItemListingHandlers.backClick,
                )
                    .takeIf { state.shouldShowNavigationIcon },
                actions = {
                    if (state.shouldShowAccountSwitcher) {
                        QuantVaultAccountActionItem(
                            initials = state.activeAccountSummary.initials,
                            color = state.activeAccountSummary.avatarColor,
                            onClick = { isAccountMenuVisible = !isAccountMenuVisible },
                        )
                    }
                    QuantVaultSearchActionItem(
                        contentDescription = stringResource(id = R.string.search_vault),
                        isDisplayed = state.shouldShowSearchIcon,
                        onClick = vaultItemListingHandlers.searchIconClick,
                    )
                    QuantVaultOverflowActionItem(
                        isVisible = state.shouldShowOverflowMenu,
                        menuItemDataList = persistentListOf(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.sync),
                                onClick = vaultItemListingHandlers.syncClick,
                            ),
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.lock),
                                onClick = vaultItemListingHandlers.lockClick,
                            ),
                        ),
                    )
                },
            )
        },
        floatingActionButton = {
            if (state.hasAddItemFabButton) {
                QuantVaultFloatingActionButton(
                    onClick = vaultItemListingHandlers.addVaultItemClick,
                    painter = rememberVectorPainter(id = R.drawable.ic_plus_large),
                    contentDescription = stringResource(id = R.string.add_item),
                    modifier = Modifier.testTag(tag = "AddItemButton"),
                )
            }
        },
        overlay = {
            QuantVaultAccountSwitcher(
                isVisible = isAccountMenuVisible,
                accountSummaries = state.accountSummaries.toImmutableList(),
                onSwitchAccountClick = vaultItemListingHandlers.switchAccountClick,
                onLockAccountClick = vaultItemListingHandlers.lockAccountClick,
                onLogoutAccountClick = vaultItemListingHandlers.logoutAccountClick,
                onAddAccountClick = {
                    // Not available
                },
                onDismissRequest = { isAccountMenuVisible = false },
                isAddAccountAvailable = false,
                topAppBarScrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxSize(),
            )
        },
        pullToRefreshState = pullToRefreshState,
        snackbarHost = { QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState) },
    ) {
        when (state.viewState) {
            is VaultItemListingState.ViewState.Content -> {
                VaultItemListingContent(
                    state = state.viewState,
                    actionCard = state.actionCard,
                    showAddTotpBanner = state.isTotp,
                    policyDisablesSend = state.policyDisablesSend &&
                        state.itemListingType is VaultItemListingState.ItemListingType.Send,
                    vaultItemListingHandlers = vaultItemListingHandlers,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is VaultItemListingState.ViewState.NoItems -> {
                VaultItemListingEmpty(
                    state = state.viewState,
                    policyDisablesSend = state.policyDisablesSend &&
                        state.itemListingType is VaultItemListingState.ItemListingType.Send,
                    addItemClickAction = vaultItemListingHandlers.addVaultItemClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is VaultItemListingState.ViewState.Error -> {
                QuantVaultErrorContent(
                    message = state.viewState.message(),
                    buttonData = QuantVaultButtonData(
                        label = R.string.try_again.asText(),
                        onClick = vaultItemListingHandlers.refreshClick,
                    ),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is VaultItemListingState.ViewState.Loading -> {
                QuantVaultLoadingContent(modifier = Modifier.fillMaxSize())
            }
        }
    }
}








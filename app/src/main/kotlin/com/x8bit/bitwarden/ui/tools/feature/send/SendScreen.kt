package com.x8bit.bitwarden.ui.tools.feature.send

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.bitwarden.ui.platform.components.appbar.QuantVaultMediumTopAppBar
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultSearchActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.model.QuantVaultButtonData
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultSelectionDialog
import com.bitwarden.ui.platform.components.dialog.row.QuantVaultBasicDialogRow
import com.bitwarden.ui.platform.components.fab.QuantVaultFloatingActionButton
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.scaffold.model.rememberQuantVaultPullToRefreshState
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.data.platform.manager.model.AppResumeScreenData
import com.x8bit.bitwarden.data.platform.manager.util.AppResumeStateManager
import com.x8bit.bitwarden.data.platform.manager.util.RegisterScreenDataOnLifecycleEffect
import com.x8bit.bitwarden.ui.platform.composition.LocalAppResumeStateManager
import com.x8bit.bitwarden.ui.platform.feature.search.model.SearchType
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.AddEditSendRoute
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.ModeType
import com.x8bit.bitwarden.ui.tools.feature.send.handlers.SendHandlers
import com.x8bit.bitwarden.ui.tools.feature.send.model.SendItemType
import com.x8bit.bitwarden.ui.tools.feature.send.util.selectionText
import com.x8bit.bitwarden.ui.tools.feature.send.viewsend.ViewSendRoute
import kotlinx.collections.immutable.persistentListOf
import com.x8bit.bitwarden.R

/**
 * UI for the send screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(
    onNavigateToAddEditSend: (AddEditSendRoute) -> Unit,
    onNavigateToViewSend: (ViewSendRoute) -> Unit,
    onNavigateToSendFilesList: () -> Unit,
    onNavigateToSendTextList: () -> Unit,
    onNavigateToSearchSend: (searchType: SearchType.Sends) -> Unit,
    viewModel: SendViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    appResumeStateManager: AppResumeStateManager = LocalAppResumeStateManager.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberQuantVaultPullToRefreshState(
        isEnabled = state.isPullToRefreshEnabled,
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.trySendAction(SendAction.RefreshPull) },
    )

    RegisterScreenDataOnLifecycleEffect(
        appResumeStateManager = appResumeStateManager,
    ) {
        AppResumeScreenData.SendScreen
    }

    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is SendEvent.NavigateToSearch -> onNavigateToSearchSend(SearchType.Sends.All)

            is SendEvent.NavigateNewSend -> {
                onNavigateToAddEditSend(
                    AddEditSendRoute(modeType = ModeType.ADD, sendType = event.sendType),
                )
            }

            is SendEvent.NavigateToEditSend -> {
                onNavigateToAddEditSend(
                    AddEditSendRoute(
                        modeType = ModeType.EDIT,
                        sendType = event.sendType,
                        sendId = event.sendId,
                    ),
                )
            }

            is SendEvent.NavigateToViewSend -> {
                onNavigateToViewSend(
                    ViewSendRoute(sendId = event.sendId, sendType = event.sendType),
                )
            }

            is SendEvent.NavigateToAboutSend -> {
                intentManager.launchUri("https://Quant Vault.com/products/send".toUri())
            }

            is SendEvent.ShowShareSheet -> intentManager.shareText(event.url)
            is SendEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
            SendEvent.NavigateToFileSends -> onNavigateToSendFilesList()
            SendEvent.NavigateToTextSends -> onNavigateToSendTextList()
        }
    }

    SendDialogs(
        dialogState = state.dialogState,
        onAddSendSelected = { viewModel.trySendAction(SendAction.AddSendSelected(it)) },
        onDismissRequest = { viewModel.trySendAction(SendAction.DismissDialog) },
    )

    val sendHandlers = remember(viewModel) { SendHandlers.create(viewModel) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState(),
    )
    QuantVaultScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultMediumTopAppBar(
                title = stringResource(id = R.string.send),
                scrollBehavior = scrollBehavior,
                actions = {
                    QuantVaultSearchActionItem(
                        contentDescription = stringResource(id = R.string.search_sends),
                        isDisplayed = state.shouldShowSearchIcon,
                        onClick = { viewModel.trySendAction(SendAction.SearchClick) },
                    )
                    QuantVaultOverflowActionItem(
                        menuItemDataList = persistentListOf(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.sync),
                                onClick = { viewModel.trySendAction(SendAction.SyncClick) },
                            ),
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.lock),
                                onClick = { viewModel.trySendAction(SendAction.LockClick) },
                            ),
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.about_send),
                                onClick = { viewModel.trySendAction(SendAction.AboutSendClick) },
                                isExternalLink = true,
                            ),
                        ),
                    )
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.viewState.shouldDisplayFab,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                QuantVaultFloatingActionButton(
                    onClick = { viewModel.trySendAction(SendAction.AddSendClick) },
                    painter = rememberVectorPainter(id = R.drawable.ic_plus_large),
                    contentDescription = stringResource(id = R.string.add_item),
                    modifier = Modifier.testTag(tag = "AddItemButton"),
                )
            }
        },
        pullToRefreshState = pullToRefreshState,
        snackbarHost = { QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState) },
    ) {
        val modifier = Modifier
            .fillMaxSize()
        when (val viewState = state.viewState) {
            is SendState.ViewState.Content -> SendContent(
                policyDisablesSend = state.policyDisablesSend,
                state = viewState,
                sendHandlers = sendHandlers,
                modifier = modifier,
            )

            SendState.ViewState.Empty -> SendEmpty(
                policyDisablesSend = state.policyDisablesSend,
                onAddItemClick = { viewModel.trySendAction(SendAction.AddSendClick) },
                modifier = modifier,
            )

            is SendState.ViewState.Error -> QuantVaultErrorContent(
                message = viewState.message(),
                buttonData = QuantVaultButtonData(
                    label = R.string.try_again.asText(),
                    onClick = { viewModel.trySendAction(SendAction.RefreshClick) },
                ),
                modifier = modifier,
            )

            SendState.ViewState.Loading -> QuantVaultLoadingContent(modifier = modifier)
        }
    }
}

@Composable
private fun SendDialogs(
    dialogState: SendState.DialogState?,
    onAddSendSelected: (SendItemType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is SendState.DialogState.Error -> QuantVaultBasicDialog(
            title = dialogState.title?.invoke(),
            message = dialogState.message(),
            onDismissRequest = onDismissRequest,
            throwable = dialogState.throwable,
        )

        is SendState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = dialogState.message(),
        )

        SendState.DialogState.SelectSendAddType -> QuantVaultSelectionDialog(
            title = stringResource(id = R.string.type),
            onDismissRequest = onDismissRequest,
        ) {
            SendItemType.entries.forEach {
                QuantVaultBasicDialogRow(
                    text = it.selectionText(),
                    onClick = { onAddSendSelected(it) },
                )
            }
        }

        null -> Unit
    }
}







package com.quantvault.authenticator.ui.authenticator.feature.itemlisting

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.authenticator.ui.platform.components.header.AuthenticatorExpandingHeader
import com.quantvault.authenticator.ui.platform.components.listitem.VaultVerificationCodeItem
import com.quantvault.authenticator.ui.platform.components.listitem.model.SharedCodesDisplayState
import com.quantvault.authenticator.ui.platform.components.listitem.model.VaultDropdownMenuAction
import com.quantvault.authenticator.ui.platform.components.listitem.model.VerificationCodeDisplayItem
import com.quantvault.authenticator.ui.platform.composition.LocalPermissionsManager
import com.quantvault.authenticator.ui.platform.manager.permissions.PermissionsManager
import com.quantvault.authenticator.ui.platform.util.startQuantVaultAccountSettings
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.base.util.toListItemCardStyle
import com.quantvault.ui.platform.components.appbar.QuantVaultMediumTopAppBar
import com.quantvault.ui.platform.components.appbar.action.QuantVaultSearchActionItem
import com.quantvault.ui.platform.components.button.QuantVaultFilledButton
import com.quantvault.ui.platform.components.button.model.QuantVaultButtonData
import com.quantvault.ui.platform.components.card.QuantVaultActionCard
import com.quantvault.ui.platform.components.content.QuantVaultLoadingContent
import com.quantvault.ui.platform.components.dialog.QuantVaultBasicDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.quantvault.ui.platform.components.fab.QuantVaultExpandableFloatingActionButton
import com.quantvault.ui.platform.components.fab.model.ExpandableFabIcon
import com.quantvault.ui.platform.components.fab.model.ExpandableFabOption
import com.quantvault.ui.platform.components.header.QuantVaultListHeaderText
import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.quantvault.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.composition.LocalIntentManager
import com.quantvault.ui.platform.manager.IntentManager
import com.quantvault.ui.platform.manager.util.startAppSettingsActivity
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.util.asText
import kotlinx.collections.immutable.persistentListOf

/**
 * Displays the item listing screen.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListingScreen(
    viewModel: ItemListingViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    permissionsManager: PermissionsManager = LocalPermissionsManager.current,
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToQrCodeScanner: () -> Unit,
    onNavigateToManualKeyEntry: () -> Unit,
    onNavigateToEditItemScreen: (id: String) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val launcher = permissionsManager.getLauncher { isGranted ->
        if (isGranted) {
            viewModel.trySendAction(ItemListingAction.ScanQrCodeClick)
        } else {
            viewModel.trySendAction(ItemListingAction.EnterSetupKeyClick)
        }
    }
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is ItemListingEvent.NavigateBack -> onNavigateBack()
            is ItemListingEvent.NavigateToSearch -> onNavigateToSearch()
            is ItemListingEvent.NavigateToQrCodeScanner -> onNavigateToQrCodeScanner()
            is ItemListingEvent.NavigateToManualAddItem -> onNavigateToManualKeyEntry()
            is ItemListingEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(snackbarData = event.data)
            }

            is ItemListingEvent.NavigateToEditItem -> onNavigateToEditItemScreen(event.id)
            is ItemListingEvent.NavigateToAppSettings -> {
                intentManager.startAppSettingsActivity()
            }

            ItemListingEvent.NavigateToQuantVaultListing -> {
                intentManager.launchUri(
                    "https://play.google.com/store/apps/details?id=com.quantvault.app".toUri(),
                )
            }

            ItemListingEvent.NavigateToSyncInformation -> {
                intentManager.launchUri("https://QuantVault.com/help/totp-sync".toUri())
            }

            ItemListingEvent.NavigateToQuantVaultSettings -> {
                intentManager.startQuantVaultAccountSettings()
            }
        }
    }

    ItemListingDialogs(
        dialog = state.dialog,
        onDismissRequest = { viewModel.trySendAction(ItemListingAction.DialogDismiss) },
        onConfirmDeleteClick = {
            viewModel.trySendAction(ItemListingAction.ConfirmDeleteClick(it))
        },
    )

    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultMediumTopAppBar(
                title = stringResource(id = QuantVaultString.verification_codes),
                scrollBehavior = scrollBehavior,
                actions = {
                    QuantVaultSearchActionItem(
                        contentDescription = stringResource(id = QuantVaultString.search_codes),
                        isDisplayed = state.shouldShowSearchIcon,
                        onClick = onNavigateToSearch,
                    )
                },
            )
        },
        floatingActionButton = {
            QuantVaultExpandableFloatingActionButton(
                modifier = Modifier.testTag("AddItemButton"),
                items = persistentListOf(
                    ExpandableFabOption(
                        label = QuantVaultString.scan_a_qr_code.asText(),
                        icon = IconData.Local(
                            iconRes = QuantVaultDrawable.ic_camera_small,
                            contentDescription = QuantVaultString.scan_a_qr_code.asText(),
                            testTag = "ScanQRCodeButton",
                        ),
                        onFabOptionClick = { launcher.launch(Manifest.permission.CAMERA) },
                    ),
                    ExpandableFabOption(
                        label = QuantVaultString.enter_key_manually.asText(),
                        icon = IconData.Local(
                            iconRes = QuantVaultDrawable.ic_lock_encrypted_small,
                            contentDescription = QuantVaultString.enter_key_manually.asText(),
                            testTag = "EnterSetupKeyButton",
                        ),
                        onFabOptionClick = {
                            viewModel.trySendAction(ItemListingAction.EnterSetupKeyClick)
                        },
                    ),
                ),
                expandableFabIcon = ExpandableFabIcon(
                    icon = IconData.Local(
                        iconRes = QuantVaultDrawable.ic_plus_large,
                        contentDescription = QuantVaultString.add_item.asText(),
                        testTag = "AddItemButton",
                    ),
                    iconRotation = 45f,
                ),
            )
        },
        snackbarHost = { QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState) },
    ) {
        when (val currentState = state.viewState) {
            is ItemListingState.ViewState.Content -> {
                ItemListingContent(
                    state = currentState,
                    onItemClick = { viewModel.trySendAction(ItemListingAction.ItemClick(it)) },
                    onDropdownMenuClick = { action, item ->
                        viewModel.trySendAction(
                            ItemListingAction.DropdownMenuClick(menuAction = action, item = item),
                        )
                    },
                    onDownloadQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.DownloadQuantVaultClick)
                    },
                    onDismissDownloadQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.DownloadQuantVaultDismiss)
                    },
                    onSyncWithQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.SyncWithQuantVaultClick)
                    },
                    onDismissSyncWithQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.SyncWithQuantVaultDismiss)
                    },
                    onSyncLearnMoreClick = {
                        viewModel.trySendAction(ItemListingAction.SyncLearnMoreClick)
                    },
                    onSectionExpandedClick = {
                        viewModel.trySendAction(ItemListingAction.SectionExpandedClick(it))
                    },
                )
            }

            ItemListingState.ViewState.Loading -> {
                QuantVaultLoadingContent(modifier = Modifier.fillMaxSize())
            }

            is ItemListingState.ViewState.NoItems -> {
                EmptyItemListingContent(
                    actionCardState = currentState.actionCard,
                    onAddCodeClick = { launcher.launch(Manifest.permission.CAMERA) },
                    onDownloadQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.DownloadQuantVaultClick)
                    },
                    onDismissDownloadQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.DownloadQuantVaultDismiss)
                    },
                    onSyncWithQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.SyncWithQuantVaultClick)
                    },
                    onSyncLearnMoreClick = {
                        viewModel.trySendAction(ItemListingAction.SyncLearnMoreClick)
                    },
                    onDismissSyncWithQuantVaultClick = {
                        viewModel.trySendAction(ItemListingAction.SyncWithQuantVaultDismiss)
                    },
                )
            }
        }
    }
}

@Composable
private fun ItemListingDialogs(
    dialog: ItemListingState.DialogState?,
    onDismissRequest: () -> Unit,
    onConfirmDeleteClick: (itemId: String) -> Unit,
) {
    when (dialog) {
        ItemListingState.DialogState.Loading -> {
            QuantVaultLoadingDialog(
                text = stringResource(id = QuantVaultString.syncing),
            )
        }

        is ItemListingState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialog.title(),
                message = dialog.message(),
                onDismissRequest = onDismissRequest,
            )
        }

        is ItemListingState.DialogState.DeleteConfirmationPrompt -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = QuantVaultString.delete),
                message = dialog.message(),
                confirmButtonText = stringResource(id = QuantVaultString.okay),
                dismissButtonText = stringResource(id = QuantVaultString.cancel),
                onConfirmClick = {
                    onConfirmDeleteClick(dialog.itemId)
                },
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        null -> Unit
    }
}

@Suppress("LongMethod")
@Composable
private fun ItemListingContent(
    state: ItemListingState.ViewState.Content,
    onItemClick: (String) -> Unit,
    onDropdownMenuClick: (VaultDropdownMenuAction, VerificationCodeDisplayItem) -> Unit,
    onDownloadQuantVaultClick: () -> Unit,
    onDismissDownloadQuantVaultClick: () -> Unit,
    onSyncWithQuantVaultClick: () -> Unit,
    onDismissSyncWithQuantVaultClick: () -> Unit,
    onSyncLearnMoreClick: () -> Unit,
    onSectionExpandedClick: (SharedCodesDisplayState.SharedCodesAccountSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isLocalHeaderExpanded by rememberSaveable { mutableStateOf(value = true) }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        state.actionCard?.let {
            item(key = "action_card") {
                Spacer(modifier = Modifier.height(height = 12.dp))
                ActionCard(
                    actionCardState = it,
                    onDownloadQuantVaultClick = onDownloadQuantVaultClick,
                    onDownloadQuantVaultDismissClick = onDismissDownloadQuantVaultClick,
                    onSyncWithQuantVaultClick = onSyncWithQuantVaultClick,
                    onSyncWithQuantVaultDismissClick = onDismissSyncWithQuantVaultClick,
                    onSyncLearnMoreClick = onSyncLearnMoreClick,
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .animateItem(),
                )
            }
        }

        if (state.favoriteItems.isNotEmpty()) {
            item(key = "favorites_header") {
                Spacer(modifier = Modifier.height(height = 12.dp))
                QuantVaultListHeaderText(
                    label = stringResource(id = QuantVaultString.favorites),
                    supportingLabel = state.favoriteItems.count().toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp)
                        .animateItem(),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }

            itemsIndexed(
                items = state.favoriteItems,
                key = { _, it -> "favorite_item_${it.id}" },
            ) { index, item ->
                VaultVerificationCodeItem(
                    displayItem = item,
                    onItemClick = { onItemClick(item.authCode) },
                    onDropdownMenuClick = { action -> onDropdownMenuClick(action, item) },
                    cardStyle = state.favoriteItems.toListItemCardStyle(index = index),
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .fillMaxWidth()
                        .animateItem(),
                )
            }
        }

        if (state.shouldShowLocalHeader) {
            item(key = "local_items_header") {
                AuthenticatorExpandingHeader(
                    label = stringResource(
                        id = QuantVaultString.local_codes,
                        state.itemList.size,
                    ),
                    isExpanded = isLocalHeaderExpanded,
                    onClick = { isLocalHeaderExpanded = !isLocalHeaderExpanded },
                    onClickLabel = stringResource(
                        id = if (isLocalHeaderExpanded) {
                            QuantVaultString.local_items_are_expanded_click_to_collapse
                        } else {
                            QuantVaultString.local_items_are_collapsed_click_to_expand
                        },
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .animateItem(),
                )
            }
        } else if (state.sharedItems.isEmpty()) {
            item(key = "local_items_spacer") {
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
        }

        if (isLocalHeaderExpanded) {
            itemsIndexed(
                items = state.itemList,
                key = { _, it -> "local_item_${it.id}" },
            ) { index, item ->
                VaultVerificationCodeItem(
                    displayItem = item,
                    onItemClick = { onItemClick(item.authCode) },
                    onDropdownMenuClick = { action -> onDropdownMenuClick(action, item) },
                    cardStyle = state.itemList.toListItemCardStyle(index = index),
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .fillMaxWidth()
                        .animateItem(),
                )
            }
        }

        when (state.sharedItems) {
            is SharedCodesDisplayState.Codes -> {
                state.sharedItems.sections.forEachIndexed { _, section ->
                    item(key = "sharedSection_${section.id}") {
                        AuthenticatorExpandingHeader(
                            label = section.label(),
                            isExpanded = section.isExpanded,
                            onClick = {
                                onSectionExpandedClick(section)
                            },
                            onClickLabel = stringResource(
                                id = if (section.isExpanded) {
                                    QuantVaultString.items_expanded_click_to_collapse
                                } else {
                                    QuantVaultString.items_are_collapsed_click_to_expand
                                },
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .standardHorizontalMargin()
                                .animateItem(),
                        )
                    }
                    if (section.isExpanded) {
                        itemsIndexed(
                            items = section.codes,
                            key = { _, code -> "code_${code.id}" },
                        ) { index, item ->
                            VaultVerificationCodeItem(
                                displayItem = item,
                                onItemClick = { onItemClick(item.authCode) },
                                onDropdownMenuClick = { action ->
                                    onDropdownMenuClick(action, item)
                                },
                                cardStyle = section.codes.toListItemCardStyle(index = index),
                                modifier = Modifier
                                    .standardHorizontalMargin()
                                    .fillMaxWidth()
                                    .animateItem(),
                            )
                        }
                    }
                }
            }

            SharedCodesDisplayState.Error -> {
                item(key = "shared_codes_error") {
                    Spacer(modifier = Modifier.height(height = 8.dp))
                    Text(
                        text = stringResource(QuantVaultString.shared_codes_error),
                        color = QuantVaultTheme.colorScheme.text.secondary,
                        style = QuantVaultTheme.typography.bodySmall,
                        modifier = Modifier
                            .standardHorizontalMargin()
                            .padding(horizontal = 16.dp)
                            .animateItem(),
                    )
                }
            }
        }

        // Add a spacer item to prevent the FAB from hiding verification codes at the
        // bottom of the list
        item {
            Spacer(modifier = Modifier.height(height = 88.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/**
 * Displays the item listing screen with no existing items.
 */
@Suppress("LongMethod")
@Composable
fun EmptyItemListingContent(
    actionCardState: ItemListingState.ActionCardState?,
    onAddCodeClick: () -> Unit,
    onDownloadQuantVaultClick: () -> Unit,
    onDismissDownloadQuantVaultClick: () -> Unit,
    onSyncWithQuantVaultClick: () -> Unit,
    onSyncLearnMoreClick: () -> Unit,
    onDismissSyncWithQuantVaultClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = when (actionCardState) {
            null -> Arrangement.Center
            ItemListingState.ActionCardState.DownloadQuantVaultApp -> Arrangement.Top
            ItemListingState.ActionCardState.SyncWithQuantVault -> Arrangement.Top
        },
    ) {
        actionCardState?.let {
            Spacer(modifier = Modifier.height(height = 12.dp))
            ActionCard(
                actionCardState = it,
                onDownloadQuantVaultClick = onDownloadQuantVaultClick,
                onDownloadQuantVaultDismissClick = onDismissDownloadQuantVaultClick,
                onSyncWithQuantVaultClick = onSyncWithQuantVaultClick,
                onSyncWithQuantVaultDismissClick = onDismissSyncWithQuantVaultClick,
                onSyncLearnMoreClick = onSyncLearnMoreClick,
                modifier = Modifier.standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .standardHorizontalMargin(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = rememberVectorPainter(id = QuantVaultDrawable.ill_authenticator),
                contentDescription = stringResource(id = QuantVaultString.empty_item_list),
                modifier = Modifier
                    .size(size = 100.dp)
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = QuantVaultString.you_dont_have_items_to_display),
                style = QuantVaultTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = QuantVaultString.empty_item_list_instruction),
            )

            Spacer(modifier = Modifier.height(16.dp))
            QuantVaultFilledButton(
                modifier = Modifier
                    .testTag("AddCodeButton")
                    .fillMaxWidth(),
                label = stringResource(QuantVaultString.add_code),
                onClick = onAddCodeClick,
            )

            Spacer(modifier = Modifier.height(height = 12.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun ActionCard(
    actionCardState: ItemListingState.ActionCardState,
    onDownloadQuantVaultClick: () -> Unit,
    onDownloadQuantVaultDismissClick: () -> Unit,
    onSyncWithQuantVaultClick: () -> Unit,
    onSyncWithQuantVaultDismissClick: () -> Unit,
    onSyncLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (actionCardState) {
        ItemListingState.ActionCardState.DownloadQuantVaultApp -> {
            QuantVaultActionCard(
                modifier = modifier,
                cardSubtitle = stringResource(id = QuantVaultString.download_QuantVault_card_message),
                actionText = stringResource(id = QuantVaultString.download_now),
                cardTitle = stringResource(id = QuantVaultString.download_QuantVault_card_title),
                onActionClick = onDownloadQuantVaultClick,
                onDismissClick = onDownloadQuantVaultDismissClick,
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(id = QuantVaultDrawable.ic_shield),
                        contentDescription = null,
                        tint = QuantVaultTheme.colorScheme.icon.secondary,
                    )
                },
            )
        }

        ItemListingState.ActionCardState.SyncWithQuantVault -> {
            QuantVaultActionCard(
                modifier = modifier,
                cardTitle = stringResource(id = QuantVaultString.sync_with_the_QuantVault_app),
                actionText = stringResource(id = QuantVaultString.take_me_to_app_settings),
                onActionClick = onSyncWithQuantVaultClick,
                cardSubtitle = stringResource(
                    id = QuantVaultString.sync_with_QuantVault_action_card_message,
                ),
                onDismissClick = onSyncWithQuantVaultDismissClick,
                secondaryButton = QuantVaultButtonData(
                    label = QuantVaultString.learn_more.asText(),
                    onClick = onSyncLearnMoreClick,
                ),
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(id = QuantVaultDrawable.ic_refresh),
                        contentDescription = null,
                        tint = QuantVaultTheme.colorScheme.icon.secondary,
                    )
                },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EmptyListingContentPreview() {
    EmptyItemListingContent(
        modifier = Modifier.padding(horizontal = 16.dp),
        onAddCodeClick = { },
        actionCardState = ItemListingState.ActionCardState.DownloadQuantVaultApp,
        onDownloadQuantVaultClick = { },
        onDismissDownloadQuantVaultClick = { },
        onSyncWithQuantVaultClick = { },
        onSyncLearnMoreClick = { },
        onDismissSyncWithQuantVaultClick = { },
    )
}

@Composable
@Preview(showBackground = true)
private fun ContentPreview() {
    val email = "longemailaddress+verification+codes@email.com"
    QuantVaultTheme {
        ItemListingContent(
            state = ItemListingState.ViewState.Content(
                actionCard = null,
                favoriteItems = persistentListOf(),
                itemList = persistentListOf(
                    VerificationCodeDisplayItem(
                        id = "",
                        title = "Local item",
                        subtitle = "with a subtitle",
                        timeLeftSeconds = 20,
                        periodSeconds = 30,
                        alertThresholdSeconds = 15,
                        authCode = "123456",
                        favorite = false,
                        showMoveToQuantVault = true,
                        showOverflow = true,
                    ),
                ),
                sharedItems = SharedCodesDisplayState.Codes(
                    sections = persistentListOf(
                        SharedCodesDisplayState.SharedCodesAccountSection(
                            id = "id",
                            label = "$email | Bitawrden.eu (1)".asText(),
                            codes = persistentListOf(
                                VerificationCodeDisplayItem(
                                    id = "",
                                    title = "Shared item",
                                    subtitle = "with a subtitle",
                                    timeLeftSeconds = 15,
                                    periodSeconds = 30,
                                    alertThresholdSeconds = 15,
                                    authCode = "123456",
                                    favorite = false,
                                    showMoveToQuantVault = false,
                                    showOverflow = false,
                                ),
                            ),
                            isExpanded = true,
                            sortKey = email,
                        ),
                    ),
                ),
            ),
            onItemClick = { },
            onDropdownMenuClick = { _, _ -> },
            onDownloadQuantVaultClick = { },
            onDismissDownloadQuantVaultClick = { },
            onSyncWithQuantVaultClick = { },
            onDismissSyncWithQuantVaultClick = { },
            onSyncLearnMoreClick = { },
            onSectionExpandedClick = { },
        )
    }
}





package com.x8bit.bitwarden.ui.platform.feature.settings.autofill.blockautofill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toListItemCardStyle
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.fab.QuantVaultFloatingActionButton
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import kotlinx.collections.immutable.persistentListOf
import com.x8bit.bitwarden.R

/**
 * Displays the block autofill screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockAutoFillScreen(
    onNavigateBack: () -> Unit,
    viewModel: BlockAutoFillViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            BlockAutoFillEvent.NavigateBack -> onNavigateBack.invoke()
        }
    }

    BlockAutoFillDialogs(
        dialogState = state.dialog,
        onUriTextChange = { viewModel.trySendAction(BlockAutoFillAction.UriTextChange(uri = it)) },
        onSaveClick = { newUri, originalUri ->
            viewModel.trySendAction(
                BlockAutoFillAction.SaveUri(newUri = newUri, originalUri = originalUri),
            )
        },
        onDismissRequest = { viewModel.trySendAction(BlockAutoFillAction.DismissDialog) },
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.block_auto_fill),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                navigationIconContentDescription = stringResource(id = R.string.back),
                onNavigationIconClick = { viewModel.trySendAction(BlockAutoFillAction.BackClick) },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                QuantVaultFloatingActionButton(
                    onClick = { viewModel.trySendAction(BlockAutoFillAction.AddUriClick) },
                    painter = rememberVectorPainter(id = R.drawable.ic_plus_large),
                    contentDescription = stringResource(id = R.string.add_item),
                    modifier = Modifier.testTag(tag = "AddItemButton"),
                )
            }
        },
    ) {
        when (val viewState = state.viewState) {
            is BlockAutoFillState.ViewState.Content -> {
                BlockedAutofillContent(
                    viewState = viewState,
                    onEditUriClick = {
                        viewModel.trySendAction(BlockAutoFillAction.EditUriClick(it))
                    },
                    onRemoveClick = {
                        viewModel.trySendAction(BlockAutoFillAction.RemoveUriClick(it))
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            BlockAutoFillState.ViewState.Empty -> {
                BlockAutoFillNoItems(
                    addItemClickAction = {
                        viewModel.trySendAction(BlockAutoFillAction.AddUriClick)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun BlockedAutofillContent(
    viewState: BlockAutoFillState.ViewState.Content,
    onEditUriClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            Spacer(modifier = Modifier.height(height = 24.dp))
            Text(
                text = stringResource(
                    id = R.string.auto_fill_will_not_be_offered_for_these_ur_is,
                ),
                textAlign = TextAlign.Center,
                color = QuantVaultTheme.colorScheme.text.secondary,
                style = QuantVaultTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .animateItem(),
            )
            Spacer(modifier = Modifier.height(height = 24.dp))
        }

        itemsIndexed(
            items = viewState.blockedUris,
            key = { _, uri -> uri },
        ) { index, uri ->
            BlockAutoFillListItem(
                label = uri,
                onDeleteClick = { onRemoveClick(uri) },
                onEditClick = { onEditUriClick(uri) },
                cardStyle = viewState.blockedUris.toListItemCardStyle(index = index),
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth()
                    .animateItem(),
            )
        }
        item {
            Spacer(modifier = Modifier.height(height = 16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun BlockAutoFillDialogs(
    dialogState: BlockAutoFillState.DialogState? = null,
    onUriTextChange: (String) -> Unit,
    onSaveClick: (String, String?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is BlockAutoFillState.DialogState.AddEdit -> {
            AddEditBlockedUriDialog(
                uri = dialogState.uri,
                isEdit = dialogState.isEdit,
                errorMessage = dialogState.errorMessage?.invoke(),
                onUriChange = onUriTextChange,
                onDismissRequest = onDismissRequest,
                onCancelClick = onDismissRequest,
                onSaveClick = { newUri -> onSaveClick(newUri, dialogState.originalUri) },
            )
        }

        null -> Unit
    }
}

/**
 * No items view for the [BlockAutoFillScreen].
 */
@Composable
private fun BlockAutoFillNoItems(
    addItemClickAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(height = 24.dp))
        Image(
            painter = rememberVectorPainter(
                id = R.drawable.ill_blocked_uri,
            ),
            contentDescription = null,
            modifier = Modifier
                .size(size = 124.dp)
                .align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(height = 24.dp))

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.no_uris_blocked),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(
                id = R.string.auto_fill_will_not_be_offered_for_these_ur_is,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultFilledButton(
            label = stringResource(id = R.string.new_blocked_uri),
            onClick = addItemClickAction,
            icon = rememberVectorPainter(id = R.drawable.ic_plus_small),
            modifier = Modifier
                .wrapContentWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun BlockAutoFillListItem(
    label: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                paddingStart = 16.dp,
                paddingEnd = 4.dp,
            ),
    ) {
        Text(
            text = label,
            style = QuantVaultTheme.typography.bodyLarge,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(weight = 1f),
        )
        QuantVaultOverflowActionItem(
            menuItemDataList = persistentListOf(
                OverflowMenuItemData(
                    text = stringResource(id = R.string.edit),
                    onClick = onEditClick,
                ),
                OverflowMenuItemData(
                    text = stringResource(id = R.string.delete),
                    onClick = onDeleteClick,
                ),
            ),
            vectorIconRes = R.drawable.ic_ellipsis_horizontal,
            testTag = "Options",
        )
    }
}







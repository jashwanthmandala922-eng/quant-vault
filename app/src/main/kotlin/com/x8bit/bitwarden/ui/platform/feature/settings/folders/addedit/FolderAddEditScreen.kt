package com.x8bit.bitwarden.ui.platform.feature.settings.folders.addedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import kotlinx.collections.immutable.persistentListOf
import com.x8bit.bitwarden.R

/**
 * Displays the screen for adding or editing a folder item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun FolderAddEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: FolderAddEditViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var shouldShowConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is FolderAddEditEvent.NavigateBack -> onNavigateBack.invoke()
        }
    }

    FolderAddEditItemDialogs(
        dialogState = state.dialog,
        onDismissRequest = { viewModel.trySendAction(FolderAddEditAction.DismissDialog) },
    )

    if (shouldShowConfirmationDialog) {
        QuantVaultTwoButtonDialog(
            title = null,
            message = stringResource(id = R.string.do_you_really_want_to_delete),
            dismissButtonText = stringResource(id = R.string.cancel),
            confirmButtonText = stringResource(id = R.string.delete),
            onDismissClick = { shouldShowConfirmationDialog = false },
            onConfirmClick = {
                shouldShowConfirmationDialog = false
                viewModel.trySendAction(FolderAddEditAction.DeleteClick)
            },
            onDismissRequest = { shouldShowConfirmationDialog = false },
            confirmTextColor = QuantVaultTheme.colorScheme.status.error,
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = state.screenDisplayName.invoke(),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = { viewModel.trySendAction(FolderAddEditAction.CloseClick) },
                actions = {
                    QuantVaultTextButton(
                        label = stringResource(id = R.string.save),
                        onClick = { viewModel.trySendAction(FolderAddEditAction.SaveClick) },
                        modifier = Modifier.testTag("SaveButton"),
                    )
                    QuantVaultOverflowActionItem(
                        isVisible = state.shouldShowOverflowMenu,
                        menuItemDataList = persistentListOf(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.delete),
                                onClick = { shouldShowConfirmationDialog = true },
                            ),
                        ),
                    )
                },
            )
        },
    ) {
        when (val viewState = state.viewState) {
            is FolderAddEditState.ViewState.Content -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.height(height = 12.dp))
                    QuantVaultTextField(
                        label = stringResource(id = R.string.name),
                        value = viewState.folderName,
                        onValueChange = {
                            viewModel.trySendAction(FolderAddEditAction.NameTextChange(it))
                        },
                        textFieldTestTag = "FolderNameField",
                        cardStyle = CardStyle.Full,
                        modifier = Modifier
                            .standardHorizontalMargin()
                            .fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }

            is FolderAddEditState.ViewState.Error -> {
                QuantVaultErrorContent(
                    message = viewState.message(),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is FolderAddEditState.ViewState.Loading -> {
                QuantVaultLoadingContent(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun FolderAddEditItemDialogs(
    dialogState: FolderAddEditState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is FolderAddEditState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialogState.label())
        }

        is FolderAddEditState.DialogState.Error -> QuantVaultBasicDialog(
            title = stringResource(id = R.string.an_error_has_occurred),
            message = dialogState.message(),
            onDismissRequest = onDismissRequest,
            throwable = dialogState.throwable,
        )

        null -> Unit
    }
}







package com.quantvault.authenticator.ui.authenticator.feature.search

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.authenticator.ui.authenticator.feature.search.handlers.SearchHandlers
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.bottomDivider
import com.quantvault.ui.platform.components.appbar.QuantVaultSearchTopAppBar
import com.quantvault.ui.platform.components.appbar.NavigationIcon
import com.quantvault.ui.platform.components.dialog.QuantVaultBasicDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.quantvault.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString

/**
 * The search screen for authenticator items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSearchScreen(
    viewModel: ItemSearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val searchHandlers = remember(viewModel) { SearchHandlers.create(viewModel) }
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is ItemSearchEvent.NavigateBack -> onNavigateBack()
            is ItemSearchEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
            is ItemSearchEvent.NavigateToEditItem -> onNavigateToEdit(event.itemId)
        }
    }

    ItemSearchDialogs(
        dialogState = state.dialog,
        searchHandlers = searchHandlers,
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultSearchTopAppBar(
                modifier = Modifier
                    .testTag("SearchFieldEntry")
                    .bottomDivider(),
                searchTerm = state.searchTerm,
                placeholder = stringResource(id = QuantVaultString.search_codes),
                onSearchTermChange = searchHandlers.onSearchTermChange,
                scrollBehavior = scrollBehavior,
                clearIconContentDescription = stringResource(id = QuantVaultString.clear),
                navigationIcon = NavigationIcon(
                    navigationIcon = painterResource(id = QuantVaultDrawable.ic_back),
                    navigationIconContentDescription = stringResource(id = QuantVaultString.back),
                    onNavigationIconClick = searchHandlers.onBackClick,
                ),
            )
        },
        snackbarHost = { QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState) },
    ) {
        when (val viewState = state.viewState) {
            is ItemSearchState.ViewState.Content -> {
                ItemSearchContent(
                    viewState = viewState,
                    searchHandlers = searchHandlers,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is ItemSearchState.ViewState.Empty -> {
                ItemSearchEmptyContent(
                    viewState = viewState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ItemSearchDialogs(
    dialogState: ItemSearchState.DialogState?,
    searchHandlers: SearchHandlers,
) {
    when (dialogState) {
        is ItemSearchState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                throwable = dialogState.throwable,
                onDismissRequest = searchHandlers.onDismissDialog,
            )
        }

        is ItemSearchState.DialogState.DeleteConfirmationPrompt -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = QuantVaultString.delete),
                message = dialogState.message(),
                confirmButtonText = stringResource(id = QuantVaultString.okay),
                dismissButtonText = stringResource(id = QuantVaultString.cancel),
                onConfirmClick = { searchHandlers.onConfirmDeleteClick(dialogState.itemId) },
                onDismissClick = searchHandlers.onDismissDialog,
                onDismissRequest = searchHandlers.onDismissDialog,
            )
        }

        ItemSearchState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = stringResource(id = QuantVaultString.loading))
        }

        null -> Unit
    }
}





package com.x8bit.bitwarden.ui.vault.feature.movetoorganization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.content.QuantVaultErrorContent
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.x8bit.bitwarden.ui.vault.model.VaultCollection
import com.x8bit.bitwarden.R

/**
 * Displays the vault move to organization screen.
 */
@Composable
fun VaultMoveToOrganizationScreen(
    viewModel: VaultMoveToOrganizationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is VaultMoveToOrganizationEvent.NavigateBack -> onNavigateBack()
        }
    }
    VaultMoveToOrganizationScaffold(
        state = state,
        closeClick = { viewModel.trySendAction(VaultMoveToOrganizationAction.BackClick) },
        moveClick = { viewModel.trySendAction(VaultMoveToOrganizationAction.MoveClick) },
        dismissClick = { viewModel.trySendAction(VaultMoveToOrganizationAction.DismissClick) },
        organizationSelect = {
            viewModel.trySendAction(VaultMoveToOrganizationAction.OrganizationSelect(it))
        },
        collectionSelect = {
            viewModel.trySendAction(VaultMoveToOrganizationAction.CollectionSelect(it))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
private fun VaultMoveToOrganizationScaffold(
    state: VaultMoveToOrganizationState,
    closeClick: () -> Unit,
    moveClick: () -> Unit,
    dismissClick: () -> Unit,
    organizationSelect: (VaultMoveToOrganizationState.ViewState.Content.Organization) -> Unit,
    collectionSelect: (VaultCollection) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    when (val dialog = state.dialogState) {
        is VaultMoveToOrganizationState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = stringResource(id = R.string.an_error_has_occurred),
                message = dialog.message(),
                onDismissRequest = dismissClick,
                throwable = dialog.throwable,
            )
        }

        is VaultMoveToOrganizationState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = state.appBarText(),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = closeClick,
                actions = {
                    QuantVaultTextButton(
                        label = state.appBarButtonText(),
                        onClick = moveClick,
                        isEnabled = state.viewState is
                            VaultMoveToOrganizationState.ViewState.Content,
                        modifier = Modifier.testTag("MoveButton"),
                    )
                },
            )
        },
    ) {
        val modifier = Modifier
            .fillMaxSize()
        when (state.viewState) {
            is VaultMoveToOrganizationState.ViewState.Content -> {
                VaultMoveToOrganizationContent(
                    state = state.viewState,
                    showOnlyCollections = state.onlyShowCollections,
                    organizationSelect = organizationSelect,
                    collectionSelect = collectionSelect,
                    modifier = modifier,
                )
            }

            is VaultMoveToOrganizationState.ViewState.Error -> {
                QuantVaultErrorContent(
                    message = state.viewState.message(),
                    modifier = modifier,
                )
            }

            is VaultMoveToOrganizationState.ViewState.Loading -> {
                QuantVaultLoadingContent(modifier = modifier)
            }

            is VaultMoveToOrganizationState.ViewState.Empty -> {
                VaultMoveToOrganizationEmpty(modifier = modifier)
            }
        }
    }
}







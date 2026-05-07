package com.x8bit.bitwarden.ui.platform.feature.settings.vault

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.mirrorIfRtl
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.badge.NotificationBadge
import com.bitwarden.ui.platform.components.card.QuantVaultActionCard
import com.bitwarden.ui.platform.components.card.actionCardExitAnimation
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.row.QuantVaultTextRow
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Displays the vault settings screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToExportVault: () -> Unit,
    onNavigateToFolders: () -> Unit,
    onNavigateToImportLogins: () -> Unit,
    onNavigateToImportItems: () -> Unit,
    viewModel: VaultSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            VaultSettingsEvent.NavigateBack -> onNavigateBack()
            VaultSettingsEvent.NavigateToExportVault -> onNavigateToExportVault()
            VaultSettingsEvent.NavigateToFolders -> onNavigateToFolders()
            is VaultSettingsEvent.NavigateToImportVault -> onNavigateToImportLogins()
            is VaultSettingsEvent.NavigateToImportItems -> onNavigateToImportItems()
            is VaultSettingsEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.vault),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                navigationIconContentDescription = stringResource(id = R.string.back),
                onNavigationIconClick = { viewModel.trySendAction(VaultSettingsAction.BackClick) },
            )
        },
        snackbarHost = {
            QuantVaultSnackbarHost(
                QuantVaultHostState = snackbarHostState,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedVisibility(
                visible = state.showImportActionCard,
                label = "ImportLoginsActionCard",
                exit = actionCardExitAnimation(),
            ) {
                QuantVaultActionCard(
                    cardTitle = stringResource(id = R.string.import_saved_logins),
                    actionText = stringResource(R.string.get_started),
                    cardSubtitle = stringResource(R.string.use_a_computer_to_import_logins),
                    onActionClick = {
                        viewModel.trySendAction(VaultSettingsAction.ImportLoginsCardCtaClick)
                    },
                    onDismissClick = {
                        viewModel.trySendAction(VaultSettingsAction.ImportLoginsCardDismissClick)
                    },
                    leadingContent = {
                        NotificationBadge(notificationCount = 1)
                    },
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .padding(bottom = 16.dp),
                )
            }
            QuantVaultTextRow(
                text = stringResource(R.string.folders),
                onClick = { viewModel.trySendAction(VaultSettingsAction.FoldersButtonClick) },
                withDivider = false,
                cardStyle = CardStyle.Top(),
                modifier = Modifier
                    .testTag("FoldersLabel")
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )

            QuantVaultTextRow(
                text = stringResource(R.string.export_vault),
                onClick = { viewModel.trySendAction(VaultSettingsAction.ExportVaultClick) },
                withDivider = false,
                cardStyle = CardStyle.Middle(),
                modifier = Modifier
                    .testTag("ExportVaultLabel")
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )

            QuantVaultTextRow(
                text = stringResource(R.string.import_items),
                onClick = { viewModel.trySendAction(VaultSettingsAction.ImportItemsClick) },
                withDivider = false,
                cardStyle = CardStyle.Bottom,
                modifier = Modifier
                    .testTag("ImportItemsLinkItemView")
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            ) {
                if (state.showImportItemsChevron) {
                    Icon(
                        painter = rememberVectorPainter(id = R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = QuantVaultTheme.colorScheme.icon.primary,
                        modifier = Modifier
                            .mirrorIfRtl()
                            .size(size = 16.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}







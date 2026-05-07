package com.x8bit.bitwarden.ui.vault.feature.leaveorganization

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultFilledErrorButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.text.QuantVaultHyperTextLink
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.ui.vault.feature.leaveorganization.handlers.rememberLeaveOrganizationHandler
import com.x8bit.bitwarden.R

/**
 * Top-level composable for the Leave Organization screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveOrganizationScreen(
    onNavigateBack: () -> Unit,
    viewModel: LeaveOrganizationViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handlers = rememberLeaveOrganizationHandler(viewModel)

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            LeaveOrganizationEvent.NavigateBack -> onNavigateBack()
            is LeaveOrganizationEvent.LaunchUri -> {
                intentManager.launchUri(event.uri.toUri())
            }
        }
    }

    LeaveOrganizationDialogs(
        dialogState = state.dialogState,
        onDismissRequest = handlers.onDismissDialog,
        onDismissNoNetworkRequest = handlers.onDismissNoNetworkDialog,
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.leave_organization),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                navigationIconContentDescription = stringResource(id = R.string.back),
                onNavigationIconClick = handlers.onBackClick,
            )
        },
    ) {
        LeaveOrganizationContent(
            state = state,
            onLeaveClick = handlers.onLeaveClick,
            onHelpLinkClick = handlers.onHelpClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun LeaveOrganizationDialogs(
    dialogState: LeaveOrganizationState.DialogState?,
    onDismissRequest: () -> Unit,
    onDismissNoNetworkRequest: () -> Unit,
) {
    when (dialogState) {
        LeaveOrganizationState.DialogState.Loading -> {
            QuantVaultLoadingDialog(
                text = stringResource(id = R.string.loading),
            )
        }

        is LeaveOrganizationState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                throwable = dialogState.error,
                onDismissRequest = onDismissRequest,
            )
        }

        is LeaveOrganizationState.DialogState.NoNetwork -> {
            QuantVaultBasicDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                throwable = dialogState.error,
                onDismissRequest = onDismissNoNetworkRequest,
            )
        }

        null -> Unit
    }
}

@Suppress("LongMethod")
@Composable
private fun LeaveOrganizationContent(
    state: LeaveOrganizationState,
    onLeaveClick: () -> Unit,
    onHelpLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = rememberVectorPainter(id = R.drawable.ill_leave_organization),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .standardHorizontalMargin()
                .size(100.dp)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(
                id = R.string.are_you_sure_you_want_to_leave_organization,
            ),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(16.dp))
        QuantVaultHyperTextLink(
            annotatedResId = R.string.leave_organization_warning,
            annotationKey = "learnMore",
            accessibilityString = stringResource(R.string.learn_more),
            onClick = onHelpLinkClick,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            isExternalLink = true,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                id = R.string.contact_your_admin_to_regain_access,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultFilledErrorButton(
            label = stringResource(
                id = R.string.leave_organization_button,
                state.organizationName,
            ),
            onClick = onLeaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LeaveOrganizationScreen_preview() {
    QuantVaultTheme {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        QuantVaultScaffold(
            topBar = {
                QuantVaultTopAppBar(
                    title = "Leave organization",
                    scrollBehavior = scrollBehavior,
                    navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                    navigationIconContentDescription = "Back",
                    onNavigationIconClick = {},
                )
            },
        ) {
            LeaveOrganizationContent(
                state = LeaveOrganizationState(
                    organizationId = "",
                    organizationName = "Test Organization",
                    dialogState = null,
                ),
                onLeaveClick = {},
                onHelpLinkClick = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}







package com.x8bit.bitwarden.ui.auth.feature.expiredregistrationlink

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Top level screen component for the ExpiredRegistrationLink screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiredRegistrationLinkScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToStartRegistration: () -> Unit,
    viewModel: ExpiredRegistrationLinkViewModel = hiltViewModel(),
) {
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            ExpiredRegistrationLinkEvent.NavigateBack -> onNavigateBack()
            ExpiredRegistrationLinkEvent.NavigateToLogin -> onNavigateToLogin()
            ExpiredRegistrationLinkEvent.NavigateToStartRegistration -> {
                onNavigateToStartRegistration()
            }
        }
    }
    val sendCloseClicked = { viewModel.trySendAction(ExpiredRegistrationLinkAction.CloseClicked) }
    BackHandler(onBack = sendCloseClicked)
    QuantVaultScaffold(
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.create_account),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                    navigationIconContentDescription = stringResource(id = R.string.close),
                    onNavigationIconClick = sendCloseClicked,
                ),
            )
        },
    ) {
        ExpiredRegistrationLinkContent(
            onNavigateToLogin = {
                viewModel.trySendAction(ExpiredRegistrationLinkAction.GoToLoginClicked)
            },
            onNavigateToStartRegistration = {
                viewModel.trySendAction(ExpiredRegistrationLinkAction.RestartRegistrationClicked)
            },
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun ExpiredRegistrationLinkContent(
    onNavigateToLogin: () -> Unit,
    onNavigateToStartRegistration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.expired_link),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.please_restart_registration_or_try_logging_in),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantVaultFilledButton(
            label = stringResource(R.string.restart_registration),
            onClick = onNavigateToStartRegistration,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        QuantVaultOutlinedButton(
            label = stringResource(id = R.string.log_in_verb),
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpiredRegistrationLinkScreen_preview() {
    QuantVaultTheme {
        ExpiredRegistrationLinkContent(
            onNavigateToLogin = {},
            onNavigateToStartRegistration = {},
        )
    }
}







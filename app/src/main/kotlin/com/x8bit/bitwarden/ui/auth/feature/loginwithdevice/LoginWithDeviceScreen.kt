package com.x8bit.bitwarden.ui.auth.feature.loginwithdevice

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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.card.QuantVaultContentCard
import com.bitwarden.ui.platform.components.content.QuantVaultLoadingContent
import com.bitwarden.ui.platform.components.content.model.ContentBlockData
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.text.QuantVaultHyperTextLink
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import kotlinx.collections.immutable.persistentListOf
import com.x8bit.bitwarden.R

/**
 * The top level composable for the Login with Device screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWithDeviceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTwoFactorLogin: (emailAddress: String) -> Unit,
    viewModel: LoginWithDeviceViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            LoginWithDeviceEvent.NavigateBack -> onNavigateBack()

            is LoginWithDeviceEvent.NavigateToTwoFactorLogin -> {
                onNavigateToTwoFactorLogin(event.emailAddress)
            }
        }
    }

    LoginWithDeviceDialogs(
        state = state.dialogState,
        onDismissDialog = { viewModel.trySendAction(LoginWithDeviceAction.DismissDialog) },
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = state.toolbarTitle(),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = {
                    viewModel.trySendAction(LoginWithDeviceAction.CloseButtonClick)
                },
            )
        },
    ) {
        when (val viewState = state.viewState) {
            is LoginWithDeviceState.ViewState.Content -> {
                LoginWithDeviceScreenContent(
                    state = viewState,
                    onResendNotificationClick = {
                        viewModel.trySendAction(LoginWithDeviceAction.ResendNotificationClick)
                    },
                    onViewAllLogInOptionsClick = {
                        viewModel.trySendAction(LoginWithDeviceAction.ViewAllLogInOptionsClick)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            LoginWithDeviceState.ViewState.Loading -> QuantVaultLoadingContent(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun LoginWithDeviceScreenContent(
    state: LoginWithDeviceState.ViewState.Content,
    onResendNotificationClick: () -> Unit,
    onViewAllLogInOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(height = 24.dp))

        Text(
            text = state.title(),
            textAlign = TextAlign.Center,
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))

        Text(
            text = state.subtitle(),
            textAlign = TextAlign.Center,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))

        Text(
            text = state.description(),
            textAlign = TextAlign.Center,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultContentCard(
            contentItems = persistentListOf(
                ContentBlockData(
                    headerText = stringResource(id = R.string.fingerprint_phrase),
                    subtitleText = state.fingerprintPhrase,
                ),
            ),
            contentHeaderTextStyle = QuantVaultTheme.typography.titleMedium,
            contentSubtitleTextStyle = QuantVaultTheme.typography.sensitiveInfoSmall,
            contentSubtitleColor = QuantVaultTheme.colorScheme.text.codePink,
            modifier = Modifier
                .testTag(tag = "FingerprintPhraseValue")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        if (state.allowsResend) {
            Spacer(modifier = Modifier.height(height = 24.dp))
            QuantVaultOutlinedButton(
                label = stringResource(id = R.string.resend_notification),
                onClick = onResendNotificationClick,
                modifier = Modifier
                    .testTag(tag = "ResendNotificationButton")
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(height = 24.dp))

        Text(
            text = state.otherOptions(),
            textAlign = TextAlign.Center,
            style = QuantVaultTheme.typography.bodySmall,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))

        QuantVaultHyperTextLink(
            annotatedResId = R.string.need_another_option_view_all_login_options,
            annotationKey = "viewAll",
            accessibilityString = stringResource(id = R.string.view_all_login_options),
            onClick = onViewAllLogInOptionsClick,
            style = QuantVaultTheme.typography.bodySmall,
            modifier = Modifier
                .testTag(tag = "ViewAllLoginOptionsButton")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun LoginWithDeviceDialogs(
    state: LoginWithDeviceState.DialogState?,
    onDismissDialog: () -> Unit,
) {
    when (state) {
        is LoginWithDeviceState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = state.message(),
        )

        is LoginWithDeviceState.DialogState.Error -> QuantVaultBasicDialog(
            title = state.title?.invoke(),
            message = state.message(),
            throwable = state.error,
            onDismissRequest = onDismissDialog,
        )

        null -> Unit
    }
}







package com.x8bit.bitwarden.ui.auth.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.account.QuantVaultAccountSwitcher
import com.bitwarden.ui.platform.components.account.QuantVaultPlaceholderAccountActionItem
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.text.QuantVaultClickableText
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * The top level composable for the Login screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod")
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMasterPasswordHint: (String) -> Unit,
    onNavigateToEnterpriseSignOn: (String) -> Unit,
    onNavigateToLoginWithDevice: (emailAddress: String) -> Unit,
    onNavigateToTwoFactorLogin: (String, String?, Boolean) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            LoginEvent.NavigateBack -> onNavigateBack()
            is LoginEvent.NavigateToMasterPasswordHint -> {
                onNavigateToMasterPasswordHint(event.emailAddress)
            }

            is LoginEvent.NavigateToEnterpriseSignOn -> {
                onNavigateToEnterpriseSignOn(event.emailAddress)
            }

            is LoginEvent.NavigateToLoginWithDevice -> {
                onNavigateToLoginWithDevice(event.emailAddress)
            }

            is LoginEvent.NavigateToTwoFactorLogin -> {
                onNavigateToTwoFactorLogin(
                    event.emailAddress,
                    event.password,
                    event.isNewDeviceVerification,
                )
            }
        }
    }

    LoginDialogs(
        dialogState = state.dialogState,
        onDismissRequest = { viewModel.trySendAction(LoginAction.ErrorDialogDismiss) },
    )

    val isAccountButtonVisible = state.accountSummaries.isNotEmpty()
    var isAccountMenuVisible by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.app_name),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = { viewModel.trySendAction(LoginAction.CloseButtonClick) },
                actions = {
                    if (isAccountButtonVisible) {
                        QuantVaultPlaceholderAccountActionItem(
                            onClick = { isAccountMenuVisible = !isAccountMenuVisible },
                        )
                    }
                    QuantVaultOverflowActionItem(
                        menuItemDataList = persistentListOf(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.get_password_hint),
                                onClick = {
                                    viewModel.trySendAction(LoginAction.MasterPasswordHintClick)
                                },
                            ),
                        ),
                    )
                },
            )
        },
        overlay = {
            QuantVaultAccountSwitcher(
                isVisible = isAccountMenuVisible,
                accountSummaries = state.accountSummaries.toImmutableList(),
                onSwitchAccountClick = {
                    viewModel.trySendAction(LoginAction.SwitchAccountClick(it))
                },
                onLockAccountClick = { viewModel.trySendAction(LoginAction.LockAccountClick(it)) },
                onLogoutAccountClick = {
                    viewModel.trySendAction(LoginAction.LogoutAccountClick(it))
                },
                onAddAccountClick = { viewModel.trySendAction(LoginAction.AddAccountClick) },
                onDismissRequest = { isAccountMenuVisible = false },
                topAppBarScrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxSize(),
            )
        },
    ) {
        LoginScreenContent(
            state = state,
            onPasswordInputChanged = {
                viewModel.trySendAction(LoginAction.PasswordInputChanged(it))
            },
            onMasterPasswordClick = {
                viewModel.trySendAction(LoginAction.MasterPasswordHintClick)
            },
            onLoginButtonClick = {
                keyboardController?.hide()
                viewModel.trySendAction(LoginAction.LoginButtonClick)
            },
            onLoginWithDeviceClick = {
                viewModel.trySendAction(LoginAction.LoginWithDeviceButtonClick)
            },
            onSingleSignOnClick = { viewModel.trySendAction(LoginAction.SingleSignOnClick) },
            onNotYouButtonClick = { viewModel.trySendAction(LoginAction.NotYouButtonClick) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun LoginDialogs(
    dialogState: LoginState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is LoginState.DialogState.Error -> QuantVaultBasicDialog(
            title = dialogState.title?.invoke(),
            message = dialogState.message(),
            throwable = dialogState.error,
            onDismissRequest = onDismissRequest,
        )

        is LoginState.DialogState.Loading -> QuantVaultLoadingDialog(
            text = dialogState.message(),
        )

        null -> Unit
    }
}

@Suppress("LongMethod")
@Composable
private fun LoginScreenContent(
    state: LoginState,
    onPasswordInputChanged: (String) -> Unit,
    onMasterPasswordClick: () -> Unit,
    onLoginButtonClick: () -> Unit,
    onLoginWithDeviceClick: () -> Unit,
    onSingleSignOnClick: () -> Unit,
    onNotYouButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))
        QuantVaultPasswordField(
            autoFocus = true,
            value = state.passwordInput,
            onValueChange = onPasswordInputChanged,
            label = stringResource(id = R.string.master_password),
            showPasswordTestTag = "PasswordVisibilityToggle",
            supportingContentPadding = PaddingValues(),
            supportingContent = {
                QuantVaultClickableText(
                    label = stringResource(id = R.string.get_master_passwordword_hint),
                    onClick = onMasterPasswordClick,
                    style = QuantVaultTheme.typography.labelMedium,
                    innerPadding = PaddingValues(all = 16.dp),
                    cornerSize = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(tag = "GetMasterPasswordHintLabel"),
                )
            },
            passwordFieldTestTag = "MasterPasswordEntry",
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 24.dp))

        QuantVaultFilledButton(
            label = stringResource(id = R.string.log_in_with_master_password),
            onClick = onLoginButtonClick,
            isEnabled = state.isLoginButtonEnabled,
            modifier = Modifier
                .testTag("LogInWithMasterPasswordButton")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.shouldShowLoginWithDevice) {
            QuantVaultOutlinedButton(
                label = stringResource(id = R.string.log_in_with_device),
                icon = rememberVectorPainter(id = R.drawable.ic_mobile_small),
                onClick = onLoginWithDeviceClick,
                modifier = Modifier
                    .testTag("LogInWithAnotherDeviceButton")
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        QuantVaultOutlinedButton(
            label = stringResource(id = R.string.log_in_sso),
            icon = rememberVectorPainter(id = R.drawable.ic_enterprise_small),
            onClick = onSingleSignOnClick,
            modifier = Modifier
                .testTag("LogInWithSsoButton")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(
                id = R.string.logging_in_as_x_on_y,
                state.emailAddress,
                state.environmentLabel,
            ),
            textAlign = TextAlign.Center,
            style = QuantVaultTheme.typography.bodySmall,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier
                .testTag("LoggingInAsLabel")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        QuantVaultClickableText(
            label = stringResource(id = R.string.not_you),
            onClick = onNotYouButtonClick,
            style = QuantVaultTheme.typography.labelMedium,
            innerPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            modifier = Modifier
                .standardHorizontalMargin()
                .align(alignment = Alignment.CenterHorizontally)
                .testTag("NotYouLabel"),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}






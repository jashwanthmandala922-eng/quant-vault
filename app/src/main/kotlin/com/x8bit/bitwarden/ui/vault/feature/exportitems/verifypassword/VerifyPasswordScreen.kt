package com.x8bit.bitwarden.ui.vault.feature.exportitems.verifypassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.providerevents.exception.ImportCredentialsCancellationException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.cxf.manager.CredentialExchangeCompletionManager
import com.quantvault.cxf.manager.model.ExportCredentialsResult
import com.quantvault.cxf.ui.composition.LocalCredentialExchangeCompletionManager
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.vault.feature.exportitems.component.AccountSummaryListItem
import com.x8bit.bitwarden.ui.vault.feature.exportitems.component.ExportItemsScaffold
import com.x8bit.bitwarden.ui.vault.feature.exportitems.model.AccountSelectionListItem
import com.x8bit.bitwarden.ui.vault.feature.exportitems.verifypassword.handlers.rememberVerifyPasswordHandler
import com.x8bit.bitwarden.R

/**
 * Top level composable for the Verify Password screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordVerified: (userId: String) -> Unit,
    viewModel: VerifyPasswordViewModel = hiltViewModel(),
    credentialExchangeCompletionManager: CredentialExchangeCompletionManager =
        LocalCredentialExchangeCompletionManager.current,
    snackbarHostState: QuantVaultSnackbarHostState = rememberQuantVaultSnackbarHostState(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val handler = rememberVerifyPasswordHandler(viewModel)

    EventsEffect(viewModel) { event ->
        when (event) {
            VerifyPasswordEvent.NavigateBack -> onNavigateBack()
            VerifyPasswordEvent.CancelExport -> {
                credentialExchangeCompletionManager
                    .completeCredentialExport(
                        exportResult = ExportCredentialsResult.Failure(
                            error = ImportCredentialsCancellationException(
                                errorMessage = "User cancelled import.",
                            ),
                        ),
                    )
            }

            is VerifyPasswordEvent.PasswordVerified -> {
                onPasswordVerified(event.userId)
            }

            is VerifyPasswordEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(event.data)
            }
        }
    }

    VerifyPasswordDialogs(
        dialog = state.dialog,
        onDismiss = handler.onDismissDialog,
    )

    ExportItemsScaffold(
        navIcon = rememberVectorPainter(
            id = if (state.hasOtherAccounts) {
                R.drawable.ic_back
            } else {
                R.drawable.ic_close
            },
        ),
        onNavigationIconClick = handler.onNavigateBackClick,
        navigationIconContentDescription = stringResource(R.string.back),
        scrollBehavior = scrollBehavior,
        modifier = Modifier.fillMaxSize(),
    ) {
        VerifyPasswordContent(
            state = state,
            onInputChanged = handler.onInputChanged,
            onContinueClick = handler.onContinueClick,
            onResendCodeClick = handler.onSendCodeClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun VerifyPasswordDialogs(
    dialog: VerifyPasswordState.DialogState?,
    onDismiss: () -> Unit,
) {
    when (dialog) {
        is VerifyPasswordState.DialogState.General -> {
            QuantVaultBasicDialog(
                title = dialog.title(),
                message = dialog.message(),
                throwable = dialog.error,
                onDismissRequest = onDismiss,
            )
        }

        is VerifyPasswordState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }
}

@Suppress("LongMethod")
@Composable
private fun VerifyPasswordContent(
    state: VerifyPasswordState,
    onInputChanged: (String) -> Unit,
    onContinueClick: () -> Unit,
    onResendCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = state.title(),
            textAlign = TextAlign.Center,
            style = QuantVaultTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        state.subtext?.let { subtext ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtext(),
                textAlign = TextAlign.Center,
                style = QuantVaultTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        Spacer(Modifier.height(16.dp))

        AccountSummaryListItem(
            item = state.accountSummaryListItem,
            cardStyle = CardStyle.Full,
            clickable = false,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(Modifier.height(16.dp))

        if (state.showResendCodeButton) {
            QuantVaultPasswordField(
                label = stringResource(id = R.string.verification_code),
                value = state.input,
                onValueChange = onInputChanged,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.isContinueButtonEnabled) {
                            onContinueClick()
                        } else {
                            defaultKeyboardAction(ImeAction.Done)
                        }
                    },
                ),
                autoFocus = true,
                cardStyle = CardStyle.Full,
                passwordFieldTestTag = "VerificationCodeEntry",
                showPasswordTestTag = "VerificationCodeVisibilityToggle",
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        } else {
            QuantVaultPasswordField(
                label = stringResource(R.string.master_password),
                value = state.input,
                onValueChange = onInputChanged,
                showPasswordTestTag = "PasswordVisibilityToggle",
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.isContinueButtonEnabled) {
                            onContinueClick()
                        } else {
                            defaultKeyboardAction(ImeAction.Done)
                        }
                    },
                ),
                autoFocus = true,
                supportingText = stringResource(R.string.vault_locked_master_password),
                passwordFieldTestTag = "MasterPasswordEntry",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        Spacer(Modifier.height(16.dp))

        QuantVaultFilledButton(
            label = stringResource(R.string.continue_text),
            onClick = onContinueClick,
            isEnabled = state.isContinueButtonEnabled,
            modifier = Modifier
                .testTag("ContinueImportButton")
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        if (state.showResendCodeButton) {
            QuantVaultOutlinedButton(
                label = stringResource(R.string.resend_code),
                onClick = onResendCodeClick,
                modifier = Modifier
                    .testTag("ResendTOTPCodeButton")
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        Spacer(Modifier.height(12.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun VerifyPasswordContent_MasterPassword_preview() {
    val accountSummaryListItem = AccountSelectionListItem(
        userId = "userId",
        isItemRestricted = false,
        avatarColorHex = "#FF0000",
        initials = "JD",
        email = "john.doe@example.com",
    )
    val state = VerifyPasswordState(
        title = R.string.verify_your_master_password.asText(),
        subtext = null,
        hasOtherAccounts = true,
        accountSummaryListItem = accountSummaryListItem,
    )
    ExportItemsScaffold(
        navIcon = rememberVectorPainter(
            R.drawable.ic_back,
        ),
        onNavigationIconClick = {},
        navigationIconContentDescription = stringResource(R.string.back),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        modifier = Modifier.fillMaxSize(),
    ) {
        VerifyPasswordContent(
            state = state,
            onInputChanged = {},
            onContinueClick = {},
            onResendCodeClick = {},
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun VerifyPasswordContent_Otp_preview() {
    val accountSummaryListItem = AccountSelectionListItem(
        userId = "userId",
        isItemRestricted = false,
        avatarColorHex = "#FF0000",
        initials = "JD",
        email = "john.doe@example.com",
    )
    val state = VerifyPasswordState(
        title = R.string.verify_your_account_email_address.asText(),
        subtext = R.string.enter_the_6_digit_code_that_was_emailed_to_the_address_below
            .asText(),
        accountSummaryListItem = accountSummaryListItem,
        showResendCodeButton = true,
        hasOtherAccounts = true,
    )
    ExportItemsScaffold(
        navIcon = rememberVectorPainter(
            R.drawable.ic_back,
        ),
        onNavigationIconClick = {},
        navigationIconContentDescription = stringResource(R.string.back),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        modifier = Modifier.fillMaxSize(),
    ) {
        VerifyPasswordContent(
            state = state,
            onInputChanged = {},
            onContinueClick = {},
            onResendCodeClick = {},
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}








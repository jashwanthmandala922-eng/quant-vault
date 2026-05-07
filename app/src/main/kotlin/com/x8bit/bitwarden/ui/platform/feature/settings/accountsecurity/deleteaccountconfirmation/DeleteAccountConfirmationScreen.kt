package com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity.deleteaccountconfirmation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultFilledErrorButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Displays the delete account confirmation screen.
 */
@Composable
fun DeleteAccountConfirmationScreen(
    viewModel: DeleteAccountConfirmationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            DeleteAccountConfirmationEvent.NavigateBack -> onNavigateBack()
        }
    }

    DeleteAccountConfirmationDialogs(
        dialogState = state.dialog,
        onDeleteAccountAcknowledge = {
            viewModel.trySendAction(DeleteAccountConfirmationAction.DeleteAccountAcknowledge)
        },
        onDismissDialog = {
            viewModel.trySendAction(DeleteAccountConfirmationAction.DismissDialog)
        },
    )

    DeleteAccountConfirmationScaffold(
        state = state,
        onCloseClick = { viewModel.trySendAction(DeleteAccountConfirmationAction.CloseClick) },
        onDeleteAccountClick = {
            viewModel.trySendAction(DeleteAccountConfirmationAction.DeleteAccountClick)
        },
        onResendCodeClick = {
            viewModel.trySendAction(DeleteAccountConfirmationAction.ResendCodeClick)
        },
        onVerificationCodeTextChange = {
            viewModel.trySendAction(DeleteAccountConfirmationAction.VerificationCodeTextChange(it))
        },
    )
}

@Composable
private fun DeleteAccountConfirmationDialogs(
    dialogState: DeleteAccountConfirmationState.DeleteAccountConfirmationDialog?,
    onDismissDialog: () -> Unit,
    onDeleteAccountAcknowledge: () -> Unit,
) {
    when (dialogState) {
        is DeleteAccountConfirmationState.DeleteAccountConfirmationDialog.DeleteSuccess -> {
            QuantVaultBasicDialog(
                title = null,
                message = dialogState.message(),
                onDismissRequest = onDeleteAccountAcknowledge,
            )
        }

        is DeleteAccountConfirmationState.DeleteAccountConfirmationDialog.Error -> {
            QuantVaultBasicDialog(
                title = dialogState.title(),
                message = dialogState.message(),
                throwable = dialogState.error,
                onDismissRequest = onDismissDialog,
            )
        }

        is DeleteAccountConfirmationState.DeleteAccountConfirmationDialog.Loading -> {
            QuantVaultLoadingDialog(text = dialogState.title())
        }

        null -> Unit
    }
}

@Composable
private fun DeleteAccountConfirmationContent(
    state: DeleteAccountConfirmationState,
    onDeleteAccountClick: () -> Unit,
    onResendCodeClick: () -> Unit,
    onVerificationCodeTextChange: (verificationCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(id = R.string.a_verification_code_was_sent_to_your_email),
            textAlign = TextAlign.Start,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantVaultPasswordField(
            value = state.verificationCode,
            onValueChange = onVerificationCodeTextChange,
            label = stringResource(id = R.string.verification_code),
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            autoFocus = true,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.confirm_your_identity),
            textAlign = TextAlign.Start,
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        QuantVaultFilledErrorButton(
            label = stringResource(id = R.string.delete_account),
            onClick = onDeleteAccountClick,
            isEnabled = state.verificationCode.isNotBlank(),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        QuantVaultOutlinedButton(
            label = stringResource(id = R.string.resend_code),
            onClick = onResendCodeClick,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountConfirmationScaffold(
    state: DeleteAccountConfirmationState,
    onCloseClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onResendCodeClick: () -> Unit,
    onVerificationCodeTextChange: (verificationCode: String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.verification_code),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = onCloseClick,
            )
        },
    ) {
        DeleteAccountConfirmationContent(
            state = state,
            onDeleteAccountClick = onDeleteAccountClick,
            onResendCodeClick = onResendCodeClick,
            onVerificationCodeTextChange = onVerificationCodeTextChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteAccountConfirmationScreen_preview() {
    QuantVaultTheme {
        DeleteAccountConfirmationScaffold(
            state = DeleteAccountConfirmationState(
                dialog = null,
                verificationCode = "123456",
            ),
            onCloseClick = {},
            onDeleteAccountClick = {},
            onResendCodeClick = {},
            onVerificationCodeTextChange = {},
        )
    }
}







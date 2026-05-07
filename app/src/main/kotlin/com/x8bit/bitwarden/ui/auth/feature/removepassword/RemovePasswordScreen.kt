package com.x8bit.bitwarden.ui.auth.feature.removepassword

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.R

/**
 * The top level composable for the Remove Password screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemovePasswordScreen(
    viewModel: RemovePasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    RemovePasswordDialogs(
        dialogState = state.dialogState,
        onDismissRequest = { viewModel.trySendAction(RemovePasswordAction.DialogDismiss) },
        onConfirmLeaveClick = {
            viewModel.trySendAction(RemovePasswordAction.ConfirmLeaveOrganizationClick)
        },
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.remove_master_password),
                scrollBehavior = scrollBehavior,
                navigationIcon = null,
            )
        },
    ) {
        RemovePasswordScreenContent(
            state = state,
            onContinueClick = { viewModel.trySendAction(RemovePasswordAction.ContinueClick) },
            onInputChanged = { viewModel.trySendAction(RemovePasswordAction.InputChanged(it)) },
            onLeaveOrganizationClick = {
                viewModel.trySendAction(RemovePasswordAction.LeaveOrganizationClick)
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun RemovePasswordScreenContent(
    state: RemovePasswordState,
    onContinueClick: () -> Unit,
    onInputChanged: (String) -> Unit,
    onLeaveOrganizationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))

        Text(
            text = state.description(),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))
        Text(
            text = state.labelOrg(),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Text(
            text = state.orgName?.invoke().orEmpty(),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        Text(
            text = state.labelDomain(),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Text(
            text = state.domainName?.invoke().orEmpty(),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.secondary,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        QuantVaultPasswordField(
            label = stringResource(id = R.string.master_password),
            value = state.input,
            onValueChange = onInputChanged,
            showPasswordTestTag = "PasswordVisibilityToggle",
            passwordFieldTestTag = "MasterPasswordEntry",
            autoFocus = true,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(24.dp))

        QuantVaultFilledButton(
            label = stringResource(id = R.string.continue_text),
            onClick = onContinueClick,
            isEnabled = state.input.isNotEmpty(),
            modifier = Modifier
                .testTag(tag = "ContinueButton")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        QuantVaultOutlinedButton(
            label = stringResource(id = R.string.leave_organization),
            onClick = onLeaveOrganizationClick,
            modifier = Modifier
                .testTag("LeaveOrganizationButton")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun RemovePasswordDialogs(
    dialogState: RemovePasswordState.DialogState?,
    onDismissRequest: () -> Unit,
    onConfirmLeaveClick: () -> Unit,
) {
    when (dialogState) {
        is RemovePasswordState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = onDismissRequest,
            )
        }

        is RemovePasswordState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialogState.title())
        }

        is RemovePasswordState.DialogState.LeaveConfirmationPrompt -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(id = R.string.leave_organization),
                message = dialogState.message.invoke(),
                confirmButtonText = stringResource(id = R.string.confirm),
                dismissButtonText = stringResource(id = R.string.cancel),
                onConfirmClick = onConfirmLeaveClick,
                onDismissClick = onDismissRequest,
                onDismissRequest = onDismissRequest,
            )
        }

        null -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun RemovePasswordScreen_preview() {
    QuantVaultTheme {
        RemovePasswordScreenContent(
            state = RemovePasswordState(
                input = "",
                description =
                    ("A master password is no longer required " +
                        "for members of the following organization. " +
                        "Please confirm the domain below with your " +
                        "organization administrator.").asText(),
                labelOrg = "Organization name".asText(),
                orgName = "Organization name".asText(),
                labelDomain = "Key Connector domain".asText(),
                domainName = "http://localhost:8080".asText(),
                dialogState = null,
                organizationId = null,
            ),
            onContinueClick = { },
            onInputChanged = { },
            onLeaveOrganizationClick = { },
        )
    }
}







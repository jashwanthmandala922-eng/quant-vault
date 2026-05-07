package com.x8bit.bitwarden.ui.auth.feature.masterpasswordhint

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.x8bit.bitwarden.R

/**
 * The top level composable for the Login screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterPasswordHintScreen(
    onNavigateBack: () -> Unit,
    viewModel: MasterPasswordHintViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            MasterPasswordHintEvent.NavigateBack -> onNavigateBack()
        }
    }

    when (val dialogState = state.dialog) {
        is MasterPasswordHintState.DialogState.PasswordHintSent -> {
            QuantVaultBasicDialog(
                title = stringResource(id = R.string.password_hint),
                message = stringResource(id = R.string.password_hint_alert),
                onDismissRequest = {
                    viewModel.trySendAction(MasterPasswordHintAction.DismissDialog)
                },
            )
        }

        is MasterPasswordHintState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialogState.message())
        }

        is MasterPasswordHintState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialogState
                    .title
                    ?.invoke()
                    ?: stringResource(id = R.string.an_error_has_occurred),
                message = dialogState.message(),
                throwable = dialogState.error,
                onDismissRequest = {
                    viewModel.trySendAction(MasterPasswordHintAction.DismissDialog)
                },
            )
        }

        null -> Unit
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.password_hint),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = {
                    viewModel.trySendAction(MasterPasswordHintAction.CloseClick)
                },
                actions = {
                    QuantVaultTextButton(
                        label = stringResource(id = R.string.submit),
                        onClick = { viewModel.trySendAction(MasterPasswordHintAction.SubmitClick) },
                        modifier = Modifier.testTag("SubmitButton"),
                    )
                },
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(height = 12.dp))
            QuantVaultTextField(
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
                value = state.emailInput,
                onValueChange = {
                    viewModel.trySendAction(MasterPasswordHintAction.EmailInputChange(it))
                },
                label = stringResource(id = R.string.email_address),
                keyboardType = KeyboardType.Email,
                textFieldTestTag = "MasterPasswordHintEmailField",
                supportingText = stringResource(id = R.string.enter_email_for_hint),
                cardStyle = CardStyle.Full,
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}







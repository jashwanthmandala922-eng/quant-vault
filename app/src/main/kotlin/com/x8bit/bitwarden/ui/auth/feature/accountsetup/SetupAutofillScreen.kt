package com.x8bit.bitwarden.ui.auth.feature.accountsetup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.image.QuantVaultGifImage
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.toggle.QuantVaultSwitch
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.manager.util.startSystemAutofillSettingsActivity
import com.bitwarden.ui.platform.model.WindowSize
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.platform.util.rememberWindowSize
import com.x8bit.bitwarden.ui.auth.feature.accountsetup.handlers.rememberSetupAutoFillHandler
import com.x8bit.bitwarden.R

/**
 * Top level composable for the Auto-fill setup screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupAutoFillScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBrowserAutofill: () -> Unit,
    intentManager: IntentManager = LocalIntentManager.current,
    viewModel: SetupAutoFillViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handler = rememberSetupAutoFillHandler(viewModel = viewModel)
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            SetupAutoFillEvent.NavigateToAutofillSettings -> {
                val showFallback = !intentManager.startSystemAutofillSettingsActivity()
                if (showFallback) {
                    handler.sendAutoFillServiceFallback.invoke()
                }
            }

            SetupAutoFillEvent.NavigateBack -> onNavigateBack()
            SetupAutoFillEvent.NavigateToBrowserAutofill -> onNavigateToBrowserAutofill()
        }
    }
    when (state.dialogState) {
        is SetupAutoFillDialogState.AutoFillFallbackDialog -> {
            QuantVaultBasicDialog(
                title = null,
                message = stringResource(id = R.string.Quant Vault_autofill_go_to_settings),
                onDismissRequest = handler.onDismissDialog,
            )
        }

        is SetupAutoFillDialogState.TurnOnLaterDialog -> {
            QuantVaultTwoButtonDialog(
                title = stringResource(R.string.turn_on_autofill_later),
                message = stringResource(
                    id = R.string.return_to_complete_this_step_anytime_in_settings,
                ),
                confirmButtonText = stringResource(id = R.string.confirm),
                dismissButtonText = stringResource(id = R.string.cancel),
                onConfirmClick = handler.onConfirmTurnOnLaterClick,
                onDismissClick = handler.onDismissDialog,
                onDismissRequest = handler.onDismissDialog,
            )
        }

        null -> Unit
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(
                    id = if (state.isInitialSetup) {
                        R.string.account_setup
                    } else {
                        R.string.autofill_setup
                    },
                ),
                scrollBehavior = scrollBehavior,
                navigationIcon = if (state.isInitialSetup) {
                    null
                } else {
                    NavigationIcon(
                        navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                        navigationIconContentDescription = stringResource(
                            id = R.string.close,
                        ),
                        onNavigationIconClick = {
                            viewModel.trySendAction(SetupAutoFillAction.CloseClick)
                        },
                    )
                },
            )
        },
    ) {
        SetupAutoFillContent(
            state = state,
            onAutofillServiceChanged = { handler.onAutofillServiceChanged(it) },
            onContinueClick = handler.onContinueClick,
            onTurnOnLaterClick = handler.onTurnOnLaterClick,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun SetupAutoFillContent(
    state: SetupAutoFillState,
    onAutofillServiceChanged: (Boolean) -> Unit,
    onContinueClick: () -> Unit,
    onTurnOnLaterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(Modifier.height(8.dp))
        SetupAutoFillContentHeader(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantVaultSwitch(
            label = stringResource(
                R.string.autofill_services,
            ),
            isChecked = state.autofillEnabled,
            onCheckedChange = onAutofillServiceChanged,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantVaultFilledButton(
            label = stringResource(id = R.string.continue_text),
            onClick = onContinueClick,
            isEnabled = state.autofillEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.isInitialSetup) {
            QuantVaultOutlinedButton(
                label = stringResource(R.string.turn_on_later),
                onClick = onTurnOnLaterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun SetupAutoFillContentHeader(
    modifier: Modifier = Modifier,
) {
    when (rememberWindowSize()) {
        WindowSize.Compact -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OrderedHeaderContent()
            }
        }

        WindowSize.Medium -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrderedHeaderContent()
            }
        }
    }
}

@Composable
private fun OrderedHeaderContent() {
    QuantVaultGifImage(
        resId = R.drawable.gif_setup_autofill,
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp,
                ),
            )
            .size(
                width = 230.dp,
                height = 280.dp,
            ),
    )
    Spacer(modifier = Modifier.size(24.dp))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.turn_on_autofill),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.use_autofill_to_log_into_your_accounts),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            // Apply similar line breaks to design
            modifier = Modifier.sizeIn(maxWidth = 300.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SetupAutoFillContentDisabled_preview() {
    QuantVaultTheme {
        SetupAutoFillContent(
            state = SetupAutoFillState(
                userId = "disputationi",
                dialogState = null,
                autofillEnabled = false,
                isInitialSetup = true,
            ),
            onAutofillServiceChanged = {},
            onContinueClick = {},
            onTurnOnLaterClick = {},
        )
    }
}

@PreviewScreenSizes
@Composable
private fun SetupAutoFillContentEnabled_preview() {
    QuantVaultTheme {
        SetupAutoFillContent(
            state = SetupAutoFillState(
                userId = "disputationi",
                dialogState = null,
                autofillEnabled = true,
                isInitialSetup = true,
            ),
            onAutofillServiceChanged = {},
            onContinueClick = {},
            onTurnOnLaterClick = {},
        )
    }
}







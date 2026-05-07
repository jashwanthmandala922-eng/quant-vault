package com.x8bit.bitwarden.ui.auth.feature.completeregistration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.card.QuantVaultActionCardSmall
import com.bitwarden.ui.platform.components.card.color.QuantVaultCardColors
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.text.QuantVaultClickableText
import com.bitwarden.ui.platform.components.toggle.QuantVaultSwitch
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.model.WindowSize
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.bitwarden.ui.platform.util.rememberWindowSize
import com.x8bit.bitwarden.ui.auth.feature.completeregistration.handlers.CompleteRegistrationHandler
import com.x8bit.bitwarden.ui.auth.feature.completeregistration.handlers.rememberCompleteRegistrationHandler
import com.x8bit.bitwarden.R

/**
 * Top level composable for the complete registration screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun CompleteRegistrationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPasswordGuidance: () -> Unit,
    onNavigateToPreventAccountLockout: () -> Unit,
    onNavigateToLogin: (email: String) -> Unit,
    viewModel: CompleteRegistrationViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handler = rememberCompleteRegistrationHandler(viewModel = viewModel)
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    // route OS back actions through the VM to clear the special circumstance
    BackHandler(onBack = handler.onBackClick)

    EventsEffect(viewModel) { event ->
        when (event) {
            is CompleteRegistrationEvent.NavigateBack -> onNavigateBack.invoke()
            is CompleteRegistrationEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(QuantVaultSnackbarData(message = event.message))
            }

            CompleteRegistrationEvent.NavigateToMakePasswordStrong -> onNavigateToPasswordGuidance()
            CompleteRegistrationEvent.NavigateToPreventAccountLockout -> {
                onNavigateToPreventAccountLockout()
            }

            is CompleteRegistrationEvent.NavigateToLogin -> {
                onNavigateToLogin(
                    event.email,
                )
            }
        }
    }

    // Show dialog if needed:
    when (val dialog = state.dialog) {
        is CompleteRegistrationDialog.Error -> {
            QuantVaultBasicDialog(
                title = dialog.title?.invoke(),
                message = dialog.message(),
                throwable = dialog.error,
                onDismissRequest = handler.onDismissErrorDialog,
            )
        }

        is CompleteRegistrationDialog.HaveIBeenPwned -> {
            QuantVaultTwoButtonDialog(
                title = dialog.title(),
                message = dialog.message(),
                confirmButtonText = stringResource(id = R.string.yes),
                dismissButtonText = stringResource(id = R.string.no),
                onConfirmClick = handler.onContinueWithBreachedPasswordClick,
                onDismissClick = handler.onDismissErrorDialog,
                onDismissRequest = handler.onDismissErrorDialog,
            )
        }

        CompleteRegistrationDialog.Loading -> {
            QuantVaultLoadingDialog(text = stringResource(id = R.string.create_account))
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
                title = stringResource(id = R.string.create_account),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                navigationIconContentDescription = stringResource(id = R.string.back),
                onNavigationIconClick = handler.onBackClick,
            )
        },
        snackbarHost = { QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(height = 12.dp))
            CompleteRegistrationContent(
                passwordInput = state.passwordInput,
                passwordStrengthState = state.passwordStrengthState,
                confirmPasswordInput = state.confirmPasswordInput,
                passwordHintInput = state.passwordHintInput,
                isCheckDataBreachesToggled = state.isCheckDataBreachesToggled,
                handler = handler,
                nextButtonEnabled = state.validSubmissionReady,
                callToActionText = stringResource(R.string.next),
                minimumPasswordLength = state.minimumPasswordLength,
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun CompleteRegistrationContent(
    passwordInput: String,
    passwordStrengthState: PasswordStrengthState,
    confirmPasswordInput: String,
    passwordHintInput: String,
    isCheckDataBreachesToggled: Boolean,
    nextButtonEnabled: Boolean,
    minimumPasswordLength: Int,
    callToActionText: String,
    handler: CompleteRegistrationHandler,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        CompleteRegistrationContentHeader(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantVaultActionCardSmall(
            actionIcon = rememberVectorPainter(id = R.drawable.ic_question_circle),
            actionText = stringResource(id = R.string.what_makes_a_password_strong),
            callToActionText = stringResource(id = R.string.learn_more),
            callToActionTextColor = QuantVaultTheme.colorScheme.text.interaction,
            colors = QuantVaultCardColors(
                containerColor = QuantVaultTheme.colorScheme.background.primary,
            ),
            onCardClicked = handler.onMakeStrongPassword,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(24.dp))

        var showPassword by rememberSaveable { mutableStateOf(false) }
        QuantVaultPasswordField(
            label = stringResource(id = R.string.master_password_required),
            showPassword = showPassword,
            showPasswordChange = { showPassword = it },
            value = passwordInput,
            onValueChange = handler.onPasswordInputChange,
            showPasswordTestTag = "PasswordVisibilityToggle",
            imeAction = ImeAction.Next,
            supportingContent = {
                PasswordStrengthIndicator(
                    state = passwordStrengthState,
                    currentCharacterCount = passwordInput.length,
                    minimumCharacterCount = minimumPasswordLength,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            passwordFieldTestTag = "MasterPasswordEntry",
            cardStyle = CardStyle.Top(dividerPadding = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        QuantVaultPasswordField(
            label = stringResource(id = R.string.retype_master_password_required),
            value = confirmPasswordInput,
            showPassword = showPassword,
            showPasswordChange = { showPassword = it },
            onValueChange = handler.onConfirmPasswordInputChange,
            showPasswordTestTag = "ConfirmPasswordVisibilityToggle",
            passwordFieldTestTag = "ConfirmMasterPasswordEntry",
            cardStyle = CardStyle.Middle(dividerPadding = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        QuantVaultTextField(
            label = stringResource(
                id = R.string.master_password_hint_not_specified,
            ),
            value = passwordHintInput,
            onValueChange = handler.onPasswordHintChange,
            supportingContent = {
                Text(
                    text = stringResource(
                        id = R.string.Quant Vault_cannot_recover_a_lost_or_forgotten_master_password,
                    ),
                    style = QuantVaultTheme.typography.bodySmall,
                    color = QuantVaultTheme.colorScheme.text.secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
                QuantVaultClickableText(
                    label = stringResource(
                        id = R.string.learn_about_other_ways_to_prevent_account_lockout,
                    ),
                    onClick = handler.onLearnToPreventLockout,
                    style = QuantVaultTheme.typography.labelMedium,
                    innerPadding = PaddingValues(vertical = 4.dp),
                )
            },
            textFieldTestTag = "MasterPasswordHintLabel",
            cardStyle = CardStyle.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 8.dp))
        QuantVaultSwitch(
            label = stringResource(
                id = R.string.check_known_data_breaches_for_this_password,
            ),
            isChecked = isCheckDataBreachesToggled,
            onCheckedChange = handler.onCheckDataBreachesToggle,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .testTag("CheckExposedMasterPasswordToggle")
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        QuantVaultFilledButton(
            label = callToActionText,
            isEnabled = nextButtonEnabled,
            onClick = handler.onCallToAction,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
    }
}

@Composable
private fun CompleteRegistrationContentHeader(
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

/**
 * Header content ordered with the image "first" and the text "second" which can be placed in a
 * [Column] or [Row].
 */
@Composable
private fun OrderedHeaderContent() {
    Image(
        painter = rememberVectorPainter(id = R.drawable.ill_lock),
        contentDescription = null,
        modifier = Modifier.size(100.dp),
    )
    Spacer(modifier = Modifier.size(24.dp))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.choose_your_master_password),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.choose_a_unique_and_strong_password_to_keep_your_information_safe,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewScreenSizes
@Composable
private fun CompleteRegistrationContentOldUI_preview() {
    QuantVaultTheme {
        CompleteRegistrationContent(
            passwordInput = "tortor",
            passwordStrengthState = PasswordStrengthState.WEAK_3,
            confirmPasswordInput = "consequat",
            passwordHintInput = "dissentiunt",
            isCheckDataBreachesToggled = false,
            handler = CompleteRegistrationHandler(
                onDismissErrorDialog = {},
                onContinueWithBreachedPasswordClick = {},
                onBackClick = {},
                onPasswordInputChange = {},
                onConfirmPasswordInputChange = {},
                onPasswordHintChange = {},
                onCheckDataBreachesToggle = {},
                onLearnToPreventLockout = {},
                onMakeStrongPassword = {},
                onCallToAction = {},
            ),
            callToActionText = "Next",
            nextButtonEnabled = true,
            modifier = Modifier.standardHorizontalMargin(),
            minimumPasswordLength = 12,
        )
    }
}








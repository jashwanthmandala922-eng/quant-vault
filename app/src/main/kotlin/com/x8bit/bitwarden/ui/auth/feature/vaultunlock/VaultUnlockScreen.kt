package com.x8bit.bitwarden.ui.auth.feature.vaultunlock

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.account.QuantVaultAccountActionItem
import com.bitwarden.ui.platform.components.account.QuantVaultAccountSwitcher
import com.bitwarden.ui.platform.components.account.dialog.QuantVaultLogoutConfirmationDialog
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
import com.bitwarden.ui.platform.components.support.QuantVaultSupportingContent
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.ui.auth.feature.vaultunlock.util.inputFieldVisibilityToggleTestTag
import com.x8bit.bitwarden.ui.auth.feature.vaultunlock.util.unlockScreenInputLabel
import com.x8bit.bitwarden.ui.auth.feature.vaultunlock.util.unlockScreenInputTestTag
import com.x8bit.bitwarden.ui.auth.feature.vaultunlock.util.unlockScreenKeyboardType
import com.x8bit.bitwarden.ui.auth.feature.vaultunlock.util.unlockScreenMessage
import com.x8bit.bitwarden.ui.auth.feature.vaultunlock.util.unlockScreenTitle
import com.x8bit.bitwarden.ui.credentials.manager.CredentialProviderCompletionManager
import com.x8bit.bitwarden.ui.credentials.manager.model.AssertFido2CredentialResult
import com.x8bit.bitwarden.ui.credentials.manager.model.GetCredentialsResult
import com.x8bit.bitwarden.ui.platform.composition.LocalBiometricsManager
import com.x8bit.bitwarden.ui.platform.composition.LocalCredentialProviderCompletionManager
import com.x8bit.bitwarden.ui.platform.manager.biometrics.BiometricsManager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import javax.crypto.Cipher
import com.x8bit.bitwarden.R

/**
 * Time slice to delay auto-focusing on the password/pin field. Because of the refresh that
 * takes place when switching accounts or changing the lock status we want to delay this
 * longer than the delay in place for sending those actions in [com.quantvault.app.MainViewModel]
 * defined by `ANIMATION_REFRESH_DELAY`. We need to  ensure this value is
 * always greater.
 */
private const val AUTO_FOCUS_DELAY = 575L

/**
 * The top level composable for the Vault Unlock screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun VaultUnlockScreen(
    viewModel: VaultUnlockViewModel = hiltViewModel(),
    biometricsManager: BiometricsManager = LocalBiometricsManager.current,
    focusManager: FocusManager = LocalFocusManager.current,
    credentialProviderCompletionManager: CredentialProviderCompletionManager =
        LocalCredentialProviderCompletionManager.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    LaunchedEffect(state.requiresBiometricsLogin) {
        if (state.requiresBiometricsLogin && !biometricsManager.isBiometricsSupported) {
            viewModel.trySendAction(VaultUnlockAction.BiometricsNoLongerSupported)
        }
    }

    val onBiometricsUnlockSuccess: (cipher: Cipher) -> Unit = {
        viewModel.trySendAction(VaultUnlockAction.BiometricsUnlockSuccess(it))
    }
    val onBiometricsLockOut: () -> Unit = {
        viewModel.trySendAction(VaultUnlockAction.BiometricsLockOut)
    }

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is VaultUnlockEvent.PromptForBiometrics -> {
                biometricsManager.promptBiometrics(
                    onSuccess = onBiometricsUnlockSuccess,
                    onCancel = {
                        // no-op
                    },
                    onError = {
                        // no-op
                    },
                    onLockOut = onBiometricsLockOut,
                    cipher = event.cipher,
                )
            }

            is VaultUnlockEvent.Fido2CredentialAssertionError -> {
                credentialProviderCompletionManager.completeFido2Assertion(
                    result = AssertFido2CredentialResult.Error(message = event.message),
                )
            }

            is VaultUnlockEvent.GetCredentialsError -> {
                credentialProviderCompletionManager.completeProviderGetCredentialsRequest(
                    result = GetCredentialsResult.Error(message = event.message),
                )
            }
        }
    }

    var accountMenuVisible by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { !accountMenuVisible },
    )

    // Dynamic dialogs
    when (val dialog = state.dialog) {
        is VaultUnlockState.VaultUnlockDialog.Error -> QuantVaultBasicDialog(
            title = dialog.title(),
            message = dialog.message(),
            onDismissRequest = { viewModel.trySendAction(VaultUnlockAction.DismissDialog) },
            throwable = dialog.throwable,
        )

        VaultUnlockState.VaultUnlockDialog.Loading -> QuantVaultLoadingDialog(
            text = stringResource(id = R.string.loading),
        )

        VaultUnlockState.VaultUnlockDialog.BiometricsNoLongerSupported -> {
            QuantVaultBasicDialog(
                title = stringResource(id = R.string.biometrics_no_longer_supported_title),
                message = stringResource(id = R.string.biometrics_no_longer_supported),
                onDismissRequest = remember {
                    {
                        viewModel.trySendAction(
                            VaultUnlockAction.DismissBiometricsNoLongerSupportedDialog,
                        )
                    }
                },
            )
        }

        null -> Unit
    }

    // Static dialogs
    var showLogoutConfirmationDialog by remember { mutableStateOf(false) }
    if (showLogoutConfirmationDialog) {
        QuantVaultLogoutConfirmationDialog(
            onDismissRequest = { showLogoutConfirmationDialog = false },
            onConfirmClick = {
                showLogoutConfirmationDialog = false
                viewModel.trySendAction(VaultUnlockAction.ConfirmLogoutClick)
            },
        )
    }

    // Content
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus(force = true) },
                )
            },
        topBar = {
            QuantVaultTopAppBar(
                title = state.vaultUnlockType.unlockScreenTitle(),
                scrollBehavior = scrollBehavior,
                navigationIcon = null,
                actions = {
                    if (state.showAccountMenu) {
                        QuantVaultAccountActionItem(
                            initials = state.initials,
                            color = state.avatarColor,
                            onClick = {
                                focusManager.clearFocus()
                                accountMenuVisible = !accountMenuVisible
                            },
                        )
                    }
                    QuantVaultOverflowActionItem(
                        menuItemDataList = persistentListOf(
                            OverflowMenuItemData(
                                text = stringResource(id = R.string.log_out),
                                onClick = { showLogoutConfirmationDialog = true },
                            ),
                        ),
                    )
                },
            )
        },
        overlay = {
            QuantVaultAccountSwitcher(
                isVisible = accountMenuVisible,
                accountSummaries = state.accountSummaries.toImmutableList(),
                onSwitchAccountClick = {
                    viewModel.trySendAction(VaultUnlockAction.SwitchAccountClick(it))
                },
                onLockAccountClick = {
                    viewModel.trySendAction(VaultUnlockAction.LockAccountClick(it))
                },
                onLogoutAccountClick = {
                    viewModel.trySendAction(VaultUnlockAction.LogoutAccountClick(it))
                },
                onAddAccountClick = { viewModel.trySendAction(VaultUnlockAction.AddAccountClick) },
                onDismissRequest = { accountMenuVisible = false },
                topAppBarScrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxSize(),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            if (!state.hideInput) {
                // When switching from an unlocked account to a locked account, the
                // current activity is recreated and therefore the composition takes place
                // twice. Adding this delay prevents the MP or Pin field
                // from auto focusing on the first composition which creates a visual jank where
                // the keyboard shows, disappears, and then shows again.
                var autoFocusDelayCompleted by rememberSaveable {
                    mutableStateOf(false)
                }
                LaunchedEffect(Unit) {
                    delay(AUTO_FOCUS_DELAY)
                    autoFocusDelayCompleted = true
                }
                QuantVaultPasswordField(
                    label = state.vaultUnlockType.unlockScreenInputLabel(),
                    value = state.input,
                    onValueChange = { viewModel.trySendAction(VaultUnlockAction.InputChanged(it)) },
                    keyboardType = state.vaultUnlockType.unlockScreenKeyboardType,
                    showPasswordTestTag = state
                        .vaultUnlockType
                        .inputFieldVisibilityToggleTestTag,
                    autoFocus = state.showKeyboard && autoFocusDelayCompleted,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.trySendAction(VaultUnlockAction.UnlockClick) },
                    ),
                    passwordFieldTestTag = state.vaultUnlockType.unlockScreenInputTestTag,
                    cardStyle = CardStyle.Top(),
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .fillMaxWidth(),
                )
            }
            QuantVaultSupportingContent(
                cardStyle = if (state.hideInput) CardStyle.Full else CardStyle.Bottom,
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            ) {
                if (!state.hideInput) {
                    Text(
                        text = state.vaultUnlockType.unlockScreenMessage(),
                        style = QuantVaultTheme.typography.bodySmall,
                        color = QuantVaultTheme.colorScheme.text.secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                }
                Text(
                    text = stringResource(
                        id = R.string.logged_in_as_on,
                        formatArgs = arrayOf(state.email, state.environmentUrl),
                    ),
                    style = QuantVaultTheme.typography.bodySmall,
                    color = QuantVaultTheme.colorScheme.text.secondary,
                    modifier = Modifier
                        .testTag(tag = "UserAndEnvironmentDataLabel")
                        .fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (state.showBiometricLogin && biometricsManager.isBiometricsSupported) {
                QuantVaultOutlinedButton(
                    label = stringResource(id = R.string.use_biometrics_to_unlock),
                    onClick = { viewModel.trySendAction(VaultUnlockAction.BiometricsUnlockClick) },
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else if (state.showBiometricInvalidatedMessage) {
                Text(
                    text = stringResource(R.string.account_biometric_invalidated),
                    textAlign = TextAlign.Start,
                    style = QuantVaultTheme.typography.bodyMedium,
                    color = QuantVaultTheme.colorScheme.status.error,
                    modifier = Modifier.standardHorizontalMargin(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (!state.hideInput) {
                QuantVaultFilledButton(
                    label = stringResource(id = R.string.unlock),
                    onClick = { viewModel.trySendAction(VaultUnlockAction.UnlockClick) },
                    isEnabled = state.input.isNotEmpty(),
                    modifier = Modifier
                        .testTag("UnlockVaultButton")
                        .standardHorizontalMargin()
                        .fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}







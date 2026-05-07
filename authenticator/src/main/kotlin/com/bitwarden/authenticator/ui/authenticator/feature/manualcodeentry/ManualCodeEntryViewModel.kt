package com.quantvault.authenticator.ui.authenticator.feature.manualcodeentry

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType
import com.quantvault.authenticator.data.authenticator.manager.TotpCodeManager
import com.quantvault.authenticator.data.authenticator.repository.AuthenticatorRepository
import com.quantvault.authenticator.data.authenticator.repository.model.SharedVerificationCodesState
import com.quantvault.authenticator.data.authenticator.repository.util.isSyncWithQuantVaultEnabled
import com.quantvault.authenticator.data.platform.repository.SettingsRepository
import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.authenticator.ui.platform.model.SnackbarRelay
import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManager
import com.quantvault.ui.platform.base.BaseViewModel
import com.quantvault.ui.platform.base.util.isBase32
import com.quantvault.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.quantvault.ui.platform.manager.snackbar.SnackbarRelayManager
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.Text
import com.quantvault.ui.util.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.UUID
import javax.inject.Inject

private const val KEY_STATE = "state"

/**
 * The ViewModel for handling user interactions in the manual code entry screen.
 *
 */
@HiltViewModel
@Suppress("TooManyFunctions")
class ManualCodeEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authenticatorRepository: AuthenticatorRepository,
    private val authenticatorBridgeManager: AuthenticatorBridgeManager,
    private val snackbarRelayManager: SnackbarRelayManager<SnackbarRelay>,
    settingsRepository: SettingsRepository,
) : BaseViewModel<ManualCodeEntryState, ManualCodeEntryEvent, ManualCodeEntryAction>(
    initialState = savedStateHandle[KEY_STATE]
        ?: ManualCodeEntryState(
            code = "",
            issuer = "",
            dialog = null,
            buttonState = deriveButtonState(
                sharedCodesState = authenticatorRepository.sharedCodesStateFlow.value,
                defaultSaveOption = settingsRepository.defaultSaveOption,
            ),
        ),
) {
    override fun handleAction(action: ManualCodeEntryAction) {
        when (action) {
            is ManualCodeEntryAction.CloseClick -> handleCloseClick()
            is ManualCodeEntryAction.CodeTextChange -> handleCodeTextChange(action)
            is ManualCodeEntryAction.IssuerTextChange -> handleIssuerTextChange(action)
            is ManualCodeEntryAction.ScanQrCodeTextClick -> handleScanQrCodeTextClick()
            is ManualCodeEntryAction.SettingsClick -> handleSettingsClick()
            ManualCodeEntryAction.DismissDialog -> {
                handleDialogDismiss()
            }

            ManualCodeEntryAction.SaveLocallyClick -> handleSaveLocallyClick()
            ManualCodeEntryAction.SaveToQuantVaultClick -> handleSaveToQuantVaultClick()
        }
    }

    private fun handleDialogDismiss() {
        mutableStateFlow.update { it.copy(dialog = null) }
    }

    private fun handleIssuerTextChange(action: ManualCodeEntryAction.IssuerTextChange) {
        mutableStateFlow.update {
            it.copy(issuer = action.issuer)
        }
    }

    private fun handleCloseClick() {
        sendEvent(ManualCodeEntryEvent.NavigateBack)
    }

    private fun handleCodeTextChange(action: ManualCodeEntryAction.CodeTextChange) {
        mutableStateFlow.update {
            it.copy(code = action.code)
        }
    }

    private fun handleSaveLocallyClick() = handleCodeSubmit(saveToQuantVault = false)

    private fun handleSaveToQuantVaultClick() = handleCodeSubmit(saveToQuantVault = true)

    private fun handleCodeSubmit(saveToQuantVault: Boolean) {
        val isSteamCode = state.code.startsWith(TotpCodeManager.STEAM_CODE_PREFIX)
        val sanitizedCode = state.code
            .replace(" ", "")
            .replace(TotpCodeManager.STEAM_CODE_PREFIX, "")
        if (sanitizedCode.isBlank()) {
            showErrorDialog(QuantVaultString.key_is_required.asText())
            return
        }

        if (!sanitizedCode.isBase32()) {
            showErrorDialog(QuantVaultString.key_is_invalid.asText())
            return
        }

        if (state.issuer.isBlank()) {
            showErrorDialog(QuantVaultString.name_is_required.asText())
            return
        }

        if (saveToQuantVault) {
            // Save to QuantVault by kicking off save to QuantVault flow:
            saveValidCodeToQuantVault(sanitizedCode)
        } else {
            // Save locally by giving entity to AuthRepository and navigating back:
            saveValidCodeLocally(sanitizedCode, isSteamCode)
        }
    }

    private fun saveValidCodeToQuantVault(sanitizedCode: String) {
        val didLaunchSaveToQuantVault = authenticatorBridgeManager
            .startAddTotpLoginItemFlow(
                totpUri = "otpauth://totp/?secret=$sanitizedCode&issuer=${state.issuer}",
            )
        if (!didLaunchSaveToQuantVault) {
            mutableStateFlow.update {
                it.copy(
                    dialog = ManualCodeEntryState.DialogState.Error(
                        title = QuantVaultString.something_went_wrong.asText(),
                        message = QuantVaultString.please_try_again.asText(),
                    ),
                )
            }
        } else {
            sendEvent(ManualCodeEntryEvent.NavigateBack)
        }
    }

    private fun saveValidCodeLocally(
        sanitizedCode: String,
        isSteamCode: Boolean,
    ) {
        viewModelScope.launch {
            authenticatorRepository.createItem(
                AuthenticatorItemEntity(
                    id = UUID.randomUUID().toString(),
                    key = sanitizedCode,
                    issuer = state.issuer,
                    accountName = null,
                    userId = null,
                    type = if (isSteamCode) {
                        AuthenticatorItemType.STEAM
                    } else {
                        AuthenticatorItemType.TOTP
                    },
                    favorite = false,
                ),
            )
            snackbarRelayManager.sendSnackbarData(
                data = QuantVaultSnackbarData(QuantVaultString.verification_code_added.asText()),
                relay = SnackbarRelay.ITEM_ADDED,
            )
            sendEvent(event = ManualCodeEntryEvent.NavigateBack)
        }
    }

    private fun handleScanQrCodeTextClick() {
        sendEvent(ManualCodeEntryEvent.NavigateToQrCodeScreen)
    }

    private fun handleSettingsClick() {
        sendEvent(ManualCodeEntryEvent.NavigateToAppSettings)
    }

    private fun showErrorDialog(message: Text) {
        mutableStateFlow.update {
            it.copy(
                dialog = ManualCodeEntryState.DialogState.Error(
                    message = message,
                ),
            )
        }
    }
}

private fun deriveButtonState(
    sharedCodesState: SharedVerificationCodesState,
    defaultSaveOption: DefaultSaveOption,
): ManualCodeEntryState.ButtonState {
    // If syncing with QuantVault is not enabled, show local save only:
    if (!sharedCodesState.isSyncWithQuantVaultEnabled) {
        return ManualCodeEntryState.ButtonState.LocalOnly
    }
    // Otherwise, show save options based on user's preferences:
    return when (defaultSaveOption) {
        DefaultSaveOption.NONE -> ManualCodeEntryState.ButtonState.SaveToQuantVaultPrimary
        DefaultSaveOption.QuantVault_APP -> ManualCodeEntryState.ButtonState.SaveToQuantVaultPrimary
        DefaultSaveOption.LOCAL -> ManualCodeEntryState.ButtonState.SaveLocallyPrimary
    }
}

/**
 * Models state of the manual entry screen.
 */
@Parcelize
data class ManualCodeEntryState(
    val code: String,
    val issuer: String,
    val dialog: DialogState?,
    val buttonState: ButtonState,
) : Parcelable {

    /**
     * Models dialog states for [ManualCodeEntryViewModel].
     */
    @Parcelize
    sealed class DialogState : Parcelable {

        /**
         * Show an error dialog with an optional [title], and a [message].
         */
        @Parcelize
        data class Error(
            val title: Text? = null,
            val message: Text,
        ) : DialogState()

        /**
         * Show a loading dialog.
         */
        @Parcelize
        data class Loading(
            val message: Text,
        ) : DialogState()
    }

    /**
     * Models what variation of button states should be shown.
     */
    @Parcelize
    sealed class ButtonState : Parcelable {

        /**
         * Show only save locally option.
         */
        @Parcelize
        data object LocalOnly : ButtonState()

        /**
         * Show both save locally and save to QuantVault, with QuantVault being the primary option.
         */
        @Parcelize
        data object SaveToQuantVaultPrimary : ButtonState()

        /**
         * Show both save locally and save to QuantVault, with locally being the primary option.
         */
        @Parcelize
        data object SaveLocallyPrimary : ButtonState()
    }
}

/**
 * Models events for the [ManualCodeEntryScreen].
 */
sealed class ManualCodeEntryEvent {

    /**
     * Navigate back.
     */
    data object NavigateBack : ManualCodeEntryEvent()

    /**
     * Navigate to the Qr code screen.
     */
    data object NavigateToQrCodeScreen : ManualCodeEntryEvent()

    /**
     * Navigate to the app settings.
     */
    data object NavigateToAppSettings : ManualCodeEntryEvent()
}

/**
 * Models actions for the [ManualCodeEntryScreen].
 */
sealed class ManualCodeEntryAction {

    /**
     * User clicked close.
     */
    data object CloseClick : ManualCodeEntryAction()

    /**
     * The user clicked the save locally button.
     */
    data object SaveLocallyClick : ManualCodeEntryAction()

    /**
     * Th user clicked the save to QuantVault button.
     */
    data object SaveToQuantVaultClick : ManualCodeEntryAction()

    /**
     * The user has changed the code text.
     */
    data class CodeTextChange(val code: String) : ManualCodeEntryAction()

    /**
     * The use has changed the issuer text.
     */
    data class IssuerTextChange(val issuer: String) : ManualCodeEntryAction()

    /**
     * The text to switch to QR code scanning is clicked.
     */
    data object ScanQrCodeTextClick : ManualCodeEntryAction()

    /**
     * The action for the user clicking the settings button.
     */
    data object SettingsClick : ManualCodeEntryAction()

    /**
     * The user has dismissed the dialog.
     */
    data object DismissDialog : ManualCodeEntryAction()
}





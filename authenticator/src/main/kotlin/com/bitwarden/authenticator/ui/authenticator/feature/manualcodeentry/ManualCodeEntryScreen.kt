package com.quantvault.authenticator.ui.authenticator.feature.manualcodeentry

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.authenticator.ui.platform.composition.LocalPermissionsManager
import com.quantvault.authenticator.ui.platform.manager.permissions.PermissionsManager
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.components.appbar.QuantVaultTopAppBar
import com.quantvault.ui.platform.components.button.QuantVaultTextButton
import com.quantvault.ui.platform.components.dialog.QuantVaultBasicDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.quantvault.ui.platform.components.field.QuantVaultPasswordField
import com.quantvault.ui.platform.components.field.QuantVaultTextField
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.composition.LocalIntentManager
import com.quantvault.ui.platform.manager.IntentManager
import com.quantvault.ui.platform.manager.util.startAppSettingsActivity
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * The screen to manually add a totp code.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualCodeEntryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQrCodeScreen: () -> Unit,
    viewModel: ManualCodeEntryViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    permissionsManager: PermissionsManager = LocalPermissionsManager.current,
) {
    var shouldShowPermissionDialog by rememberSaveable { mutableStateOf(false) }
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val launcher = permissionsManager.getLauncher { isGranted ->
        if (isGranted) {
            viewModel.trySendAction(ManualCodeEntryAction.ScanQrCodeTextClick)
        } else {
            shouldShowPermissionDialog = true
        }
    }

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is ManualCodeEntryEvent.NavigateToAppSettings -> {
                intentManager.startAppSettingsActivity()
            }

            is ManualCodeEntryEvent.NavigateToQrCodeScreen -> {
                onNavigateToQrCodeScreen.invoke()
            }

            is ManualCodeEntryEvent.NavigateBack -> {
                onNavigateBack.invoke()
            }
        }
    }

    if (shouldShowPermissionDialog) {
        QuantVaultTwoButtonDialog(
            message = stringResource(
                id = QuantVaultString.enable_camera_permission_to_use_the_scanner,
            ),
            confirmButtonText = stringResource(id = QuantVaultString.settings),
            dismissButtonText = stringResource(id = QuantVaultString.no_thanks),
            onConfirmClick = { viewModel.trySendAction(ManualCodeEntryAction.SettingsClick) },
            onDismissClick = { shouldShowPermissionDialog = false },
            onDismissRequest = { shouldShowPermissionDialog = false },
            title = null,
        )
    }

    ManualCodeEntryDialogs(
        dialog = state.dialog,
        onDismissRequest = remember(state) {
            { viewModel.trySendAction(ManualCodeEntryAction.DismissDialog) }
        },
    )

    QuantVaultScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = QuantVaultString.create_verification_code),
                navigationIcon = painterResource(id = QuantVaultDrawable.ic_close),
                navigationIconContentDescription = stringResource(id = QuantVaultString.close),
                onNavigationIconClick = {
                    viewModel.trySendAction(ManualCodeEntryAction.CloseClick)
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            )
        },
    ) {
        ManualCodeEntryContent(
            state = state,
            onNameChange = { viewModel.trySendAction(ManualCodeEntryAction.IssuerTextChange(it)) },
            onKeyChange = { viewModel.trySendAction(ManualCodeEntryAction.CodeTextChange(it)) },
            onSaveLocallyClick = {
                viewModel.trySendAction(ManualCodeEntryAction.SaveLocallyClick)
            },
            onSaveToQuantVaultClick = {
                viewModel.trySendAction(ManualCodeEntryAction.SaveToQuantVaultClick)
            },
            onScanQrCodeClick = {
                if (permissionsManager.checkPermission(Manifest.permission.CAMERA)) {
                    viewModel.trySendAction(ManualCodeEntryAction.ScanQrCodeTextClick)
                } else {
                    launcher.launch(Manifest.permission.CAMERA)
                }
            },
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun ManualCodeEntryContent(
    state: ManualCodeEntryState,
    onNameChange: (name: String) -> Unit,
    onKeyChange: (key: String) -> Unit,
    onSaveLocallyClick: () -> Unit,
    onSaveToQuantVaultClick: () -> Unit,
    onScanQrCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(state = rememberScrollState())) {
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(id = QuantVaultString.enter_key_manually),
            style = QuantVaultTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(height = 24.dp))
        QuantVaultTextField(
            label = stringResource(id = QuantVaultString.name),
            value = state.issuer,
            onValueChange = onNameChange,
            cardStyle = CardStyle.Top(),
            modifier = Modifier
                .testTag(tag = "NameTextField")
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        QuantVaultPasswordField(
            singleLine = false,
            label = stringResource(id = QuantVaultString.key),
            value = state.code,
            onValueChange = onKeyChange,
            cardStyle = CardStyle.Bottom,
            modifier = Modifier
                .testTag(tag = "KeyTextField")
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(24.dp))
        SaveManualCodeButtons(
            state = state.buttonState,
            onSaveLocallyClick = onSaveLocallyClick,
            onSaveToQuantVaultClick = onSaveToQuantVaultClick,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(id = QuantVaultString.cannot_add_authenticator_key),
            color = QuantVaultTheme.colorScheme.text.secondary,
            style = QuantVaultTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        QuantVaultTextButton(
            label = stringResource(id = QuantVaultString.scan_qr_code),
            onClick = onScanQrCodeClick,
            modifier = Modifier
                .wrapContentWidth()
                .align(alignment = Alignment.CenterHorizontally)
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun ManualCodeEntryDialogs(
    dialog: ManualCodeEntryState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialog) {
        is ManualCodeEntryState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = dialog.title?.invoke(),
                message = dialog.message(),
                onDismissRequest = onDismissRequest,
            )
        }

        is ManualCodeEntryState.DialogState.Loading -> {
            QuantVaultLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }
}





package com.quantvault.authenticator.ui.auth.unlock

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.authenticator.ui.platform.composition.LocalBiometricsManager
import com.quantvault.authenticator.ui.platform.manager.biometrics.BiometricsManager
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.components.button.QuantVaultFilledButton
import com.quantvault.ui.platform.components.dialog.QuantVaultBasicDialog
import com.quantvault.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import javax.crypto.Cipher

/**
 * Top level composable for the unlock screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockScreen(
    viewModel: UnlockViewModel = hiltViewModel(),
    biometricsManager: BiometricsManager = LocalBiometricsManager.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val onBiometricsUnlockSuccess: (cipher: Cipher) -> Unit = {
        viewModel.trySendAction(UnlockAction.BiometricsUnlockSuccess(it))
    }
    val onBiometricsLockOut: () -> Unit = {
        viewModel.trySendAction(UnlockAction.BiometricsLockout)
    }

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is UnlockEvent.PromptForBiometrics -> {
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
        }
    }

    UnlockDialogs(
        dialog = state.dialog,
        onDismissRequest = { viewModel.trySendAction(UnlockAction.DismissDialog) },
    )

    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(220.dp)
                    .height(74.dp)
                    .fillMaxWidth(),
                painter = rememberVectorPainter(id = QuantVaultDrawable.logo_authenticator),
                contentDescription = stringResource(QuantVaultString.QuantVault_authenticator),
            )
            Spacer(modifier = Modifier.height(32.dp))
            QuantVaultFilledButton(
                label = stringResource(id = QuantVaultString.unlock),
                onClick = { viewModel.trySendAction(UnlockAction.BiometricsUnlockClick) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(height = 12.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun UnlockDialogs(
    dialog: UnlockState.Dialog?,
    onDismissRequest: () -> Unit,
) {
    when (dialog) {
        is UnlockState.Dialog.Error -> {
            QuantVaultBasicDialog(
                title = dialog.title(),
                message = dialog.message(),
                throwable = dialog.throwable,
                onDismissRequest = onDismissRequest,
            )
        }

        UnlockState.Dialog.Loading -> {
            QuantVaultLoadingDialog(text = stringResource(id = QuantVaultString.loading))
        }

        null -> Unit
    }
}





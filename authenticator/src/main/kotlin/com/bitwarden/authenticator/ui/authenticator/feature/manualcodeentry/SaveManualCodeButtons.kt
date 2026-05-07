package com.quantvault.authenticator.ui.authenticator.feature.manualcodeentry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.components.button.QuantVaultFilledButton
import com.quantvault.ui.platform.components.button.QuantVaultOutlinedButton
import com.quantvault.ui.platform.resource.QuantVaultString

/**
 * Displays save buttons for saving a manually entered code.
 *
 * @param state State of the buttons to show.
 * @param onSaveLocallyClick Callback invoked when the user clicks save locally.
 * @param onSaveToQuantVaultClick Callback invoked when the user clicks save to QuantVault.
 * @param modifier The modifier to be applied to the composable.
 */
@Composable
fun SaveManualCodeButtons(
    state: ManualCodeEntryState.ButtonState,
    onSaveLocallyClick: () -> Unit,
    onSaveToQuantVaultClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        ManualCodeEntryState.ButtonState.LocalOnly -> {
            QuantVaultFilledButton(
                label = stringResource(id = QuantVaultString.add_code),
                onClick = onSaveLocallyClick,
                modifier = modifier.testTag(tag = "AddCodeButton"),
            )
        }

        ManualCodeEntryState.ButtonState.SaveLocallyPrimary -> {
            Column(modifier = modifier) {
                QuantVaultFilledButton(
                    label = stringResource(id = QuantVaultString.save_here),
                    onClick = onSaveLocallyClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                QuantVaultOutlinedButton(
                    label = stringResource(QuantVaultString.save_to_QuantVault),
                    onClick = onSaveToQuantVaultClick,
                    isExternalLink = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        ManualCodeEntryState.ButtonState.SaveToQuantVaultPrimary -> {
            Column(modifier = modifier) {
                QuantVaultFilledButton(
                    label = stringResource(id = QuantVaultString.save_to_QuantVault),
                    isExternalLink = true,
                    onClick = onSaveToQuantVaultClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                QuantVaultOutlinedButton(
                    label = stringResource(QuantVaultString.save_here),
                    onClick = onSaveLocallyClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}





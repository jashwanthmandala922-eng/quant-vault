package com.x8bit.bitwarden.ui.platform.components.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Represents a Quant Vault-styled dialog for the user to enter their PIN.
 *
 * @param onConfirmClick called when the confirm button is clicked and emits the entered PIN.
 * @param onDismissRequest called when the user attempts to dismiss the dialog (for example by
 * tapping outside of it).
 */
@Composable
fun QuantVaultPinDialog(
    onConfirmClick: (pin: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            QuantVaultTextButton(
                label = stringResource(id = R.string.cancel),
                onClick = onDismissRequest,
                modifier = Modifier.testTag("DismissAlertButton"),
            )
        },
        confirmButton = {
            QuantVaultTextButton(
                label = stringResource(id = R.string.submit),
                isEnabled = pin.isNotEmpty(),
                onClick = { onConfirmClick(pin) },
                modifier = Modifier.testTag("AcceptAlertButton"),
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.verify_pin),
                style = QuantVaultTheme.typography.headlineSmall,
                modifier = Modifier.testTag("AlertTitleText"),
            )
        },
        text = {
            QuantVaultPasswordField(
                label = stringResource(id = R.string.pin),
                value = pin,
                onValueChange = { pin = it },
                autoFocus = true,
                passwordFieldTestTag = "AlertInputField",
                cardStyle = CardStyle.Full,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        shape = QuantVaultTheme.shapes.dialog,
        containerColor = QuantVaultTheme.colorScheme.background.primary,
        iconContentColor = QuantVaultTheme.colorScheme.icon.secondary,
        titleContentColor = QuantVaultTheme.colorScheme.text.primary,
        textContentColor = QuantVaultTheme.colorScheme.text.primary,
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
            testTag = "AlertPopup"
        },
    )
}







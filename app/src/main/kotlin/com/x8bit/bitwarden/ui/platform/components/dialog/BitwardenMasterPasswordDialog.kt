package com.x8bit.bitwarden.ui.platform.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Represents a Quant Vault-styled dialog for entering your master password.
 *
 * @param title The title of the dialog.
 * @param message The message of the dialog.
 * @param confirmButtonText The text of the confirm button.
 * @param dismissButtonText The text of the dismiss button.
 * @param onConfirmClick called when the confirm button is clicked and emits the entered password.
 * @param onDismissRequest called when the user attempts to dismiss the dialog (for example by
 * tapping outside of it).
 */
@Composable
fun QuantVaultMasterPasswordDialog(
    title: String = stringResource(id = R.string.password_confirmation),
    message: String = stringResource(id = R.string.password_confirmation_desc),
    confirmButtonText: String = stringResource(id = R.string.submit),
    dismissButtonText: String = stringResource(id = R.string.cancel),
    onConfirmClick: (masterPassword: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var masterPassword by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            QuantVaultTextButton(
                label = dismissButtonText,
                onClick = onDismissRequest,
                modifier = Modifier.testTag("DismissAlertButton"),
            )
        },
        confirmButton = {
            QuantVaultFilledButton(
                label = confirmButtonText,
                isEnabled = masterPassword.isNotEmpty(),
                onClick = { onConfirmClick(masterPassword) },
                modifier = Modifier.testTag("AcceptAlertButton"),
            )
        },
        title = {
            Text(
                text = title,
                style = QuantVaultTheme.typography.headlineSmall,
                modifier = Modifier.testTag("AlertTitleText"),
            )
        },
        text = {
            Column {
                Text(
                    text = message,
                    style = QuantVaultTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("AlertContentText"),
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuantVaultPasswordField(
                    label = stringResource(id = R.string.master_password),
                    value = masterPassword,
                    onValueChange = { masterPassword = it },
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirmClick(masterPassword) },
                    ),
                    autoFocus = true,
                    passwordFieldTestTag = "AlertInputField",
                    cardStyle = CardStyle.Full,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
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







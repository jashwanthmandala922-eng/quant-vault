package com.quantvault.ui.platform.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import com.quantvault.ui.platform.components.button.quantvaultTextButton
import com.quantvault.ui.platform.composition.LocalIntentManager
import com.quantvault.ui.platform.manager.IntentManager
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault-styled dialog.
 *
 * @param title The optional title to be displayed by the dialog.
 * @param message The message to be displayed under the [title] by the dialog.
 * @param confirmButtonLabel The label for the confirm button.
 * @param throwable An optional [Throwable] that can be shared from this dialog.
 * @param onDismissRequest A lambda that is invoked when the user has requested to dismiss the
 * dialog, whether by tapping "OK", tapping outside the dialog, or pressing the back button.
 */
@Suppress("LongMethod")
@Composable
fun quantvaultBasicDialog(
    title: String?,
    message: String,
    confirmButtonLabel: String = stringResource(id = quantvaultString.okay),
    onDismissRequest: () -> Unit,
    throwable: Throwable? = null,
    intentManager: IntentManager = LocalIntentManager.current,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            quantvaultTextButton(
                label = confirmButtonLabel,
                onClick = onDismissRequest,
                modifier = Modifier.testTag(tag = "AcceptAlertButton"),
            )
        },
        dismissButton = throwable
            ?.let { error ->
                {
                    quantvaultTextButton(
                        label = stringResource(id = quantvaultString.share_error_details),
                        onClick = {
                            intentManager.shareErrorReport(throwable = error)
                            onDismissRequest()
                        },
                        modifier = Modifier.testTag(tag = "ShareErrorDetailsAlertButton"),
                    )
                }
            },
        title = title?.let {
            {
                Text(
                    text = it,
                    style = QuantVaultTheme.typography.headlineSmall,
                    modifier = Modifier.testTag(tag = "AlertTitleText"),
                )
            }
        },
        text = {
            Text(
                text = message,
                style = QuantVaultTheme.typography.bodyMedium,
                modifier = Modifier.testTag(tag = "AlertContentText"),
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

@Preview
@Composable
private fun quantvaultBasicDialog_preview() {
    QuantVaultTheme {
        quantvaultBasicDialog(
            title = "An error has occurred",
            message = "Username or password is incorrect. Try again.",
            onDismissRequest = {},
        )
    }
}







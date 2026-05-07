package com.quantvault.ui.platform.components.account.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.quantvault.ui.platform.components.account.model.AccountSummary
import com.quantvault.ui.platform.components.dialog.quantvaultTwoButtonDialog
import com.quantvault.ui.platform.resource.quantvaultString

/**
 * A reusable dialog for confirming whether the user wants to log out.
 *
 * @param onDismissRequest A callback for when the dialog is requesting dismissal.
 * @param onConfirmClick A callback for when the log out confirmation button is clicked.
 * @param accountSummary Optional account information that may be used to provide additional
 * information.
 */
@Composable
fun quantvaultLogoutConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    accountSummary: AccountSummary? = null,
) {
    val baseConfirmationMessage = stringResource(id = quantvaultString.logout_confirmation)
    val message = accountSummary
        ?.let { "$baseConfirmationMessage\n\n${it.email}\n${it.environmentLabel}" }
        ?: baseConfirmationMessage
    quantvaultTwoButtonDialog(
        title = stringResource(id = quantvaultString.log_out),
        message = message,
        confirmButtonText = stringResource(id = quantvaultString.yes),
        onConfirmClick = onConfirmClick,
        dismissButtonText = stringResource(id = quantvaultString.cancel),
        onDismissClick = onDismissRequest,
        onDismissRequest = onDismissRequest,
    )
}







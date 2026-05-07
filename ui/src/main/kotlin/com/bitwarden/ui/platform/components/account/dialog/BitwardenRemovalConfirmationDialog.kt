package com.quantvault.ui.platform.components.account.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.quantvault.ui.platform.components.account.model.AccountSummary
import com.quantvault.ui.platform.components.dialog.quantvaultTwoButtonDialog
import com.quantvault.ui.platform.resource.quantvaultString

/**
 * A reusable dialog for confirming whether the user wants to remove their account.
 *
 * @param onDismissRequest A callback for when the dialog is requesting dismissal.
 * @param onConfirmClick A callback for when the log-out confirmation button is clicked.
 * @param accountSummary Optional account information that may be used to provide additional
 * information.
 */
@Composable
fun quantvaultRemovalConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    accountSummary: AccountSummary? = null,
) {
    quantvaultTwoButtonDialog(
        title = stringResource(id = quantvaultString.remove_account),
        message = removalConfirmationMessage(accountSummary = accountSummary),
        confirmButtonText = stringResource(id = quantvaultString.yes),
        onConfirmClick = onConfirmClick,
        dismissButtonText = stringResource(id = quantvaultString.cancel),
        onDismissClick = onDismissRequest,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun removalConfirmationMessage(accountSummary: AccountSummary?): String {
    val baseConfirmationMessage = stringResource(id = quantvaultString.remove_account_confirmation)
    return accountSummary
        ?.let { "$baseConfirmationMessage\n\n${it.email}\n${it.environmentLabel}" }
        ?: baseConfirmationMessage
}







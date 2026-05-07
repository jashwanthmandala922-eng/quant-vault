package com.quantvault.ui.platform.components.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.quantvault.ui.platform.components.button.quantvaultTextButton
import com.quantvault.ui.platform.components.field.quantvaultTextField
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault-styled dialog that is used to enter text.
 *
 * @param title The optional title to show.
 * @param textFieldLabel Label for the text field.
 * @param onConfirmClick Called when the confirm button is clicked.
 * @param onDismissRequest Called when the user attempts to dismiss the dialog.
 * @param autoFocus When set to true, the view will request focus after the first recomposition.
 * @param initialText The text that will be visible at the start of text entry.
 */
@Composable
fun quantvaultTextEntryDialog(
    title: String?,
    textFieldLabel: String,
    onConfirmClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    autoFocus: Boolean = false,
    initialText: String? = null,
) {
    var text by remember { mutableStateOf(initialText.orEmpty()) }
    val focusRequester = remember { FocusRequester() }
    var shouldRequestFocus by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            quantvaultTextButton(
                label = stringResource(id = quantvaultString.cancel),
                onClick = onDismissRequest,
                modifier = Modifier.testTag("DismissAlertButton"),
            )
        },
        confirmButton = {
            quantvaultTextButton(
                label = stringResource(id = quantvaultString.okay),
                onClick = { onConfirmClick(text) },
                modifier = Modifier.testTag("AcceptAlertButton"),
            )
        },
        title = title?.let {
            {
                Text(
                    text = it,
                    style = QuantVaultTheme.typography.headlineSmall,
                    modifier = Modifier.testTag("AlertTitleText"),
                )
            }
        },
        text = {
            quantvaultTextField(
                label = textFieldLabel,
                value = text,
                onValueChange = { text = it },
                textFieldTestTag = "AlertContentText",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onGloballyPositioned { shouldRequestFocus = true }
                    .fillMaxWidth(),
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

    if (autoFocus && shouldRequestFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}







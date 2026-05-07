package com.x8bit.bitwarden.ui.vault.feature.addedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultSelectionDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTextEntryDialog
import com.bitwarden.ui.platform.components.dialog.row.QuantVaultBasicDialogRow
import com.x8bit.bitwarden.ui.vault.feature.addedit.model.CustomFieldType
import kotlinx.collections.immutable.ImmutableList
import com.x8bit.bitwarden.R

/**
 * A UI element that is used by the user to add a custom field item.
 *
 * @param options The types that are to be chosen by the user.
 * @param onFinishNamingClick Invoked when the user finishes naming the item.
 */
@Composable
fun VaultAddEditCustomFieldsButton(
    onFinishNamingClick: (CustomFieldType, String) -> Unit,
    options: ImmutableList<CustomFieldType>,
    modifier: Modifier = Modifier,
) {
    var shouldShowChooserDialog by remember { mutableStateOf(false) }
    var shouldShowNameDialog by remember { mutableStateOf(false) }

    var customFieldType: CustomFieldType by remember { mutableStateOf(CustomFieldType.TEXT) }
    var customFieldName: String by remember { mutableStateOf("") }

    if (shouldShowChooserDialog) {
        QuantVaultSelectionDialog(
            title = stringResource(id = R.string.select_type_field),
            onDismissRequest = { shouldShowChooserDialog = false },
        ) {
            options.forEach { type ->
                QuantVaultBasicDialogRow(
                    text = type.typeText.invoke(),
                    onClick = {
                        shouldShowChooserDialog = false
                        shouldShowNameDialog = true
                        customFieldType = type
                    },
                )
            }
        }
    }

    if (shouldShowNameDialog) {
        QuantVaultTextEntryDialog(
            title = stringResource(id = R.string.custom_field_name),
            textFieldLabel = stringResource(id = R.string.name),
            onDismissRequest = { shouldShowNameDialog = false },
            onConfirmClick = {
                shouldShowNameDialog = false
                customFieldName = it
                onFinishNamingClick(customFieldType, customFieldName)
            },
        )
    }
    val focusManager = LocalFocusManager.current
    QuantVaultOutlinedButton(
        label = stringResource(id = R.string.add_field),
        onClick = {
            // Clear any current focused item such as an unrelated text field.
            focusManager.clearFocus()
            shouldShowChooserDialog = true
        },
        modifier = modifier.testTag("NewCustomFieldButton"),
    )
}







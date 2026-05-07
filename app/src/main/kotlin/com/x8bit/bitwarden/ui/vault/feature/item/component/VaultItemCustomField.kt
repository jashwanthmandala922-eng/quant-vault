package com.x8bit.bitwarden.ui.vault.feature.item.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.icon.model.IconData
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.toggle.QuantVaultSwitch
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.vault.feature.item.VaultItemState
import com.x8bit.bitwarden.R

/**
 * Custom Field UI common for all item types.
 */
@Suppress("LongMethod")
@Composable
fun CustomField(
    customField: VaultItemState.ViewState.Content.Common.Custom,
    onCopyCustomHiddenField: (String) -> Unit,
    onCopyCustomTextField: (String) -> Unit,
    onShowHiddenFieldClick: (
        VaultItemState.ViewState.Content.Common.Custom.HiddenField,
        Boolean,
    ) -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    when (customField) {
        is VaultItemState.ViewState.Content.Common.Custom.BooleanField -> {
            QuantVaultSwitch(
                label = customField.name,
                isChecked = customField.value,
                readOnly = true,
                onCheckedChange = { },
                cardStyle = cardStyle,
                modifier = modifier.testTag("ViewCustomBooleanField"),
            )
        }

        is VaultItemState.ViewState.Content.Common.Custom.HiddenField -> {
            if (customField.isCopyable) {
                QuantVaultPasswordField(
                    label = customField.name,
                    value = customField.value,
                    showPasswordChange = { onShowHiddenFieldClick(customField, it) },
                    showPassword = customField.isVisible,
                    onValueChange = { },
                    readOnly = true,
                    singleLine = false,
                    showPasswordTestTag = "CustomFieldShowPasswordButton",
                    passwordFieldTestTag = "CustomFieldValue",
                    actions = {
                        QuantVaultStandardIconButton(
                            vectorIconRes = R.drawable.ic_copy,
                            contentDescription = stringResource(id = R.string.copy),
                            onClick = { onCopyCustomHiddenField(customField.value) },
                            modifier = Modifier.testTag("CustomFieldCopyValueButton"),
                        )
                    },
                    cardStyle = cardStyle,
                    modifier = modifier.testTag("ViewCustomHiddenField"),
                )
            } else {
                QuantVaultPasswordField(
                    label = customField.name,
                    value = customField.value,
                    showPasswordChange = { onShowHiddenFieldClick(customField, it) },
                    showPassword = customField.isVisible,
                    onValueChange = { },
                    readOnly = true,
                    singleLine = false,
                    cardStyle = cardStyle,
                    modifier = modifier,
                )
            }
        }

        is VaultItemState.ViewState.Content.Common.Custom.LinkedField -> {
            QuantVaultTextField(
                label = customField.name,
                value = customField.vaultLinkedFieldType.label.invoke(),
                leadingIconData = IconData.Local(
                    iconRes = R.drawable.ic_linked,
                    contentDescription = R.string.field_type_linked.asText(),
                ),
                onValueChange = { },
                readOnly = true,
                singleLine = false,
                cardStyle = cardStyle,
                textFieldTestTag = "CustomFieldDropdown",
                modifier = modifier.testTag("ViewCustomLinkedField"),
            )
        }

        is VaultItemState.ViewState.Content.Common.Custom.TextField -> {
            QuantVaultTextField(
                label = customField.name,
                value = customField.value,
                onValueChange = { },
                readOnly = true,
                singleLine = false,
                textFieldTestTag = "CustomFieldValue",
                actions = {
                    if (customField.isCopyable) {
                        QuantVaultStandardIconButton(
                            vectorIconRes = R.drawable.ic_copy,
                            contentDescription = stringResource(id = R.string.copy),
                            onClick = { onCopyCustomTextField(customField.value) },
                            modifier = Modifier.testTag("CustomFieldCopyValueButton"),
                        )
                    }
                },
                cardStyle = cardStyle,
                modifier = modifier.testTag("ViewCustomTextField"),
            )
        }
    }
}







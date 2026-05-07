package com.x8bit.bitwarden.ui.vault.feature.addedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quantvault.core.util.persistentListOfNotNull
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.button.model.QuantVaultHelpButtonData
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.header.QuantVaultExpandingHeader
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.toggle.QuantVaultSwitch
import com.x8bit.bitwarden.ui.vault.feature.addedit.handlers.VaultAddEditCommonHandlers
import com.x8bit.bitwarden.ui.vault.feature.addedit.model.CustomFieldType
import com.x8bit.bitwarden.R

/**
 * The collapsable UI for additional options when adding or editing a cipher.
 */
@Suppress("LongMethod")
fun LazyListScope.vaultAddEditAdditionalOptions(
    itemType: VaultAddEditState.ViewState.Content.ItemType,
    commonState: VaultAddEditState.ViewState.Content.Common,
    commonTypeHandlers: VaultAddEditCommonHandlers,
    isAdditionalOptionsExpanded: Boolean,
    onAdditionalOptionsClick: () -> Unit,
) {
    item {
        QuantVaultExpandingHeader(
            isExpanded = isAdditionalOptionsExpanded,
            onClick = onAdditionalOptionsClick,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
    }

    if (isAdditionalOptionsExpanded) {
        val isNotes = itemType is VaultAddEditState.ViewState.Content.ItemType.SecureNotes
        if (!isNotes) {
            item(key = "optionalNotes") {
                QuantVaultTextField(
                    singleLine = false,
                    label = stringResource(id = R.string.notes),
                    value = commonState.notes,
                    onValueChange = commonTypeHandlers.onNotesTextChange,
                    textFieldTestTag = "ItemNotesEntry",
                    cardStyle = CardStyle.Full,
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
            }
        }

        if (commonState.isUnlockWithPasswordEnabled) {
            item(key = "MasterPasswordRepromptToggle") {
                Column(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                ) {
                    if (!isNotes) {
                        Spacer(modifier = Modifier.height(height = 8.dp))
                    }
                    QuantVaultSwitch(
                        label = stringResource(id = R.string.password_prompt),
                        isChecked = commonState.masterPasswordReprompt,
                        onCheckedChange = commonTypeHandlers.onToggleMasterPasswordReprompt,
                        helpData = QuantVaultHelpButtonData(
                            onClick = commonTypeHandlers.onTooltipClick,
                            contentDescription = stringResource(
                                id = R.string.master_password_re_prompt_help,
                            ),
                            isExternalLink = true,
                        ),
                        cardStyle = CardStyle.Full,
                        modifier = Modifier
                            .testTag(tag = "MasterPasswordRepromptToggle")
                            .fillMaxWidth(),
                    )
                }
            }
        }

        item(key = "customFieldsHeader") {
            Column(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            ) {
                Spacer(modifier = Modifier.height(height = 16.dp))
                QuantVaultListHeaderText(
                    label = stringResource(id = R.string.custom_fields),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }

        itemsIndexed(
            items = commonState.customFieldData,
            key = { _, customItem -> "customField_${customItem.itemId}" },
        ) { index, customItem ->
            Column(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            ) {
                Spacer(modifier = Modifier.height(height = 8.dp))
                VaultAddEditCustomField(
                    customField = customItem,
                    onCustomFieldValueChange = commonTypeHandlers.onCustomFieldValueChange,
                    onCustomFieldAction = commonTypeHandlers.onCustomFieldActionSelect,
                    showMoveUpAction = index > 0,
                    showMoveDownAction = index < commonState.customFieldData.lastIndex,
                    onHiddenVisibilityChanged = commonTypeHandlers.onHiddenFieldVisibilityChange,
                    supportedLinkedTypes = itemType.vaultLinkedFieldTypes,
                    cardStyle = CardStyle.Full,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
        }

        item(key = "addCustomFieldButton") {
            Column(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            ) {
                Spacer(modifier = Modifier.height(height = 8.dp))
                VaultAddEditCustomFieldsButton(
                    onFinishNamingClick = commonTypeHandlers.onAddNewCustomFieldClick,
                    options = persistentListOfNotNull(
                        CustomFieldType.TEXT,
                        CustomFieldType.HIDDEN,
                        CustomFieldType.BOOLEAN,
                        CustomFieldType.LINKED.takeIf {
                            itemType.vaultLinkedFieldTypes.isNotEmpty()
                        },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}







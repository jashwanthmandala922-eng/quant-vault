package com.x8bit.bitwarden.ui.vault.feature.item.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.model.CardStyle
import com.x8bit.bitwarden.ui.vault.feature.item.handlers.VaultCommonItemTypeHandlers
import com.x8bit.bitwarden.R

/**
 * Displays the common notes field for the vault item screen.
 *
 * @param notes The notes.
 * @param vaultCommonItemTypeHandlers Provides the handlers required for the notes.
 * @param showHeader Indicates whether to show the header.
 */
fun LazyListScope.vaultItemNotes(
    notes: String?,
    vaultCommonItemTypeHandlers: VaultCommonItemTypeHandlers,
    showHeader: Boolean = true,
) {
    notes ?: return
    item(key = "notes") {
        if (showHeader) {
            Spacer(modifier = Modifier.height(height = 16.dp))
            QuantVaultListHeaderText(
                label = stringResource(id = R.string.additional_options),
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp)
                    .animateItem(),
            )
        }
        Spacer(modifier = Modifier.height(height = 8.dp))
        QuantVaultTextField(
            label = stringResource(id = R.string.notes),
            value = notes,
            onValueChange = { },
            readOnly = true,
            singleLine = false,
            actions = {
                QuantVaultStandardIconButton(
                    vectorIconRes = R.drawable.ic_copy,
                    contentDescription = stringResource(id = R.string.copy_notes),
                    onClick = vaultCommonItemTypeHandlers.onCopyNotesClick,
                    modifier = Modifier.testTag(tag = "CipherNotesCopyButton"),
                )
            },
            textFieldTestTag = "CipherNotesLabel",
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .animateItem(),
        )
    }
}







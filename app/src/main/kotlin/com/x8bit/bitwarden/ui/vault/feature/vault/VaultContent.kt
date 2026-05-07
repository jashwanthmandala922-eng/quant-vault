package com.x8bit.bitwarden.ui.vault.feature.vault

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toListItemCardStyle
import com.bitwarden.ui.platform.components.card.QuantVaultActionCard
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.icon.model.IconData
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.ui.platform.components.dialog.QuantVaultMasterPasswordDialog
import com.x8bit.bitwarden.ui.platform.components.listitem.QuantVaultGroupItem
import com.x8bit.bitwarden.ui.vault.feature.itemlisting.model.ListingItemOverflowAction
import com.x8bit.bitwarden.ui.vault.feature.vault.handlers.VaultHandlers
import com.x8bit.bitwarden.R

private const val TOTP_TYPES_COUNT: Int = 1
private const val HIDDEN_TYPES_COUNT: Int = 2

/**
 * Content view for the [VaultScreen].
 */
@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
fun VaultContent(
    state: VaultState.ViewState.Content,
    actionCardState: VaultState.ActionCardState?,
    vaultHandlers: VaultHandlers,
    modifier: Modifier = Modifier,
) {
    // Handles the master password prompt for the row click
    var masterPasswordRepromptItem by rememberSaveable {
        mutableStateOf<VaultState.ViewState.VaultItem?>(value = null)
    }
    masterPasswordRepromptItem?.let { action ->
        QuantVaultMasterPasswordDialog(
            onConfirmClick = { password ->
                masterPasswordRepromptItem = null
                vaultHandlers.masterPasswordRepromptSubmit(action, password)
            },
            onDismissRequest = { masterPasswordRepromptItem = null },
        )
    }
    // Handles the master password prompt for the overflow clicks
    var overflowMasterPasswordRepromptAction by rememberSaveable {
        mutableStateOf<ListingItemOverflowAction.VaultAction?>(value = null)
    }
    overflowMasterPasswordRepromptAction?.let { action ->
        QuantVaultMasterPasswordDialog(
            onConfirmClick = { password ->
                overflowMasterPasswordRepromptAction = null
                vaultHandlers.overflowMasterPasswordRepromptSubmit(action, password)
            },
            onDismissRequest = { overflowMasterPasswordRepromptAction = null },
        )
    }

    var overflowSpeedBumpAction: ListingItemOverflowAction.VaultAction? by rememberSaveable {
        mutableStateOf(value = null)
    }
    overflowSpeedBumpAction?.let { action ->
        action
            .speedBump
            ?.let { speedBump ->
                QuantVaultTwoButtonDialog(
                    twoButtonDialogData = speedBump,
                    onConfirmClick = {
                        overflowSpeedBumpAction = null
                        vaultHandlers.overflowOptionClick(action)
                    },
                    onDismissClick = { overflowSpeedBumpAction = null },
                    onDismissRequest = { overflowSpeedBumpAction = null },
                )
            }
            ?: run {
                // If we somehow get here and there is no speed bump, then we should keep on going.
                overflowSpeedBumpAction = null
                vaultHandlers.overflowOptionClick(action)
            }
    }

    LazyColumn(
        modifier = modifier,
    ) {
        item(key = "top_spacer") {
            Spacer(modifier = Modifier.height(height = 12.dp))
        }

        actionCardState?.let {
            item(key = "action_card") {
                ActionCard(
                    actionCardState = it,
                    vaultHandlers = vaultHandlers,
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
                Spacer(modifier = Modifier.height(height = 24.dp))
            }
        }

        if (state.totpItemsCount > 0) {
            item(key = "totp_header") {
                QuantVaultListHeaderText(
                    label = stringResource(id = R.string.totp),
                    supportingLabel = TOTP_TYPES_COUNT.toString(),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }

            item(key = "verification_codes_group") {
                QuantVaultGroupItem(
                    startIcon = IconData.Local(iconRes = R.drawable.ic_clock),
                    label = stringResource(id = R.string.verification_codes),
                    supportingLabel = state.totpItemsCount.toString(),
                    onClick = vaultHandlers.verificationCodesClick,
                    cardStyle = CardStyle.Full,
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag("VerificationCodesFilter")
                        .standardHorizontalMargin(),
                )
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
        }

        if (state.favoriteItems.isNotEmpty()) {
            item(key = "favorites_header") {
                QuantVaultListHeaderText(
                    label = stringResource(id = R.string.favorites),
                    supportingLabel = state.favoriteItems.count().toString(),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }

            itemsIndexed(
                items = state.favoriteItems,
                key = { _, favorite -> "favorite_${favorite.id}" },
            ) { index, favoriteItem ->
                VaultEntryListItem(
                    startIcon = favoriteItem.startIcon,
                    startIconTestTag = favoriteItem.startIconTestTag,
                    trailingLabelIcons = favoriteItem.extraIconList,
                    label = favoriteItem.name(),
                    supportingLabel = favoriteItem.supportingLabel?.invoke(),
                    onClick = {
                        if (favoriteItem.shouldShowMasterPasswordReprompt) {
                            masterPasswordRepromptItem = favoriteItem
                        } else {
                            vaultHandlers.vaultItemClick(favoriteItem)
                        }
                    },
                    overflowOptions = favoriteItem.overflowOptions,
                    onOverflowOptionClick = { action ->
                        if (favoriteItem.shouldShowMasterPasswordReprompt &&
                            action.requiresPasswordReprompt
                        ) {
                            overflowMasterPasswordRepromptAction = action
                        } else if (action.speedBump != null) {
                            overflowSpeedBumpAction = action
                        } else {
                            vaultHandlers.overflowOptionClick(action)
                        }
                    },
                    cardStyle = state
                        .favoriteItems
                        .toListItemCardStyle(index = index, dividerPadding = 56.dp),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag("CipherCell")
                        .standardHorizontalMargin(),
                )
            }
            item(key = "favorites_spacer") {
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
        }

        item(key = "types_header") {
            QuantVaultListHeaderText(
                label = stringResource(id = R.string.types),
                supportingLabel = state.itemTypesCount.toString(),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }

        item(key = "logins_group") {
            QuantVaultGroupItem(
                startIcon = IconData.Local(
                    iconRes = R.drawable.ic_globe,
                    testTag = "LoginCipherIcon",
                ),
                label = stringResource(id = R.string.type_login),
                supportingLabel = state.loginItemsCount.toString(),
                onClick = vaultHandlers.loginGroupClick,
                cardStyle = CardStyle.Top(dividerPadding = 56.dp),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .testTag("LoginFilter")
                    .standardHorizontalMargin(),
            )
        }

        if (state.showCardGroup) {
            item(key = "cards_group") {
                QuantVaultGroupItem(
                    startIcon = IconData.Local(
                        iconRes = R.drawable.ic_payment_card,
                        testTag = "CardCipherIcon",
                    ),
                    label = stringResource(id = R.string.type_card),
                    supportingLabel = state.cardItemsCount.toString(),
                    onClick = vaultHandlers.cardGroupClick,
                    cardStyle = CardStyle.Middle(dividerPadding = 56.dp),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag("CardFilter")
                        .standardHorizontalMargin(),
                )
            }
        }

        item(key = "identities_group") {
            QuantVaultGroupItem(
                startIcon = IconData.Local(
                    iconRes = R.drawable.ic_id_card,
                    testTag = "IdentityCipherIcon",
                ),
                label = stringResource(id = R.string.type_identity),
                supportingLabel = state.identityItemsCount.toString(),
                onClick = vaultHandlers.identityGroupClick,
                cardStyle = CardStyle.Middle(dividerPadding = 56.dp),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .testTag("IdentityFilter")
                    .standardHorizontalMargin(),
            )
        }

        item(key = "notes_group") {
            QuantVaultGroupItem(
                startIcon = IconData.Local(
                    iconRes = R.drawable.ic_note,
                    testTag = "SecureNoteCipherIcon",
                ),
                label = stringResource(id = R.string.type_secure_note),
                supportingLabel = state.secureNoteItemsCount.toString(),
                onClick = vaultHandlers.secureNoteGroupClick,
                cardStyle = CardStyle.Middle(dividerPadding = 56.dp),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .testTag("SecureNoteFilter")
                    .standardHorizontalMargin(),
            )
        }

        item(key = "ssh_keys_group") {
            QuantVaultGroupItem(
                startIcon = IconData.Local(
                    iconRes = R.drawable.ic_ssh_key,
                    testTag = "SshKeyCipherIcon",
                ),
                label = stringResource(id = R.string.type_ssh_key),
                supportingLabel = state.sshKeyItemsCount.toString(),
                onClick = vaultHandlers.sshKeyGroupClick,
                cardStyle = CardStyle.Bottom,
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .testTag("SshKeyFilter")
                    .standardHorizontalMargin(),
            )
        }

        item(key = "types_spacer") {
            Spacer(modifier = Modifier.height(height = 16.dp))
        }

        if (state.folderItems.isNotEmpty()) {
            item(key = "folders_header") {
                QuantVaultListHeaderText(
                    label = stringResource(id = R.string.folders),
                    supportingLabel = state.folderItems.count().toString(),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }

            itemsIndexed(
                items = state.folderItems,
                key = { _, folder -> "folder_${folder.id}" },
            ) { index, folder ->
                QuantVaultGroupItem(
                    startIcon = IconData.Local(iconRes = R.drawable.ic_folder),
                    label = folder.name(),
                    supportingLabel = folder.itemCount.toString(),
                    onClick = { vaultHandlers.folderClick(folder) },
                    cardStyle = state
                        .folderItems
                        .toListItemCardStyle(index = index, dividerPadding = 56.dp),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag("FolderFilter")
                        .standardHorizontalMargin(),
                )
            }
            item(key = "folders_spacer") {
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
        }

        if (state.noFolderItems.isNotEmpty()) {
            item(key = "no_folders_header") {
                QuantVaultListHeaderText(
                    label = stringResource(id = R.string.folder_none),
                    supportingLabel = state.noFolderItems.count().toString(),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }
            itemsIndexed(
                items = state.noFolderItems,
                key = { _, noFolderItem -> "no_folder_${noFolderItem.id}" },
            ) { index, noFolderItem ->
                VaultEntryListItem(
                    startIcon = noFolderItem.startIcon,
                    startIconTestTag = noFolderItem.startIconTestTag,
                    trailingLabelIcons = noFolderItem.extraIconList,
                    label = noFolderItem.name(),
                    supportingLabel = noFolderItem.supportingLabel?.invoke(),
                    onClick = {
                        if (noFolderItem.shouldShowMasterPasswordReprompt) {
                            masterPasswordRepromptItem = noFolderItem
                        } else {
                            vaultHandlers.vaultItemClick(noFolderItem)
                        }
                    },
                    overflowOptions = noFolderItem.overflowOptions,
                    onOverflowOptionClick = { action ->
                        if (noFolderItem.shouldShowMasterPasswordReprompt &&
                            action.requiresPasswordReprompt
                        ) {
                            overflowMasterPasswordRepromptAction = action
                        } else if (action.speedBump != null) {
                            overflowSpeedBumpAction = action
                        } else {
                            vaultHandlers.overflowOptionClick(action)
                        }
                    },
                    cardStyle = state
                        .noFolderItems
                        .toListItemCardStyle(index = index, dividerPadding = 56.dp),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag("CipherCell")
                        .standardHorizontalMargin(),
                )
            }
            item(key = "no_folders_spacer") {
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
        }

        if (state.collectionItems.isNotEmpty()) {
            item(key = "collection_header") {
                QuantVaultListHeaderText(
                    label = stringResource(id = R.string.collections),
                    supportingLabel = state.collectionItems.count().toString(),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .standardHorizontalMargin()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }

            itemsIndexed(
                items = state.collectionItems,
                key = { _, collection -> "collection_${collection.id}" },
            ) { index, collection ->
                QuantVaultGroupItem(
                    startIcon = IconData.Local(iconRes = R.drawable.ic_collections),
                    label = collection.name,
                    supportingLabel = collection.itemCount.toString(),
                    onClick = { vaultHandlers.collectionClick(collection) },
                    cardStyle = state
                        .collectionItems
                        .toListItemCardStyle(index = index, dividerPadding = 56.dp),
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .testTag("CollectionFilter")
                        .standardHorizontalMargin(),
                )
            }
            item(key = "collections_spacer") {
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
        }

        item(key = "hidden_items_header") {
            QuantVaultListHeaderText(
                label = stringResource(id = R.string.hidden_items),
                supportingLabel = HIDDEN_TYPES_COUNT.toString(),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }

        item(key = "archive_group") {
            QuantVaultGroupItem(
                startIcon = IconData.Local(iconRes = R.drawable.ic_archive),
                endIcon = state.archiveEndIcon?.let { IconData.Local(iconRes = it) },
                label = stringResource(id = R.string.archive_noun),
                subLabel = state.archiveSubText?.invoke(),
                supportingLabel = state.archivedItemsCount?.toString().orEmpty(),
                onClick = vaultHandlers.archiveClick,
                cardStyle = CardStyle.Top(dividerPadding = 56.dp),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .testTag(tag = "ArchiveFilter")
                    .standardHorizontalMargin(),
            )
        }

        item(key = "trash_group") {
            QuantVaultGroupItem(
                startIcon = IconData.Local(iconRes = R.drawable.ic_trash),
                label = stringResource(id = R.string.trash),
                supportingLabel = state.trashItemsCount.toString(),
                onClick = vaultHandlers.trashClick,
                cardStyle = CardStyle.Bottom,
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .testTag("TrashFilter")
                    .standardHorizontalMargin(),
            )
        }

        item(key = "bottom_padding") {
            Spacer(modifier = Modifier.height(height = 88.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun ActionCard(
    actionCardState: VaultState.ActionCardState,
    vaultHandlers: VaultHandlers,
    modifier: Modifier = Modifier,
) {
    when (actionCardState) {
        VaultState.ActionCardState.UpgradePremium -> {
            QuantVaultActionCard(
                cardTitle = stringResource(
                    id = R.string.unlock_advanced_security_features,
                ),
                cardSubtitle = stringResource(
                    id = R.string.a_premium_plan_gives_you_more_tools_to_stay_secure_and_in_control,
                ),
                actionText = stringResource(id = R.string.upgrade_to_premium),
                onActionClick = { vaultHandlers.actionCardClick(actionCardState) },
                onDismissClick = { vaultHandlers.dismissActionCardClick(actionCardState) },
                modifier = modifier,
            )
        }

        VaultState.ActionCardState.IntroducingArchive -> {
            QuantVaultActionCard(
                cardTitle = stringResource(id = R.string.introducing_archive),
                cardSubtitle = stringResource(
                    id = R.string.keep_items_you_dont_need_right_now_safe_but_out_sight,
                ),
                actionText = stringResource(id = R.string.go_to_archive),
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(id = R.drawable.ic_archive),
                        contentDescription = null,
                        tint = QuantVaultTheme.colorScheme.icon.secondary,
                    )
                },
                onActionClick = { vaultHandlers.actionCardClick(actionCardState) },
                onDismissClick = { vaultHandlers.dismissActionCardClick(actionCardState) },
                modifier = modifier,
            )
        }
    }
}








@file:OmitFromCoverage

package com.x8bit.bitwarden.ui.vault.feature.itemlisting.model

import android.os.Parcelable
import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.sdk.SendType
import com.bitwarden.ui.platform.components.dialog.model.QuantVaultTwoButtonDialogData
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import com.quantvault.sdk.CipherType
import kotlinx.parcelize.Parcelize
import com.x8bit.bitwarden.R

/**
 * Represents the actions for an individual item's overflow menu.
 */
sealed class ListingItemOverflowAction : Parcelable {

    /**
     * The display title of the option.
     */
    abstract val title: Text

    /**
     * The content description of the option.
     */
    abstract val contentDescription: Text

    /**
     * The data to be displayed for an optional speed bump dialog.
     */
    abstract val speedBump: QuantVaultTwoButtonDialogData?

    /**
     * Represents the send actions.
     */
    sealed class SendAction : ListingItemOverflowAction() {
        /**
         * Click on the view send overflow option.
         */
        @Parcelize
        data class ViewClick(
            val sendId: String,
            val sendType: SendType,
        ) : SendAction() {
            override val title: Text get() = R.string.view.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the edit send overflow option.
         */
        @Parcelize
        data class EditClick(
            val sendId: String,
            val sendType: SendType,
        ) : SendAction() {
            override val title: Text get() = R.string.edit.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy send URL overflow option.
         */
        @Parcelize
        data class CopyUrlClick(val sendUrl: String) : SendAction() {
            override val title: Text get() = R.string.copy_link.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the share send URL overflow option.
         */
        @Parcelize
        data class ShareUrlClick(val sendUrl: String) : SendAction() {
            override val title: Text get() = R.string.share_link.asText()
            override val contentDescription: Text
                get() = R.string.external_link_format.asText(title)
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the remove password send overflow option.
         */
        @Parcelize
        data class RemovePasswordClick(val sendId: String) : SendAction() {
            override val title: Text get() = R.string.remove_password.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the delete send overflow option.
         */
        @Parcelize
        data class DeleteClick(val sendId: String) : SendAction() {
            override val title: Text get() = R.string.delete.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData?
                get() = QuantVaultTwoButtonDialogData(
                    title = R.string.delete.asText(),
                    message = R.string.are_you_sure_delete_send.asText(),
                    confirmButtonText = R.string.yes.asText(),
                    dismissButtonText = R.string.cancel.asText(),
                )
        }
    }

    /**
     * Represents the vault actions.
     */
    sealed class VaultAction : ListingItemOverflowAction() {
        /**
         * Whether the action requires a master password re-prompt if that
         * setting is enabled for the selected item.
         */
        abstract val requiresPasswordReprompt: Boolean

        /**
         * Click on the view cipher overflow option.
         */
        @Parcelize
        data class ViewClick(
            val cipherId: String,
            val cipherType: CipherType,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.view.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the edit cipher overflow option.
         */
        @Parcelize
        data class EditClick(
            val cipherId: String,
            val cipherType: CipherType,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.edit.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy username overflow option.
         */
        @Parcelize
        data class CopyUsernameClick(val username: String) : VaultAction() {
            override val title: Text get() = R.string.copy_username.asText()
            override val requiresPasswordReprompt: Boolean get() = false
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy password overflow option.
         */
        @Parcelize
        data class CopyPasswordClick(
            val cipherId: String,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.copy_password.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy TOTP code overflow option.
         */
        @Parcelize
        data class CopyTotpClick(
            val cipherId: String,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.copy_totp.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy number overflow option.
         */
        @Parcelize
        data class CopyNumberClick(
            val cipherId: String,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.copy_number.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy security code overflow option.
         */
        @Parcelize
        data class CopySecurityCodeClick(
            val cipherId: String,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.copy_security_code.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the copy secure note overflow option.
         */
        @Parcelize
        data class CopyNoteClick(
            val cipherId: String,
            override val requiresPasswordReprompt: Boolean,
        ) : VaultAction() {
            override val title: Text get() = R.string.copy_notes.asText()
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the launch overflow option.
         */
        @Parcelize
        data class LaunchClick(val url: String) : VaultAction() {
            override val title: Text get() = R.string.launch.asText()
            override val requiresPasswordReprompt: Boolean get() = false
            override val contentDescription: Text
                get() = R.string.external_link_format.asText(title)
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }

        /**
         * Click on the archive overflow option.
         */
        @Parcelize
        data class ArchiveClick(val cipherId: String) : VaultAction() {
            override val title: Text get() = R.string.archive_verb.asText()
            override val requiresPasswordReprompt: Boolean get() = true
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData?
                get() = QuantVaultTwoButtonDialogData(
                    title = R.string.archive_item.asText(),
                    message = R.string.once_archived_this_item_will_be_excluded.asText(),
                    confirmButtonText = R.string.archive_verb.asText(),
                    dismissButtonText = R.string.cancel.asText(),
                )
        }

        /**
         * Click on the unarchive overflow option.
         */
        @Parcelize
        data class UnarchiveClick(val cipherId: String) : VaultAction() {
            override val title: Text get() = R.string.unarchive.asText()
            override val requiresPasswordReprompt: Boolean get() = true
            override val contentDescription: Text get() = title
            override val speedBump: QuantVaultTwoButtonDialogData? get() = null
        }
    }
}







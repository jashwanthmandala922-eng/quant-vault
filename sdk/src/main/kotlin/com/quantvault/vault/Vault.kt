package com.quantvault.vault

import com.quantvault.sdk.Cipher
import com.quantvault.sdk.CipherView
import com.quantvault.sdk.CipherRepromptType
import com.quantvault.sdk.LoginView
import com.quantvault.sdk.CardView
import com.quantvault.sdk.IdentityView
import com.quantvault.sdk.SecureNoteView
import com.quantvault.sdk.SshKeyView
import com.quantvault.sdk.FieldView
import com.quantvault.sdk.LoginUriView
import com.quantvault.sdk.UriMatchType
import com.quantvault.send.SendType
import com.quantvault.send.SendFileView
import com.quantvault.send.SendTextView
import com.quantvault.send.SendVisibility

enum class UriMatchType {
    DOMAIN, HOST, EXACT, PREFIX, REGEX, NEVER
}

object CipherViewWrapper {
    fun wrap(cipher: Cipher): CipherView = CipherView(
        id = cipher.id ?: "",
        type = cipher.type,
        name = cipher.name,
        notes = cipher.notes,
        folderId = cipher.folderId,
        collectionIds = cipher.collectionIds,
        login = cipher.login?.let { login ->
            LoginView(
                username = login.username,
                password = login.password,
                totp = login.totp,
                uris = login.uris.map { LoginUriView(it.uri, it.match) },
                usernameHidden = false,
                passwordHidden = false
            )
        },
        card = cipher.card?.let { card ->
            CardView(
                cardholderName = card.cardholderName,
                brand = card.brand,
                number = card.number,
                expMonth = card.expMonth,
                expYear = card.expYear,
                code = card.code
            )
        },
        identity = cipher.identity?.let { identity ->
            IdentityView(
                title = identity.title,
                firstName = identity.firstName,
                middleName = identity.middleName,
                lastName = identity.lastName,
                address1 = identity.address1,
                address2 = identity.address2,
                address3 = identity.address3,
                city = identity.city,
                state = identity.state,
                postalCode = identity.postalCode,
                country = identity.country,
                phone = identity.phone,
                email = identity.email,
                ssn = identity.ssn,
                passportNumber = identity.passportNumber,
                licenseNumber = identity.licenseNumber
            )
        },
        secureNote = cipher.secureNote?.let { SecureNoteView(it.type) },
        sshKey = cipher.sshKey?.let { sshKey ->
            SshKeyView(
                privateKey = sshKey.privateKey,
                publicKey = sshKey.publicKey,
                fingerprint = null,
                keyHash = null
            )
        },
        fields = cipher.fields.map { field ->
            FieldView(field.name, field.value, field.type, field.hidden, false)
        },
        passwordHistory = emptyList(),
        reprompt = com.quantvault.sdk.CipherRepromptType.NONE,
        edit = true,
        viewPassword = true,
        organizationId = null,
        attachments = null
    )
}

data class SendView(
    val id: String,
    val name: String,
    val notes: String?,
    val type: com.quantvault.send.SendType,
    val file: com.quantvault.send.SendFileView?,
    val text: com.quantvault.send.SendTextView?,
    val visibility: com.quantvault.send.SendVisibility,
    val password: String?,
    val maxAccessCount: Int?,
    val accessCount: Int,
    val expirationDate: Long?,
    val creationDate: Long,
    val revisionDate: Long,
    val disabled: Boolean,
    val hideEmail: Boolean,
    val organizationId: String?
)

data class SendTextView(
    val text: String,
    val hidden: Boolean
)

data class SendFileView(
    val id: String,
    val fileName: String,
    val size: Long,
    val url: String?
)
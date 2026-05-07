package com.quantvault.sdk

enum class CipherType {
    LOGIN, NOTE, CARD, IDENTITY, SECURE_NOTE, SSH_KEY, BANK_ACCOUNT, SecureNote, SshKey, BankAccount
}

enum class CipherListViewType {
    LOGIN, NOTE, CARD, IDENTITY, SECURE_NOTE, SSH_KEY, BANK_ACCOUNT, SecureNote, SshKey, BankAccount
}

enum class CipherRepromptType {
    NONE, PASSWORD, PIN
}

enum class UriMatchType {
    DOMAIN, HOST, EXACT, PREFIX, REGEX, NEVER
}

enum class FieldType {
    TEXT, HIDDEN, BOOLEAN
}

enum class SecureNoteType {
    GENERIC
}

enum class CopyableCipherFields {
    USERNAME, PASSWORD, TOTP, CARD_NUMBER, CARD_CODE
}

data class CipherView(
    val id: String,
    val type: CipherType,
    val name: String,
    val notes: String?,
    val folderId: String?,
    val collectionIds: List<String>,
    val login: LoginView?,
    val card: CardView?,
    val identity: IdentityView?,
    val secureNote: SecureNoteView?,
    val sshKey: SshKeyView?,
    val fields: List<FieldView>,
    val passwordHistory: List<PasswordHistoryView>,
    val reprompt: CipherRepromptType,
    val edit: Boolean,
    val viewPassword: Boolean,
    val organizationId: String?,
    val attachments: List<AttachmentView>?
)

data class CipherListView(
    val id: String,
    val type: CipherListViewType,
    val name: String,
    val subtitle: String?,
    val favorite: Boolean,
    val reprompt: CipherRepromptType,
    val edit: Boolean,
    val organizationId: String?,
    val hasFido2Credentials: Boolean
)

data class Cipher(
    val id: String?,
    val type: CipherType,
    val name: String,
    val notes: String?,
    val folderId: String?,
    val collectionIds: List<String>,
    val login: Login?,
    val card: Card?,
    val identity: Identity?,
    val secureNote: SecureNote?,
    val sshKey: SshKey?,
    val fields: List<Field>
)

data class LoginView(
    val username: String?,
    val password: String?,
    val totp: String?,
    val uris: List<LoginUriView>,
    val usernameHidden: Boolean,
    val passwordHidden: Boolean
)

data class CardView(
    val cardholderName: String?,
    val brand: String?,
    val number: String?,
    val expMonth: String?,
    val expYear: String?,
    val code: String?
)

data class IdentityView(
    val title: String?,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val address1: String?,
    val address2: String?,
    val address3: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?,
    val phone: String?,
    val email: String?,
    val ssn: String?,
    val passportNumber: String?,
    val licenseNumber: String?
)

data class SecureNoteView(val type: SecureNoteType)
data class SshKeyView(
    val privateKey: String?,
    val publicKey: String?,
    val fingerprint: String?,
    val keyHash: String?
)

data class FieldView(
    val name: String?,
    val value: String?,
    val type: FieldType,
    val hidden: Boolean,
    val protected: Boolean
)

data class LoginUriView(val uri: String?, val match: UriMatchType?)
data class PasswordHistoryView(val password: String, val timestamp: Long)
data class PasswordHistory(val password: String?, val timestamp: Long)
data class AttachmentView(val id: String, val fileName: String, val size: Long, val url: String?)

data class FolderView(val id: String, val name: String, val externalId: String?)
data class Folder(val id: String?, val name: String?, val externalId: String?)
data class CollectionView(val id: String, val name: String, val externalId: String?, val organizationId: String)
data class Fido2Credential(
    val credentialId: String,
    val keyType: String,
    val keyCurve: String,
    val keyValue: String,
    val rpId: String?,
    val userHandle: String?,
    val userName: String?,
    val counter: Long,
    val rpName: String?,
    val userDisplayName: String?
)

data class DecryptCipherListResult(
    val ciphers: List<CipherListView>,
    val folders: List<FolderView>,
    val collections: List<CollectionView>
)

data class Login(val username: String?, val password: String?, val totp: String?, val uris: List<LoginUri>)
data class LoginUri(val uri: String?, val match: UriMatchType?)
data class Card(val cardholderName: String?, val brand: String?, val number: String?, val expMonth: String?, val expYear: String?, val code: String?)
data class Identity(val title: String?, val firstName: String?, val middleName: String?, val lastName: String?, val address1: String?, val address2: String?, val address3: String?, val city: String?, val state: String?, val postalCode: String?, val country: String?, val phone: String?, val email: String?, val ssn: String?, val passportNumber: String?, val licenseNumber: String?)
data class SecureNote(val type: SecureNoteType)
data class SshKey(val privateKey: String?, val publicKey: String?)
data class Field(val name: String?, val value: String?, val type: FieldType, val hidden: Boolean)

typealias Send = com.quantvault.send.Send
typealias SendType = com.quantvault.send.SendType
typealias SendView = com.quantvault.send.SendView
typealias SendFileView = com.quantvault.send.SendFileView
typealias SendTextView = com.quantvault.send.SendTextView
typealias SendVisibility = com.quantvault.send.SendVisibility
typealias CollectionType = com.quantvault.collections.CollectionType
typealias Fido2CredentialStore = com.quantvault.fido.Fido2CredentialStore
typealias Fido2CredentialAutofillView = com.quantvault.fido.Fido2CredentialAutofillView
typealias PublicKeyCredentialAuthenticatorAttestationResponse = com.quantvault.fido.PublicKeyCredentialAuthenticatorAttestationResponse
typealias PublicKeyCredentialAuthenticatorAssertionResponse = com.quantvault.fido.PublicKeyCredentialAuthenticatorAssertionResponse
typealias Account = com.quantvault.exporters.Account
typealias ExportFormat = com.quantvault.exporters.ExportFormat

data class EncryptionContext(
    val cipher: Cipher,
    val key: String
)

enum class AuthType {
    PASSWORD,
    PIN,
    BIOMETRIC
}

enum class CipherViewType {
    LOGIN, NOTE, CARD, IDENTITY, SECURE_NOTE, SSH_KEY, BANK_ACCOUNT
}

data class Attachment(
    val id: String,
    val fileName: String,
    val size: Long,
    val url: String?,
    val key: String?
)

data class TotpResponse(
    val code: String,
    val expirationEpochSeconds: Long
)

data class Collection(
    val id: String?,
    val name: String?,
    val externalId: String?,
    val organizationId: String?
)

data class AttachmentEncryptResult(
    val id: String,
    val fileName: String,
    val size: Long,
    val url: String?,
    val key: String?
)

typealias CollectionId = String

data class Fido2CredentialView(
    val credentialId: String,
    val keyType: String,
    val keyCurve: String,
    val keyValue: String,
    val rpId: String?,
    val userHandle: String?,
    val userName: String?,
    val counter: Long,
    val rpName: String?,
    val userDisplayName: String?
)

data class CipherViewWrapper(
    val cipherView: CipherView,
    val viewType: CipherViewType,
    val hasAttachments: Boolean = false,
    val hasFido2Credentials: Boolean = false
)
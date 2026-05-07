@file:Suppress("TooManyFunctions")

package com.x8bit.bitwarden.data.vault.repository.util

import com.quantvault.core.data.repository.util.SpecialCharWithPrecedenceComparator
import com.quantvault.network.model.AttachmentJsonRequest
import com.quantvault.network.model.CipherJsonRequest
import com.quantvault.network.model.CipherMiniResponseJson
import com.quantvault.network.model.CipherRepromptTypeJson
import com.quantvault.network.model.CipherTypeJson
import com.quantvault.network.model.FieldTypeJson
import com.quantvault.network.model.LinkedIdTypeJson
import com.quantvault.network.model.SecureNoteTypeJson
import com.quantvault.network.model.SyncResponseJson
import com.quantvault.network.model.UriMatchTypeJson
import com.quantvault.sdk.Attachment
import com.quantvault.sdk.Card
import com.quantvault.sdk.CardListView
import com.quantvault.sdk.Cipher
import com.quantvault.sdk.CipherListView
import com.quantvault.sdk.CipherListViewType
import com.quantvault.sdk.CipherPermissions
import com.quantvault.sdk.CipherRepromptType
import com.quantvault.sdk.CipherType
import com.quantvault.sdk.CipherView
import com.quantvault.sdk.EncryptionContext
import com.quantvault.sdk.Fido2Credential
import com.quantvault.sdk.Field
import com.quantvault.sdk.FieldType
import com.quantvault.sdk.Identity
import com.quantvault.sdk.Login
import com.quantvault.sdk.LoginListView
import com.quantvault.sdk.LoginUri
import com.quantvault.sdk.PasswordHistory
import com.quantvault.sdk.SecureNote
import com.quantvault.sdk.SecureNoteType
import com.quantvault.sdk.SshKey
import com.quantvault.sdk.UriMatchType

/**
 * Converts a Quant Vault SDK [Cipher] object to a corresponding
 * [SyncResponseJson.Cipher] object.
 *
 * @param encryptedFor The ID of the user who this cipher is encrypted by.
 */
fun Cipher.toEncryptedNetworkCipher(
    encryptedFor: String,
): CipherJsonRequest =
    CipherJsonRequest(
        notes = notes,
        attachments = attachments
            ?.filter { it.id != null }
            ?.associate { requireNotNull(it.id) to it.toNetworkAttachmentRequest() },
        reprompt = reprompt.toNetworkRepromptType(),
        passwordHistory = passwordHistory?.toEncryptedNetworkPasswordHistoryList(),
        lastKnownRevisionDate = revisionDate,
        type = type.toNetworkCipherType(),
        login = login?.toEncryptedNetworkLogin(),
        secureNote = secureNote?.toEncryptedNetworkSecureNote(),
        folderId = folderId,
        organizationId = organizationId,
        identity = identity?.toEncryptedNetworkIdentity(),
        name = name,
        fields = fields?.toEncryptedNetworkFieldList(),
        isFavorite = favorite,
        card = card?.toEncryptedNetworkCard(),
        key = key,
        sshKey = sshKey?.toEncryptedNetworkSshKey(),
        archivedDate = archivedDate,
        encryptedFor = encryptedFor,
    )

/**
 * Converts a Quant Vault SDK [Cipher] object to a corresponding
 * [SyncResponseJson.Cipher] object.
 *
 * @param encryptedFor The ID of the user who this cipher is encrypted by.
 */
fun Cipher.toEncryptedNetworkCipherResponse(
    encryptedFor: String,
): SyncResponseJson.Cipher =
    SyncResponseJson.Cipher(
        notes = notes,
        reprompt = reprompt.toNetworkRepromptType(),
        passwordHistory = passwordHistory?.toEncryptedNetworkPasswordHistoryList(),
        permissions = permissions?.toEncryptedNetworkCipherPermissions(),
        type = type.toNetworkCipherType(),
        login = login?.toEncryptedNetworkLogin(),
        secureNote = secureNote?.toEncryptedNetworkSecureNote(),
        folderId = folderId,
        organizationId = organizationId,
        identity = identity?.toEncryptedNetworkIdentity(),
        name = name,
        fields = fields?.toEncryptedNetworkFieldList(),
        isFavorite = favorite,
        card = card?.toEncryptedNetworkCard(),
        attachments = attachments?.toNetworkAttachmentList(),
        sshKey = sshKey?.toEncryptedNetworkSshKey(),
        shouldOrganizationUseTotp = organizationUseTotp,
        shouldEdit = edit,
        revisionDate = revisionDate,
        creationDate = creationDate,
        deletedDate = deletedDate,
        collectionIds = collectionIds,
        id = id.orEmpty(),
        shouldViewPassword = viewPassword,
        key = key,
        encryptedFor = encryptedFor,
        archivedDate = archivedDate,
    )

/**
 * Updates a [SyncResponseJson.Cipher] with metadata from a
 * [CipherMiniResponseJson.CipherMiniResponse].
 * This is useful for updating local cipher data after bulk operations that return mini responses.
 *
 * @param miniResponse The mini response containing updated cipher metadata.
 * @param collectionIds Optional list of collection IDs to update.
 * If null, keeps existing collection IDs.
 * @return A new [SyncResponseJson.Cipher] with updated fields from the mini response.
 */
fun SyncResponseJson.Cipher.updateFromMiniResponse(
    miniResponse: CipherMiniResponseJson.CipherMiniResponse,
    collectionIds: List<String>? = null,
): SyncResponseJson.Cipher = copy(
    organizationId = miniResponse.organizationId,
    collectionIds = collectionIds ?: this.collectionIds,
    revisionDate = miniResponse.revisionDate,
    key = miniResponse.key,
    attachments = miniResponse.attachments,
    archivedDate = miniResponse.archivedDate,
    deletedDate = miniResponse.deletedDate,
    reprompt = miniResponse.reprompt,
    shouldOrganizationUseTotp = miniResponse.shouldOrganizationUseTotp,
    type = miniResponse.type,
)

/**
 * Converts a Quant Vault SDK [Card] object to a corresponding
 * [SyncResponseJson.Cipher.Card] object.
 */
private fun Card.toEncryptedNetworkCard(): SyncResponseJson.Cipher.Card =
    SyncResponseJson.Cipher.Card(
        number = number,
        expMonth = expMonth,
        code = code,
        expirationYear = expYear,
        cardholderName = cardholderName,
        brand = brand,
    )

private fun SshKey.toEncryptedNetworkSshKey(): SyncResponseJson.Cipher.SshKey =
    SyncResponseJson.Cipher.SshKey(
        publicKey = publicKey,
        privateKey = privateKey,
        keyFingerprint = fingerprint,
    )

/**
 * Converts a list of Quant Vault SDK [Field] objects to a corresponding
 * list of [SyncResponseJson.Cipher.Field] objects.
 */
private fun List<Field>.toEncryptedNetworkFieldList(): List<SyncResponseJson.Cipher.Field> =
    this.map { it.toEncryptedNetworkField() }

/**
 * Converts a Quant Vault SDK [Field] object to a corresponding
 * [SyncResponseJson.Cipher.Field] object.
 */
private fun Field.toEncryptedNetworkField(): SyncResponseJson.Cipher.Field =
    SyncResponseJson.Cipher.Field(
        linkedIdType = linkedId?.toNetworkLinkedIdType(),
        name = name,
        type = type.toNetworkFieldType(),
        value = value,
    )

private fun UInt.toNetworkLinkedIdType(): LinkedIdTypeJson =
    LinkedIdTypeJson.entries.first { this == it.value }

/**
 * Converts a Quant Vault SDK [FieldType] object to a corresponding
 * [FieldTypeJson] object.
 */
private fun FieldType.toNetworkFieldType(): FieldTypeJson =
    when (this) {
        FieldType.TEXT -> FieldTypeJson.TEXT
        FieldType.HIDDEN -> FieldTypeJson.HIDDEN
        FieldType.BOOLEAN -> FieldTypeJson.BOOLEAN
        FieldType.LINKED -> FieldTypeJson.LINKED
    }

/**
 * Converts a Quant Vault SDK [Identity] object to a corresponding
 * [SyncResponseJson.Cipher.Identity] object.
 */
private fun Identity.toEncryptedNetworkIdentity(): SyncResponseJson.Cipher.Identity =
    SyncResponseJson.Cipher.Identity(
        title = title,
        middleName = middleName,
        firstName = firstName,
        lastName = lastName,
        address1 = address1,
        address2 = address2,
        address3 = address3,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country,
        company = company,
        email = email,
        phone = phone,
        ssn = ssn,
        username = username,
        passportNumber = passportNumber,
        licenseNumber = licenseNumber,
    )

/**
 * Converts a Quant Vault SDK [SecureNote] object to a corresponding
 * [SyncResponseJson.Cipher.SecureNote] object.
 */
private fun SecureNote.toEncryptedNetworkSecureNote(): SyncResponseJson.Cipher.SecureNote =
    SyncResponseJson.Cipher.SecureNote(
        type = when (type) {
            SecureNoteType.GENERIC -> SecureNoteTypeJson.GENERIC
        },
    )

/**
 * Converts a list of Quant Vault SDK [LoginUri] objects to a corresponding
 * list of [SyncResponseJson.Cipher.Login.Uri] objects.
 */
private fun List<LoginUri>.toEncryptedNetworkUriList(): List<SyncResponseJson.Cipher.Login.Uri> =
    this.map { it.toEncryptedNetworkUri() }

/**
 * Converts a Quant Vault SDK [LoginUri] object to a corresponding
 * [SyncResponseJson.Cipher.Login.Uri] object.
 */
private fun LoginUri.toEncryptedNetworkUri(): SyncResponseJson.Cipher.Login.Uri =
    SyncResponseJson.Cipher.Login.Uri(
        uriMatchType = match?.toNetworkMatchType(),
        uri = uri,
        uriChecksum = uriChecksum,
    )

private fun UriMatchType.toNetworkMatchType(): UriMatchTypeJson =
    when (this) {
        UriMatchType.DOMAIN -> UriMatchTypeJson.DOMAIN
        UriMatchType.HOST -> UriMatchTypeJson.HOST
        UriMatchType.STARTS_WITH -> UriMatchTypeJson.STARTS_WITH
        UriMatchType.EXACT -> UriMatchTypeJson.EXACT
        UriMatchType.REGULAR_EXPRESSION -> UriMatchTypeJson.REGULAR_EXPRESSION
        UriMatchType.NEVER -> UriMatchTypeJson.NEVER
    }

/**
 * Converts a list of Quant Vault SDK [Attachment] objects to a corresponding
 * [SyncResponseJson.Cipher.Attachment] list.
 */
private fun List<Attachment>.toNetworkAttachmentList(): List<SyncResponseJson.Cipher.Attachment> =
    map { it.toNetworkAttachment() }

/**
 * Converts a Quant Vault SDK [Attachment] object to a corresponding
 * [SyncResponseJson.Cipher.Attachment] object.
 */
private fun Attachment.toNetworkAttachment(): SyncResponseJson.Cipher.Attachment =
    SyncResponseJson.Cipher.Attachment(
        fileName = fileName,
        size = size?.toInt() ?: 0,
        sizeName = sizeName,
        id = id,
        url = url,
        key = key,
    )

/**
 * Converts a Quant Vault SDK [Attachment] object to a corresponding [AttachmentJsonRequest] object.
 */
fun Attachment.toNetworkAttachmentRequest(): AttachmentJsonRequest =
    AttachmentJsonRequest(
        fileName = fileName,
        fileSize = size,
        key = key,
    )

/**
 * Converts a Quant Vault SDK [Login] object to a corresponding
 * [SyncResponseJson.Cipher.Login] object.
 */
private fun Login.toEncryptedNetworkLogin(): SyncResponseJson.Cipher.Login =
    SyncResponseJson.Cipher.Login(
        uris = uris?.toEncryptedNetworkUriList(),
        totp = totp,
        password = password,
        passwordRevisionDate = passwordRevisionDate,
        shouldAutofillOnPageLoad = autofillOnPageLoad,
        // uri needs to be null to avoid duplicating the first url entry for a login item.
        uri = null,
        username = username,
        fido2Credentials = fido2Credentials?.toNetworkFido2Credentials(),
    )

private fun List<Fido2Credential>.toNetworkFido2Credentials() =
    this.map { it.toNetworkFido2Credential() }

private fun Fido2Credential.toNetworkFido2Credential() = SyncResponseJson.Cipher.Fido2Credential(
    credentialId = credentialId,
    keyType = keyType,
    keyAlgorithm = keyAlgorithm,
    keyCurve = keyCurve,
    keyValue = keyValue,
    rpId = rpId,
    rpName = rpName,
    userHandle = userHandle,
    userName = userName,
    userDisplayName = userDisplayName,
    counter = counter,
    discoverable = discoverable,
    creationDate = creationDate,
)

/**
 * Converts a list of Quant Vault SDK [PasswordHistory] objects to a corresponding
 * list of [SyncResponseJson.Cipher.PasswordHistory] objects.
 */
@Suppress("MaxLineLength")
private fun List<PasswordHistory>.toEncryptedNetworkPasswordHistoryList(): List<SyncResponseJson.Cipher.PasswordHistory> =
    this.map { it.toEncryptedNetworkPasswordHistory() }

/**
 * Converts a Quant Vault SDK [PasswordHistory] object to a corresponding
 * [SyncResponseJson.Cipher.PasswordHistory] object.
 */
@Suppress("MaxLineLength")
private fun PasswordHistory.toEncryptedNetworkPasswordHistory(): SyncResponseJson.Cipher.PasswordHistory =
    SyncResponseJson.Cipher.PasswordHistory(
        password = password,
        lastUsedDate = lastUsedDate,
    )

/**
 * Converts a Quant Vault SDK [CipherPermissions] object to a corresponding
 * [SyncResponseJson.Cipher.CipherPermissions] object.
 */
@Suppress("MaxLineLength")
private fun CipherPermissions.toEncryptedNetworkCipherPermissions(): SyncResponseJson.Cipher.CipherPermissions =
    SyncResponseJson.Cipher.CipherPermissions(
        delete = delete,
        restore = restore,
    )

/**
 * Converts a Quant Vault SDK [CipherRepromptType] object to a corresponding
 * [CipherRepromptTypeJson] object.
 */
private fun CipherRepromptType.toNetworkRepromptType(): CipherRepromptTypeJson =
    when (this) {
        CipherRepromptType.NONE -> CipherRepromptTypeJson.NONE
        CipherRepromptType.PASSWORD -> CipherRepromptTypeJson.PASSWORD
    }

/**
 * Converts a Quant Vault SDK [CipherType] object to a corresponding
 * [CipherTypeJson] object.
 */
private fun CipherType.toNetworkCipherType(): CipherTypeJson =
    when (this) {
        CipherType.LOGIN -> CipherTypeJson.LOGIN
        CipherType.SECURE_NOTE -> CipherTypeJson.SECURE_NOTE
        CipherType.CARD -> CipherTypeJson.CARD
        CipherType.IDENTITY -> CipherTypeJson.IDENTITY
        CipherType.SSH_KEY -> CipherTypeJson.SSH_KEY
        CipherType.BANK_ACCOUNT -> TODO("PM-32810: Add Bank Account Type")
    }

/**
 * Converts a list of [SyncResponseJson.Cipher] objects to a list of corresponding
 * Quant Vault SDK [Cipher] objects.
 */
fun List<SyncResponseJson.Cipher>.toEncryptedSdkCipherList(): List<Cipher> =
    map { it.toEncryptedSdkCipher() }

/**
 * Converts a [SyncResponseJson.Cipher] object to a corresponding
 * Quant Vault SDK [Cipher] object.
 */
fun SyncResponseJson.Cipher.toEncryptedSdkCipher(): Cipher =
    Cipher(
        id = id,
        organizationId = organizationId,
        folderId = folderId,
        collectionIds = collectionIds.orEmpty(),
        key = key,
        name = name.orEmpty(),
        notes = notes,
        type = type.toSdkCipherType(),
        login = login?.toSdkLogin(),
        identity = identity?.toSdkIdentity(),
        sshKey = sshKey?.toSdkSshKey(),
        card = card?.toSdkCard(),
        secureNote = secureNote?.toSdkSecureNote(),
        // TODO: PM-32810: Add Bank Account Type
        bankAccount = null,
        favorite = isFavorite,
        reprompt = reprompt.toSdkRepromptType(),
        organizationUseTotp = shouldOrganizationUseTotp,
        edit = shouldEdit,
        viewPassword = shouldViewPassword,
        localData = null,
        attachments = attachments?.toSdkAttachmentList(),
        fields = fields?.toSdkFieldList(),
        passwordHistory = passwordHistory?.toSdkPasswordHistoryList(),
        permissions = permissions?.toSdkPermissions(),
        creationDate = creationDate,
        deletedDate = deletedDate,
        revisionDate = revisionDate,
        archivedDate = archivedDate,
        data = null,
    )

/**
 * Transforms a [SyncResponseJson.Cipher.Login] into the corresponding Quant Vault SDK [Login].
 */
fun SyncResponseJson.Cipher.Login.toSdkLogin(): Login =
    Login(
        username = username,
        password = password,
        passwordRevisionDate = passwordRevisionDate,
        uris = uris?.toSdkLoginUriList(),
        totp = totp,
        autofillOnPageLoad = shouldAutofillOnPageLoad,
        fido2Credentials = fido2Credentials?.toSdkFido2Credentials(),
    )

private fun List<SyncResponseJson.Cipher.Fido2Credential>.toSdkFido2Credentials() =
    this.map { it.toSdkFido2Credential() }

private fun SyncResponseJson.Cipher.Fido2Credential.toSdkFido2Credential() = Fido2Credential(
    credentialId = credentialId,
    keyType = keyType,
    keyAlgorithm = keyAlgorithm,
    keyCurve = keyCurve,
    keyValue = keyValue,
    rpId = rpId,
    rpName = rpName,
    userHandle = userHandle,
    userName = userName,
    userDisplayName = userDisplayName,
    counter = counter,
    discoverable = discoverable,
    creationDate = creationDate,
)

/**
 * Transforms a [SyncResponseJson.Cipher.Identity] into the corresponding Quant Vault SDK [Identity].
 */
fun SyncResponseJson.Cipher.Identity.toSdkIdentity(): Identity =
    Identity(
        title = title,
        middleName = middleName,
        firstName = firstName,
        lastName = lastName,
        address1 = address1,
        address2 = address2,
        address3 = address3,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country,
        company = company,
        email = email,
        phone = phone,
        ssn = ssn,
        username = username,
        passportNumber = passportNumber,
        licenseNumber = licenseNumber,
    )

/**
 * Transforms a [SyncResponseJson.Cipher.Card] into the corresponding Quant Vault SDK [Card].
 */
fun SyncResponseJson.Cipher.Card.toSdkCard(): Card =
    Card(
        cardholderName = cardholderName,
        expMonth = expMonth,
        expYear = expirationYear,
        code = code,
        brand = brand,
        number = number,
    )

/**
 * Transforms a [SyncResponseJson.Cipher.SecureNote] into
 * the corresponding Quant Vault SDK [SecureNote].
 */
fun SyncResponseJson.Cipher.SecureNote.toSdkSecureNote(): SecureNote =
    SecureNote(
        type = when (type) {
            SecureNoteTypeJson.GENERIC -> SecureNoteType.GENERIC
        },
    )

/**
 * Transforms a [SyncResponseJson.Cipher.SshKey] into
 * the corresponding Quant Vault SDK [SshKey].
 */
fun SyncResponseJson.Cipher.SshKey.toSdkSshKey(): SshKey =
    SshKey(
        publicKey = publicKey,
        privateKey = privateKey,
        fingerprint = keyFingerprint,
    )

/**
 * Transforms a list of [SyncResponseJson.Cipher.Login.Uri] into
 * a corresponding list of  Quant Vault SDK [LoginUri].
 */
fun List<SyncResponseJson.Cipher.Login.Uri>.toSdkLoginUriList(): List<LoginUri> =
    map { it.toSdkLoginUri() }

/**
 * Transforms a [SyncResponseJson.Cipher.Login.Uri] into
 * a corresponding Quant Vault SDK [LoginUri].
 */
fun SyncResponseJson.Cipher.Login.Uri.toSdkLoginUri(): LoginUri =
    LoginUri(
        uri = uri,
        match = uriMatchType?.toSdkMatchType(),
        uriChecksum = uriChecksum,
    )

/**
 * Transforms a list of [SyncResponseJson.Cipher.Attachment] into
 * a corresponding list of  Quant Vault SDK [Attachment].
 */
fun List<SyncResponseJson.Cipher.Attachment>.toSdkAttachmentList(): List<Attachment> =
    map { it.toSdkAttachment() }

/**
 * Transforms a [SyncResponseJson.Cipher.Attachment] into
 * a corresponding Quant Vault SDK [Attachment].
 */
fun SyncResponseJson.Cipher.Attachment.toSdkAttachment(): Attachment =
    Attachment(
        id = id,
        url = url,
        size = size.toString(),
        sizeName = sizeName,
        fileName = fileName,
        key = key,
    )

/**
 * Transforms a list of [SyncResponseJson.Cipher.Field] into
 * a corresponding list of  Quant Vault SDK [Field].
 */
fun List<SyncResponseJson.Cipher.Field>.toSdkFieldList(): List<Field> =
    map { it.toSdkField() }

/**
 * Transforms a [SyncResponseJson.Cipher.Field] into
 * a corresponding Quant Vault SDK [Field].
 */
fun SyncResponseJson.Cipher.Field.toSdkField(): Field =
    Field(
        name = name,
        value = value,
        type = type.toSdkFieldType(),
        linkedId = linkedIdType?.value,
    )

/**
 * Transforms a list of [SyncResponseJson.Cipher.PasswordHistory] into
 * a corresponding list of  Quant Vault SDK [PasswordHistory].
 */
@Suppress("MaxLineLength")
fun List<SyncResponseJson.Cipher.PasswordHistory>.toSdkPasswordHistoryList(): List<PasswordHistory> =
    map { it.toSdkPasswordHistory() }

/**
 * Transforms a [SyncResponseJson.Cipher.PasswordHistory] into
 * a corresponding Quant Vault SDK [PasswordHistory].
 */
fun SyncResponseJson.Cipher.PasswordHistory.toSdkPasswordHistory(): PasswordHistory =
    PasswordHistory(
        password = password,
        lastUsedDate = lastUsedDate,
    )

/**
 * Transforms a [SyncResponseJson.Cipher.CipherPermissions] into
 * a corresponding Quant Vault SDK [CipherPermissions].
 */
fun SyncResponseJson.Cipher.CipherPermissions.toSdkPermissions(): CipherPermissions =
    CipherPermissions(
        delete = delete,
        restore = restore,
    )

/**
 * Transforms a [CipherTypeJson] to the corresponding Quant Vault SDK [CipherType].
 */
fun CipherTypeJson.toSdkCipherType(): CipherType =
    when (this) {
        CipherTypeJson.LOGIN -> CipherType.LOGIN
        CipherTypeJson.SECURE_NOTE -> CipherType.SECURE_NOTE
        CipherTypeJson.CARD -> CipherType.CARD
        CipherTypeJson.IDENTITY -> CipherType.IDENTITY
        CipherTypeJson.SSH_KEY -> CipherType.SSH_KEY
    }

/**
 * Transforms a [UriMatchTypeJson] to the corresponding Quant Vault SDK [UriMatchType].
 */
fun UriMatchTypeJson.toSdkMatchType(): UriMatchType =
    when (this) {
        UriMatchTypeJson.DOMAIN -> UriMatchType.DOMAIN
        UriMatchTypeJson.HOST -> UriMatchType.HOST
        UriMatchTypeJson.STARTS_WITH -> UriMatchType.STARTS_WITH
        UriMatchTypeJson.EXACT -> UriMatchType.EXACT
        UriMatchTypeJson.REGULAR_EXPRESSION -> UriMatchType.REGULAR_EXPRESSION
        UriMatchTypeJson.NEVER -> UriMatchType.NEVER
    }

/**
 * Transforms a [CipherRepromptTypeJson] to the corresponding Quant Vault SDK [CipherRepromptType].
 */
fun CipherRepromptTypeJson.toSdkRepromptType(): CipherRepromptType =
    when (this) {
        CipherRepromptTypeJson.NONE -> CipherRepromptType.NONE
        CipherRepromptTypeJson.PASSWORD -> CipherRepromptType.PASSWORD
    }

/**
 * Transforms a [FieldTypeJson] to the corresponding Quant Vault SDK [FieldType].
 */
fun FieldTypeJson.toSdkFieldType(): FieldType =
    when (this) {
        FieldTypeJson.TEXT -> FieldType.TEXT
        FieldTypeJson.HIDDEN -> FieldType.HIDDEN
        FieldTypeJson.BOOLEAN -> FieldType.BOOLEAN
        FieldTypeJson.LINKED -> FieldType.LINKED
    }

/**
 * Sorts the data in alphabetical order by name. Using lexicographical sorting but giving
 * precedence to special characters over letters and digits.
 */
@JvmName("toAlphabeticallySortedCipherList")
fun List<CipherView>.sortAlphabetically(): List<CipherView> {
    return this.sortedWith(
        comparator = { cipher1, cipher2 ->
            SpecialCharWithPrecedenceComparator.compare(cipher1.name, cipher2.name)
        },
    )
}

/**
 * Sorts the data in alphabetical order by name. Using lexicographical sorting but giving
 * precedence to special characters over letters and digits.
 */
@JvmName("toAlphabeticallySortedCipherListView")
fun List<CipherListView>.sortAlphabetically(): List<CipherListView> {
    return this.sortedWith(
        comparator = { cipher1, cipher2 ->
            SpecialCharWithPrecedenceComparator.compare(cipher1.name, cipher2.name)
        },
    )
}

/**
 * Converts a Quant Vault SDK [EncryptionContext] object to a corresponding [CipherJsonRequest]
 * object.
 */
fun EncryptionContext.toEncryptedNetworkCipher(): CipherJsonRequest =
    cipher.toEncryptedNetworkCipher(encryptedFor = encryptedFor)

/**
 * Converts a Quant Vault SDK [EncryptionContext] object to a corresponding [SyncResponseJson.Cipher]
 * object.
 */
fun EncryptionContext.toEncryptedNetworkCipherResponse(): SyncResponseJson.Cipher =
    cipher.toEncryptedNetworkCipherResponse(encryptedFor = encryptedFor)

/**
 * Converts a Quant Vault SDK [Cipher] object to a corresponding
 * [CipherListView] object with modified field to represent a decryption error instance.
 * This allows reuse of existing logic for filtering and grouping ciphers to construct
 * the sections in the vault list.
 */
fun Cipher.toFailureCipherListView(): CipherListView =
    CipherListView(
        id = id,
        organizationId = organizationId,
        folderId = folderId,
        collectionIds = collectionIds,
        key = key,
        name = name,
        subtitle = "",
        type = when (type) {
            CipherType.LOGIN -> CipherListViewType.Login(
                v1 = LoginListView(
                    fido2Credentials = null,
                    hasFido2 = false,
                    username = null,
                    totp = null,
                    uris = null,
                ),
            )

            CipherType.SECURE_NOTE -> CipherListViewType.SecureNote
            CipherType.CARD -> CipherListViewType.Card(
                CardListView(
                    brand = null,
                ),
            )

            CipherType.IDENTITY -> CipherListViewType.Identity
            CipherType.SSH_KEY -> CipherListViewType.SshKey
            CipherType.BANK_ACCOUNT -> CipherListViewType.BankAccount
        },
        favorite = favorite,
        reprompt = reprompt,
        organizationUseTotp = organizationUseTotp,
        edit = edit,
        permissions = permissions,
        viewPassword = viewPassword,
        attachments = 0.toUInt(),
        hasOldAttachments = false,
        localData = null,
        creationDate = creationDate,
        deletedDate = deletedDate,
        revisionDate = revisionDate,
        copyableFields = emptyList(),
        archivedDate = archivedDate,
    )





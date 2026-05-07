package com.quantvault.sdk

import com.quantvault.auth.DeriveKeyConnectorResult
import com.quantvault.crypto.InitUserCryptoMethod
import com.quantvault.crypto.InitOrgCryptoRequest
import com.quantvault.auth.MasterPasswordPolicyOptions
import com.quantvault.crypto.*
import com.quantvault.exporters.Account
import com.quantvault.exporters.ExportFormat
import com.quantvault.fido.*
import java.io.File
import java.time.Instant

interface VaultSdkSource {
    fun clearCrypto(userId: String)
    suspend fun getTrustDevice(userId: String): Result<TrustDeviceResponse>
    suspend fun deriveKeyConnector(userId: String, userKeyEncrypted: String, email: String, password: String, kdf: Kdf): Result<DeriveKeyConnectorResult>
    suspend fun enrollPin(userId: String, pin: String): Result<EnrollPinResponse>
    suspend fun enrollPinWithEncryptedPin(userId: String, encryptedPin: String): Result<EnrollPinResponse>
    suspend fun validatePinUserKey(userId: String, pin: String, pinProtectedUserKeyEnvelope: String): Result<Boolean>
    suspend fun getAuthRequestKey(publicKey: String, userId: String): Result<String>
    suspend fun getResetPasswordKey(orgPublicKey: String, userId: String): Result<String>
    suspend fun getUserEncryptionKey(userId: String): Result<String>
    suspend fun getUserFingerprint(userId: String): Result<String>
    suspend fun initializeCrypto(userId: String, initData: ByteArray, method: InitUserCryptoMethod): Result<InitializeCryptoResult>
    suspend fun initializeOrganizationCrypto(userId: String, request: InitOrgCryptoRequest): Result<InitializeCryptoResult>
    suspend fun encryptAttachment(userId: String, cipher: Cipher, attachmentView: AttachmentView, decryptedFilePath: String, encryptedFilePath: String): Result<Attachment>
    suspend fun encryptCipher(userId: String, cipherView: CipherView): Result<EncryptionContext>
    suspend fun decryptCipher(userId: String, cipher: Cipher): Result<CipherView>
    suspend fun decryptCipherListWithFailures(userId: String, cipherList: List<Cipher>): Result<DecryptCipherListResult>
    suspend fun decryptCollection(userId: String, collection: Collection): Result<CollectionView>
    suspend fun decryptCollectionList(userId: String, collectionList: List<Collection>): Result<List<CollectionView>>
    suspend fun encryptSend(userId: String, sendView: SendView): Result<Send>
    suspend fun encryptBuffer(userId: String, send: Send, fileBuffer: ByteArray): Result<ByteArray>
    suspend fun encryptFile(userId: String, send: Send, path: String, destinationFilePath: String): Result<File>
    suspend fun decryptSend(userId: String, send: Send): Result<SendView>
    suspend fun decryptSendList(userId: String, sendList: List<Send>): Result<List<SendView>>
    suspend fun encryptFolder(userId: String, folderView: FolderView): Result<Folder>
    suspend fun decryptFolder(userId: String, folder: Folder): Result<FolderView>
    suspend fun decryptFolderList(userId: String, folderList: List<Folder>): Result<List<FolderView>>
    suspend fun decryptFile(userId: String, cipher: Cipher, attachmentView: AttachmentView, encryptedFilePath: String, decryptedFilePath: String): Result<Unit>
    suspend fun encryptPasswordHistory(userId: String, passwordHistoryView: PasswordHistoryView): Result<PasswordHistory>
    suspend fun decryptPasswordHistoryList(userId: String, passwordHistoryList: List<PasswordHistory>): Result<List<PasswordHistoryView>>
    suspend fun generateTotpForCipherListView(userId: String, cipherListView: CipherListView, time: Instant?): Result<TotpResponse>
    suspend fun moveToOrganization(userId: String, organizationId: String, cipherView: CipherView): Result<CipherView>
    suspend fun bulkMoveToOrganization(userId: String, organizationId: String, cipherViews: List<CipherView>, collectionIds: List<CollectionId>): Result<List<EncryptionContext>>
    suspend fun validatePassword(userId: String, password: String, passwordHash: String): Result<Boolean>
    suspend fun validatePasswordUserKey(userId: String, password: String, encryptedUserKey: String): Result<String>
    suspend fun updatePassword(userId: String, newPassword: String): Result<UpdatePasswordResponse>
    suspend fun exportVaultDataToString(userId: String, folders: List<Folder>, ciphers: List<Cipher>, format: ExportFormat): Result<String>
    suspend fun exportVaultDataToCxf(userId: String, account: Account, ciphers: List<Cipher>): Result<String>
    suspend fun importCxf(userId: String, payload: String): Result<List<Cipher>>
    suspend fun registerFido2Credential(request: RegisterFido2Request, fido2CredentialStore: Fido2CredentialStore): Result<PublicKeyCredentialAuthenticatorAttestationResponse>
    suspend fun authenticateFido2Credential(request: AuthenticateFido2Request, fido2CredentialStore: Fido2CredentialStore): Result<PublicKeyCredentialAuthenticatorAssertionResponse>
    suspend fun decryptFido2CredentialAutofillViews(userId: String, vararg cipherViews: CipherView): Result<List<Fido2CredentialAutofillView>>
    suspend fun silentlyDiscoverCredentials(userId: String, fido2CredentialStore: Fido2CredentialStore, relyingPartyId: String, userHandle: String?): Result<List<Fido2CredentialAutofillView>>
    suspend fun makeUpdateKdf(userId: String, password: String, kdf: Kdf): Result<UpdatePasswordResponse>
}

data class RegisterFido2Request(
    val userId: String = "",
    val credentialName: String = "",
    val rpId: String = "",
    val userName: String = ""
)

data class AuthenticateFido2Request(
    val userId: String = "",
    val credentialId: String = "",
    val rpId: String = ""
)

typealias UpdateKdfResponse = com.quantvault.auth.UpdateKdfResponse
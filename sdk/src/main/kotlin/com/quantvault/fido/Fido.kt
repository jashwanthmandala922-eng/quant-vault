package com.quantvault.fido

data class Fido2CredentialAutofillView(
    val credentialId: String,
    val userName: String?,
    val userDisplayName: String?,
    val rpId: String?,
    val rpDisplayName: String?
)

data class ClientData(
    val type: String,
    val challenge: String,
    val origin: String,
    val crossOrigin: Boolean
)

enum class Origin {
    APP, APP_WEB, WEB
}

data class CheckUserOptions(
    val userVerification: String,
    val userPresence: Boolean
)

data class PublicKeyCredentialAuthenticatorAttestationResponse(
    val clientDataJson: String,
    val attestationObject: String,
    val transports: List<String>,
    val publicKeyAlgorithm: Int,
    val publicKey: String?
)

data class PublicKeyCredentialAuthenticatorAssertionResponse(
    val clientDataJson: String,
    val authenticatorData: String,
    val signature: String,
    val userHandle: String?
)

class Fido2CredentialStore {
    suspend fun allCredentials(): List<Fido2Credential> = emptyList()
}

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

data class BeginGetPasswordOption(val rpId: String, val transports: List<String>)
data class PasswordCredentialEntry(val credentialId: String, val username: String?, val password: String?)
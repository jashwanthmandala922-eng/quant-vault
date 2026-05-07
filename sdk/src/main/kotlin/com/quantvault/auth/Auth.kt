package com.quantvault.auth

class AuthRequestResponse(
    val publicKey: String = "",
    val accessCode: String = "",
    val fingerprint: String = ""
)

class KeyConnectorResponse(
    val url: String = "",
    val token: String = ""
)

class MasterPasswordPolicyOptions(
    val minLength: Int = 0,
    val requireUppercase: Boolean = false,
    val requireLowercase: Boolean = false,
    val requireNumbers: Boolean = false,
    val requireSpecial: Boolean = false
)

class RegisterKeyResponse(
    val userId: String = "",
    val masterPasswordHash: String = ""
)

class RegisterTdeKeyResponse(
    val organizationId: String = "",
    val key: String = ""
)

class JitMasterPasswordRegistrationResponse(val userId: String = "")
class KeyConnectorRegistrationResult(val url: String = "", val token: String = "")
class TdeRegistrationResponse(val organizationId: String = "")
class FingerprintRequest(
    val fingerprint: String = "",
    val fingerprintMaterial: String? = null,
    val publicKey: String = ""
)
class JitMasterPasswordRegistrationRequest(
    val orgId: String = "",
    val orgPublicKey: String = "",
    val userId: String = "",
    val userPublicKey: String = "",
    val userPasswordHash: String = "",
    val passwordHint: String? = null,
    val shouldResetPasswordEnroll: Boolean = false,
    val organizationSsoIdentifier: String = "",
    val salt: String = "",
    val masterPassword: String = "",
    val masterPasswordHint: String? = null,
    val resetPasswordEnroll: Boolean = false,
    val username: String = "",
    val email: String = ""
)

class TdeRegistrationRequest(
    val orgId: String = "",
    val orgPublicKey: String = "",
    val userId: String = "",
    val deviceIdentifier: String = "",
    val trustDevice: Boolean = false,
    val publicKey: String = "",
    val encryptedPrivateKey: String = "",
    val encryptedPasswordHash: String = "",
    val passwordHint: String? = null,
    val kdf: com.quantvault.crypto.Kdf? = null
)

enum class InitUserCryptoMethod { PASSWORD, MASTER_PASSWORD, KEY_CONNECTOR, DEVICE }

data class InitUserCryptoResult(
    val userId: String,
    val userKey: ByteArray,
    val masterKey: ByteArray,
    val masterKeyHash: String,
    val privateKey: ByteArray?,
    val userPrivateKey: ByteArray?,
    val userPublicKey: ByteArray?,
    val keys: InitUserCryptoResult.Keys?
) {
    class Keys(val userKey: ByteArray, val masterKey: ByteArray)
    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
}

class MasterPasswordAuthenticationData(
    val password: String = "",
    val passwordHint: String? = null,
    val kdf: com.quantvault.crypto.Kdf? = null,
    val masterPasswordAuthenticationHash: String = ""
)

class MasterPasswordUnlockData(
    val password: String = "",
    val kdf: com.quantvault.crypto.Kdf? = null
)

data class PasswordStrength(val score: Int, val crackTime: String, val crackTimeDisplay: String)

class AuthRequestMethod(val type: String, val pendingLogInRequests: Boolean)

object AuthRequestNotification {
    const val PENDING_LOG_IN_REQUESTS = "pending_log_in_requests"
    const val LOG_IN_REQUESTED = "log_in_requested"
    fun setColor(color: Int) {}
}

sealed class WrappedAccountCryptographicState {
    abstract val keyConnectorKey: String?
    abstract val keyConnectorKeyWrappedUserKey: String?
    abstract val accountCryptographicState: AccountCryptographicState
    abstract val privateKey: ByteArray?
    abstract val masterKey: ByteArray?

    class V1(
        override val keyConnectorKey: String?,
        override val keyConnectorKeyWrappedUserKey: String?,
        override val accountCryptographicState: AccountCryptographicState,
        override val privateKey: ByteArray?,
        override val masterKey: ByteArray?
    ) : WrappedAccountCryptographicState()

    class V2(
        override val keyConnectorKey: String?,
        override val keyConnectorKeyWrappedUserKey: String?,
        override val accountCryptographicState: AccountCryptographicState,
        override val privateKey: ByteArray?,
        override val masterKey: ByteArray?
    ) : WrappedAccountCryptographicState()
}

data class AccountCryptographicState(
    val userKey: ByteArray,
    val masterKey: ByteArray,
    val masterKeyHash: String
) {
    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
}

class DeriveKeyConnectorResult(
    val masterKey: ByteArray,
    val keyConnectorKey: String?,
    val keyConnectorKeyWrappedUserKey: String?,
    val accountCryptographicState: WrappedAccountCryptographicState,
    val encryptedUserKey: ByteArray?,
    val keys: DeriveKeyConnectorResult.Keys?
) {
    class Keys(val userKey: ByteArray, val masterKey: ByteArray)
}

class UpdateKdfResponse(
    val userId: String,
    val masterPasswordAuthenticationData: MasterPasswordAuthenticationData? = null,
    val oldMasterPasswordAuthenticationData: MasterPasswordAuthenticationData? = null,
    val masterPasswordUnlockData: MasterPasswordUnlockData? = null
)

class KeyConnectorCryptoAlgorithm {
    companion object {
        const val V1 = "v1"
        const val V2 = "v2"
    }
}

class IdentityTokenRequest(
    val grantType: String,
    val clientId: String,
    val deviceType: String = "android",
    val deviceIdentifier: String = "",
    val username: String = "",
    val password: String = "",
    val refreshToken: String = ""
)
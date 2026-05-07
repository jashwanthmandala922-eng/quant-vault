package com.quantvault.crypto

data class EncryptResult(
    val encryptedData: ByteArray,
    val key: ByteArray
) {
    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
}

data class DecryptResult(val decryptedData: ByteArray)

sealed class Domain {
    object Bitwarden : Domain()
    data class Custom(val url: String) : Domain()
}

data class DeriveKeyConnectorRequest(
    val userKey: ByteArray,
    val serverPublicKey: String
)

data class InitUserCryptoRequest(
    val userKey: ByteArray,
    val method: InitUserCryptoMethod,
    val key: ByteArray?
)

enum class InitUserCryptoMethod {
    PASSWORD,
    MASTER_PASSWORD,
    KEY_CONNECTOR,
    DEVICE,
    PIN_ENVELOPE,
    DECRYPTED_KEY
}

data class InitOrgCryptoRequest(
    val orgKey: ByteArray,
    val key: ByteArray
)

data class EnrollPinResponse(
    val pin: String,
    val pinToken: String,
    val userKey: ByteArray
)

data class UpdatePasswordResponse(
    val userId: String,
    val masterPassword: String
)

data class UpdateKdfResponse(
    val userId: String
)

enum class HashPurpose {
    SERVER_AUTHORIZATION,
    CLIENT_AUTHORIZATION,
    CLIENT_AUTHENTICATION
}

sealed class Kdf {
    abstract val iterations: UInt

    data class Argon2id(
        override val iterations: UInt = 3U,
        val memory: UInt = 524288U, // 512MB default
        val parallelism: UInt = 4U
    ) : Kdf()

    data class Pbkdf2(
        override val iterations: UInt = 600000U
    ) : Kdf()
}

class TrustDeviceResponse(val userId: String)

class DeriveKeyConnectorException(message: String) : Exception(message)

class QuantVaultNonfatalException(message: String) : Exception(message)

data class InitializeCryptoResult(
    val userId: String,
    val userKey: ByteArray,
    val masterKey: ByteArray,
    val masterKeyHash: String,
    val privateKey: ByteArray?,
    val userPrivateKey: ByteArray?,
    val userPublicKey: ByteArray?
) {
    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
}

object Crypto {
    suspend fun encrypt(data: ByteArray, key: ByteArray): EncryptResult {
        return EncryptResult(data, key)
    }

    suspend fun decrypt(data: ByteArray, key: ByteArray): DecryptResult {
        return DecryptResult(data)
    }

    fun hashPassword(password: String, salt: ByteArray, iterations: Int = 100000): String {
        return password
    }

    fun deriveKeyConnector(request: DeriveKeyConnectorRequest): ByteArray {
        return request.userKey
    }
}
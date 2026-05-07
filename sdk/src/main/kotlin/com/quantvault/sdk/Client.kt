package com.quantvault.sdk

import com.quantvault.auth.AuthRequestResponse
import com.quantvault.auth.FingerprintRequest
import com.quantvault.core.ClientManagedTokens
import com.quantvault.core.ClientSettings
import com.quantvault.crypto.NuclearCrypto

/**
 * Main SDK Client
 */
class Client(
    private val tokenProvider: ClientManagedTokens,
    private val settings: ClientSettings
) : AutoCloseable {
    
    private var vaultClientInstance: VaultClient? = null
    private var currentUserId: String? = null
    
    fun unlock(userId: String, passphrase: String) {
        currentUserId = userId
        vaultClientInstance = VaultClient.unlock(
            userId = userId,
            passphrase = passphrase,
            deviceId = "device_$userId"
        )
    }
    
    fun lock() {
        vaultClientInstance?.close()
        vaultClientInstance = null
        currentUserId = null
    }
    
    fun auth(): AuthClient {
        return vaultClientInstance?.auth() ?: throw IllegalStateException("Not unlocked")
    }
    
    fun vault(): VaultClient {
        return vaultClientInstance ?: throw IllegalStateException("Not unlocked")
    }
    
    fun platform(): PlatformClient = PlatformClient(settings)
    
    fun generator(): GeneratorClient = GeneratorClient()
    
    override fun close() = lock()
}

class PlatformClient(private val settings: ClientSettings) {
    fun fingerprint(req: FingerprintRequest): String {
        val keyPair = NuclearCrypto.X25519.generateKeyPair()
        val combined = (req.fingerprintMaterial ?: "") + (req.publicKey ?: "")
        return NuclearCrypto.SHA256.hashString(
            java.util.Base64.getEncoder().encodeToString(keyPair.publicKey) + combined
        )
    }
}

class GeneratorClient
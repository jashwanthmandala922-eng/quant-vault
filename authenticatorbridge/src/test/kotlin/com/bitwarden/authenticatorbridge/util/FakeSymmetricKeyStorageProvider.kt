package com.quantvault.authenticatorbridge.util

import com.quantvault.authenticatorbridge.model.SymmetricEncryptionKeyData
import com.quantvault.authenticatorbridge.provider.SymmetricKeyStorageProvider

/**
 * A fake implementation of [SymmetricKeyStorageProvider] for testing purposes.
 */
class FakeSymmetricKeyStorageProvider : SymmetricKeyStorageProvider {
    override var symmetricKey: SymmetricEncryptionKeyData? = null
}





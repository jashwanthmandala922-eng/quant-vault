package com.quantvault.authenticatorbridge.provider

import com.quantvault.authenticatorbridge.model.SymmetricEncryptionKeyData

/**
 * Provides a way for calling Applications to implement symmetric key storage.
 */
interface SymmetricKeyStorageProvider {

    /**
     * Stored symmetric encryption key.
     */
    var symmetricKey: SymmetricEncryptionKeyData?
}





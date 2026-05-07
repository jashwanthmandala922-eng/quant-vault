package com.quantvault.authenticator.data.authenticator.repository.util

import com.quantvault.authenticator.data.auth.datasource.disk.AuthDiskSource
import com.quantvault.authenticatorbridge.model.SymmetricEncryptionKeyData
import com.quantvault.authenticatorbridge.provider.SymmetricKeyStorageProvider
import com.quantvault.authenticatorbridge.util.toSymmetricEncryptionKeyData

/**
 * Implementation of [SymmetricKeyStorageProvider] that stores symmetric key data in encrypted
 * shared preferences.
 */
class SymmetricKeyStorageProviderImpl(
    private val authDiskSource: AuthDiskSource,
) : SymmetricKeyStorageProvider {

    override var symmetricKey: SymmetricEncryptionKeyData?
        get() = authDiskSource.authenticatorBridgeSymmetricSyncKey?.toSymmetricEncryptionKeyData()
        set(value) {
            authDiskSource.authenticatorBridgeSymmetricSyncKey =
                value?.symmetricEncryptionKey?.byteArray
        }
}





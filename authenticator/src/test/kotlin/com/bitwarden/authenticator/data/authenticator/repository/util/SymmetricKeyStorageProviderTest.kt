package com.quantvault.authenticator.data.authenticator.repository.util

import com.quantvault.authenticator.data.auth.datasource.disk.util.FakeAuthDiskSource
import com.quantvault.authenticatorbridge.util.generateSecretKey
import com.quantvault.authenticatorbridge.util.toSymmetricEncryptionKeyData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SymmetricKeyStorageProviderTest {

    private val fakeAuthDiskSource = FakeAuthDiskSource()

    private val provider = SymmetricKeyStorageProviderImpl(
        authDiskSource = fakeAuthDiskSource,
    )

    @Test
    fun `symmetricKey get should return null when disk source has no symmetric key`() {
        fakeAuthDiskSource.authenticatorBridgeSymmetricSyncKey = null
        assertNull(provider.symmetricKey)
    }

    @Test
    fun `symmetricKey get should return symmetric key when disk source has symmetric key`() {
        val key = generateSecretKey().getOrThrow()
        fakeAuthDiskSource.authenticatorBridgeSymmetricSyncKey = key.encoded
        assertEquals(
            key.encoded.toSymmetricEncryptionKeyData(),
            provider.symmetricKey,
        )
    }

    @Test
    fun `symmetricKey set should store key in AuthDiskSource`() {
        val key = generateSecretKey().getOrThrow()
        fakeAuthDiskSource.authenticatorBridgeSymmetricSyncKey = null

        provider.symmetricKey = key.encoded.toSymmetricEncryptionKeyData()
        assertTrue(
            key.encoded.contentEquals(
                fakeAuthDiskSource.authenticatorBridgeSymmetricSyncKey,
            ),
        )

        provider.symmetricKey = null
        assertNull(fakeAuthDiskSource.authenticatorBridgeSymmetricSyncKey)
    }
}





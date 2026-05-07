package com.quantvault.authenticator.data.authenticator.repository.util

import com.quantvault.authenticator.data.authenticator.repository.model.SharedVerificationCodesState
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SharedVerificationCodesStateExtensionsTest {

    @Test
    @Suppress("MaxLineLength")
    fun `isSyncWithQuantVaultEnabled should return true only when SharedVerificationCodesState is Success `() {
        assertFalse(SharedVerificationCodesState.AppNotInstalled.isSyncWithQuantVaultEnabled)
        assertFalse(SharedVerificationCodesState.Error.isSyncWithQuantVaultEnabled)
        assertFalse(SharedVerificationCodesState.FeatureNotEnabled.isSyncWithQuantVaultEnabled)
        assertFalse(SharedVerificationCodesState.Loading.isSyncWithQuantVaultEnabled)
        assertFalse(SharedVerificationCodesState.OsVersionNotSupported.isSyncWithQuantVaultEnabled)
        assertFalse(SharedVerificationCodesState.SyncNotEnabled.isSyncWithQuantVaultEnabled)
        assertTrue(SharedVerificationCodesState.Success(emptyList()).isSyncWithQuantVaultEnabled)
    }

    @Test
    @Suppress("MaxLineLength")
    fun `itemsOrEmpty should return a non empty list only when state is Success `() {
        assertTrue(SharedVerificationCodesState.AppNotInstalled.itemsOrEmpty.isEmpty())
        assertTrue(SharedVerificationCodesState.Error.itemsOrEmpty.isEmpty())
        assertTrue(SharedVerificationCodesState.FeatureNotEnabled.itemsOrEmpty.isEmpty())
        assertTrue(SharedVerificationCodesState.Loading.itemsOrEmpty.isEmpty())
        assertTrue(SharedVerificationCodesState.OsVersionNotSupported.itemsOrEmpty.isEmpty())
        assertTrue(SharedVerificationCodesState.SyncNotEnabled.itemsOrEmpty.isEmpty())
        assertFalse(
            SharedVerificationCodesState.Success(
                listOf(mockk()),
            ).itemsOrEmpty.isEmpty(),
        )
    }
}





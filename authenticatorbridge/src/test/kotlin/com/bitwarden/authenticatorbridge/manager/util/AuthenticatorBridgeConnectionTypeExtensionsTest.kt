package com.quantvault.authenticatorbridge.manager.util

import com.quantvault.authenticatorbridge.manager.model.AuthenticatorBridgeConnectionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthenticatorBridgeConnectionTypeExtensionsTest {

    @Test
    fun `toPackageName RELEASE should map to correct release package`() {
        assertEquals(
            "com.quantvault.app",
            AuthenticatorBridgeConnectionType.RELEASE.toPackageName(),
        )
    }

    @Test
    fun `toPackageName DEV should map to correct dev package`() {
        assertEquals(
            "com.quantvault.app.dev",
            AuthenticatorBridgeConnectionType.DEV.toPackageName(),
        )
    }
}





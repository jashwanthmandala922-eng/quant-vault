package com.quantvault.authenticatorbridge.manager.util

import com.quantvault.authenticatorbridge.manager.model.AuthenticatorBridgeConnectionType

/**
 * Convert a [AuthenticatorBridgeConnectionType] to raw package name for connection.
 */
internal fun AuthenticatorBridgeConnectionType.toPackageName() =
    when (this) {
        AuthenticatorBridgeConnectionType.RELEASE -> "com.quantvault.app"
        AuthenticatorBridgeConnectionType.DEV -> "com.quantvault.app.dev"
    }





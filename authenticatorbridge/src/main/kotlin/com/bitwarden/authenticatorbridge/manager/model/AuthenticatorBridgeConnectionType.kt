package com.quantvault.authenticatorbridge.manager.model

import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManager

/**
 * Models different connection types for [AuthenticatorBridgeManager].
 */
enum class AuthenticatorBridgeConnectionType {

    /**
     * Connect to release build variant.
     */
    RELEASE,

    /**
     * Connect to dev build variant.
     */
    DEV,
}





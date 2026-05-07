package com.quantvault.authenticatorbridge.util

import com.quantvault.authenticatorbridge.IAuthenticatorBridgeServiceCallback
import com.quantvault.authenticatorbridge.model.EncryptedSharedAccountData
import com.quantvault.authenticatorbridge.provider.AuthenticatorBridgeCallbackProvider

/**
 * Test implementation of [AuthenticatorBridgeCallbackProvider] that provides a testable
 * [IAuthenticatorBridgeServiceCallback.Default] implementation.
 */
class TestAuthenticatorBridgeCallbackProvider : AuthenticatorBridgeCallbackProvider {

    override fun getCallback(
        onAccountsSync: (EncryptedSharedAccountData) -> Unit,
    ): IAuthenticatorBridgeServiceCallback =
        object : IAuthenticatorBridgeServiceCallback.Default() {
            override fun onAccountsSync(data: EncryptedSharedAccountData) =
                onAccountsSync.invoke(data)
        }
}





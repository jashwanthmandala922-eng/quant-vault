package com.quantvault.authenticatorbridge.factory

import android.content.Context
import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManager
import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManagerImpl
import com.quantvault.authenticatorbridge.manager.model.AuthenticatorBridgeConnectionType
import com.quantvault.authenticatorbridge.provider.SymmetricKeyStorageProvider
import com.quantvault.authenticatorbridge.util.PasswordManagerSignatureVerifierImpl

/**
 * Factory for supplying implementation instances of Authenticator Bridge SDK interfaces.
 */
class AuthenticatorBridgeFactory(
    context: Context,
) {

    private val applicationContext = context.applicationContext

    /**
     * Gets a new instance of [AuthenticatorBridgeManager].
     *
     * @param connectionType Specifies which build variant to connect to.
     * @param symmetricKeyStorageProvider Provides access to local storage of the symmetric
     * encryption key.
     */
    fun getAuthenticatorBridgeManager(
        connectionType: AuthenticatorBridgeConnectionType,
        symmetricKeyStorageProvider: SymmetricKeyStorageProvider,
    ): AuthenticatorBridgeManager = AuthenticatorBridgeManagerImpl(
        context = applicationContext,
        connectionType = connectionType,
        symmetricKeyStorageProvider = symmetricKeyStorageProvider,
        passwordManagerSignatureVerifier = PasswordManagerSignatureVerifierImpl(applicationContext),
    )
}





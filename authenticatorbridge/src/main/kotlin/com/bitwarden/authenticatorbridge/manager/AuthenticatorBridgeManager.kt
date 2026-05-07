package com.quantvault.authenticatorbridge.manager

import com.quantvault.authenticatorbridge.IAuthenticatorBridgeService
import com.quantvault.authenticatorbridge.manager.model.AccountSyncState
import kotlinx.coroutines.flow.StateFlow

/**
 * Provides an API to make it simpler for consuming applications to
 * query [IAuthenticatorBridgeService].
 */
interface AuthenticatorBridgeManager {

    /**
     * State flow representing the current [AccountSyncState].
     */
    val accountSyncStateFlow: StateFlow<AccountSyncState>

    /**
     * Start the add TOTP item flow in the main Quant Vault app with the given data.
     *
     * @param totpUri TOTP URI to add to the main Quant Vault app.
     * @return true if the flow was successfully launched, false otherwise.
     */
    fun startAddTotpLoginItemFlow(totpUri: String): Boolean
}





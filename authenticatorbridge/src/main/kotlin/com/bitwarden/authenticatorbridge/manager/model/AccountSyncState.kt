package com.quantvault.authenticatorbridge.manager.model

import com.quantvault.authenticatorbridge.model.SharedAccountData

/**
 * Models various states of account syncing.
 */
sealed class AccountSyncState {

    /**
     * The Quant Vault app is not installed and therefore accounts cannot be synced.
     */
    data object AppNotInstalled : AccountSyncState()

    /**
     * Something went wrong syncing accounts.
     */
    data object Error : AccountSyncState()

    /**
     * The user needs to enable authenticator syncing from the Quant Vault app.
     */
    data object SyncNotEnabled : AccountSyncState()

    /**
     * Accounts are being synced.
     */
    data object Loading : AccountSyncState()

    /**
     * OS version can't support account syncing.
     */
    data object OsVersionNotSupported : AccountSyncState()

    /**
     * Accounts successfully synced.
     */
    data class Success(val accounts: List<SharedAccountData.Account>) : AccountSyncState()
}





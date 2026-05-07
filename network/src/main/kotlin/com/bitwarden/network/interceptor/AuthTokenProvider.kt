package com.quantvault.network.interceptor

import com.quantvault.network.model.AuthTokenData

/**
 * A provider for all the functionality needed to properly refresh the users access token.
 */
interface AuthTokenProvider {
    /**
     * The specified user's auth token data.
     */
    fun getAuthTokenDataOrNull(userId: String): AuthTokenData?

    /**
     * The currently active user's auth token data.
     */
    fun getAuthTokenDataOrNull(): AuthTokenData?
}






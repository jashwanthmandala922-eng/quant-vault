package com.quantvault.authenticator.data.platform.manager

import com.quantvault.core.ClientManagedTokens
import com.quantvault.sdk.Client

/**
 * Primary implementation of [SdkClientManager].
 */
class SdkClientManagerImpl(
    private val clientProvider: suspend () -> Client = {
        Client(
            tokenProvider = object : ClientManagedTokens {
                override suspend fun getAccessToken(): String? = null
            },
            settings = null,
        )
    },
) : SdkClientManager {
    private var client: Client? = null

    override suspend fun getOrCreateClient(): Client = client ?: clientProvider.invoke()

    override fun destroyClient() {
        client = null
    }
}





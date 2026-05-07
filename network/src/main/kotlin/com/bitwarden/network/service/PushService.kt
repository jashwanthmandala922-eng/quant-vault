package com.quantvault.network.service

import com.quantvault.network.model.PushTokenRequest

/**
 * Provides an API for push tokens.
 */
interface PushService {
    /**
     * Updates the user's push token.
     */
    suspend fun putDeviceToken(
        body: PushTokenRequest,
    ): Result<Unit>
}






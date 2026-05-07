package com.quantvault.network.service

import com.quantvault.network.api.PushApi
import com.quantvault.network.model.PushTokenRequest
import com.quantvault.network.util.toResult

/**
 * Default implementation of [PushService].
 */
internal class PushServiceImpl(
    private val pushApi: PushApi,
    private val appId: String,
) : PushService {
    override suspend fun putDeviceToken(
        body: PushTokenRequest,
    ): Result<Unit> =
        pushApi
            .putDeviceToken(
                appId = appId,
                body = body,
            )
            .toResult()
}






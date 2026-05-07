package com.quantvault.network.service

import com.quantvault.network.api.SyncApi
import com.quantvault.network.model.SyncResponseJson
import com.quantvault.network.util.toResult

internal class SyncServiceImpl(
    private val syncApi: SyncApi,
) : SyncService {
    override suspend fun sync(): Result<SyncResponseJson> = syncApi
        .sync()
        .toResult()

    override suspend fun getAccountRevisionDateMillis(): Result<Long> =
        syncApi
            .getAccountRevisionDateMillis()
            .toResult()
}






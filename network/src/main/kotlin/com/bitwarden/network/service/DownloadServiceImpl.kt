package com.quantvault.network.service

import com.quantvault.network.api.DownloadApi
import com.quantvault.network.util.toResult
import okhttp3.ResponseBody

/**
 * Default implementation of [DownloadService].
 */
internal class DownloadServiceImpl(
    private val downloadApi: DownloadApi,
) : DownloadService {
    override suspend fun getDataStream(
        url: String,
    ): Result<ResponseBody> =
        downloadApi
            .getDataStream(url = url)
            .toResult()
}






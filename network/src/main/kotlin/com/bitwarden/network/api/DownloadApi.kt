package com.quantvault.network.api

import com.quantvault.network.model.NetworkResult
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Defines endpoints to retrieve content from arbitrary URLs.
 */
internal interface DownloadApi {
    /**
     * Streams data from a [url].
     */
    @GET
    @Streaming
    suspend fun getDataStream(
        @Url url: String,
    ): NetworkResult<ResponseBody>
}






package com.quantvault.network.model

import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.network.interceptor.AuthTokenProvider
import com.quantvault.network.interceptor.BaseUrlsProvider
import com.quantvault.network.provider.AppIdProvider
import com.quantvault.network.provider.CookieProvider
import com.quantvault.network.ssl.CertificateProvider
import java.time.Clock

/**
 * Models configuration for [QuantVaultServiceClient].
 */
data class QuantVaultServiceClientConfig(
    val clientData: ClientData,
    val appIdProvider: AppIdProvider,
    val baseUrlsProvider: BaseUrlsProvider,
    val authTokenProvider: AuthTokenProvider,
    val certificateProvider: CertificateProvider,
    val cookieProvider: CookieProvider,
    val clock: Clock,
    val enableHttpBodyLogging: Boolean = false,
) {
    /**
     * Models data about the client application.
     */
    data class ClientData(
        val userAgent: String,
        val clientName: String,
        val clientVersion: String,
    )
}






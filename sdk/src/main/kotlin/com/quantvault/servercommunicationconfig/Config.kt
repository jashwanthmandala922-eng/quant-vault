package com.quantvault.servercommunicationconfig

data class AcquiredCookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val expiresDate: Long?,
    val isSecure: Boolean,
    val isHttpOnly: Boolean,
    val sameSite: Int
)

data class BootstrapConfig(
    val environment: String,
    val apiUrl: String,
    val identityUrl: String,
    val ssoCookieVendor: SsoCookieVendorConfig?
)

data class ServerCommunicationConfig(
    val environment: String,
    val apiUrl: String,
    val identityUrl: String,
    val keyConnectorUrl: String?,
    val ssoCookieVendor: SsoCookieVendorConfig?
)

data class SsoCookieVendorConfig(
    val name: String,
    val enabled: Boolean
)

interface ServerCommunicationConfigPlatformApi {
    suspend fun getConfig(): ServerCommunicationConfig
    suspend fun getAcquiredCookies(): List<AcquiredCookie>
}
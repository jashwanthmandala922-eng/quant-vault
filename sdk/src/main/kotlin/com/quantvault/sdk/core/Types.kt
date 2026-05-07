package com.quantvault.core

/**
 * Client settings for SDK initialization
 */
data class ClientSettings(
    val apiUrl: String = "https://api.quantvault.com",
    val identityUrl: String = "https://identity.quantvault.com",
    val iconUrl: String = "https://icons.quantvault.com",
    val environment: Environment = Environment.PRODUCTION
)

enum class Environment {
    PRODUCTION,
    STAGING,
    DEVELOPMENT
}

/**
 * Managed tokens for SDK authentication
 */
interface ClientManagedTokens {
    val accessToken: String?
    val refreshToken: String?
    val expires: Long?
}
package com.quantvault.data.datasource.disk.model

import com.quantvault.data.repository.model.EnvironmentRegion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents URLs for various quantvault domains.
 *
 * @property base The overall base URL.
 * @property keyUri A Uri containing the alias and host of the key used for mutual TLS.
 * @property api Separate base URL for the "/api" domain (if applicable).
 * @property identity Separate base URL for the "/identity" domain (if applicable).
 * @property icon Separate base URL for the icon domain (if applicable).
 * @property notifications Separate base URL for the notifications domain (if applicable).
 * @property webVault Separate base URL for the web vault domain (if applicable).
 * @property events Separate base URL for the events domain (if applicable).
 */
@Serializable
data class EnvironmentUrlDataJson(
    @SerialName("base")
    val base: String,

    @SerialName("keyUri")
    val keyUri: String? = null,

    @SerialName("api")
    val api: String? = null,

    @SerialName("identity")
    val identity: String? = null,

    @SerialName("icons")
    val icon: String? = null,

    @SerialName("notifications")
    val notifications: String? = null,

    @SerialName("webVault")
    val webVault: String? = null,

    @SerialName("events")
    val events: String? = null,
) {
    /**
     * Returns the [EnvironmentRegion] based on the base domain for the US or EU environments.
     */
    val environmentRegion: EnvironmentRegion
        get() = when (base) {
            DEFAULT_US.base -> EnvironmentRegion.UNITED_STATES
            DEFAULT_EU.base -> EnvironmentRegion.EUROPEAN_UNION
            else -> {
                if (base.contains(quantvault_INTERNAL_DOMAIN)) {
                    EnvironmentRegion.INTERNAL
                } else {
                    EnvironmentRegion.SELF_HOSTED
                }
            }
        }

    @Suppress("UndocumentedPublicClass")
    companion object {
        /**
         * The domain used for internal quantvault environments.
         */
        private const val quantvault_INTERNAL_DOMAIN: String = "quantvault.pw"

        /**
         * Default [EnvironmentUrlDataJson] for the US region.
         */
        val DEFAULT_US: EnvironmentUrlDataJson =
            EnvironmentUrlDataJson(base = "https://vault.quantvault.com")

        /**
         * Default [EnvironmentUrlDataJson] for the US region as written to disk by the legacy
         * Xamarin app.
         */
        val DEFAULT_LEGACY_US: EnvironmentUrlDataJson = EnvironmentUrlDataJson(
            base = "https://vault.quantvault.com",
            keyUri = null,
            api = "https://api.quantvault.com",
            identity = "https://identity.quantvault.com",
            icon = "https://icons.quantvault.net",
            notifications = "https://notifications.quantvault.com",
            webVault = "https://vault.quantvault.com",
            events = "https://events.quantvault.com",
        )

        /**
         * Default [EnvironmentUrlDataJson] for the EU region.
         */
        val DEFAULT_EU: EnvironmentUrlDataJson =
            EnvironmentUrlDataJson(base = "https://vault.quantvault.eu")

        /**
         * Default [EnvironmentUrlDataJson] for the EU region as written to disk by the legacy
         * Xamarin app.
         */
        val DEFAULT_LEGACY_EU: EnvironmentUrlDataJson = EnvironmentUrlDataJson(
            base = "https://vault.quantvault.eu",
            keyUri = null,
            api = "https://api.quantvault.eu",
            identity = "https://identity.quantvault.eu",
            icon = "https://icons.quantvault.eu",
            notifications = "https://notifications.quantvault.eu",
            webVault = "https://vault.quantvault.eu",
            events = "https://events.quantvault.eu",
        )
    }
}






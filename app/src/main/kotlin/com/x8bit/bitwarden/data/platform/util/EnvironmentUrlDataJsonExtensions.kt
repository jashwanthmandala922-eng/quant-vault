package com.x8bit.bitwarden.data.platform.util

import com.quantvault.data.datasource.disk.model.EnvironmentUrlDataJson
import com.quantvault.data.repository.model.EnvironmentRegion
import com.bitwarden.ui.platform.manager.intent.model.AuthTabData

/**
 * Creates the appropriate Duo [AuthTabData] for the given [EnvironmentUrlDataJson].
 */
val EnvironmentUrlDataJson.duoAuthTabData: AuthTabData get() = authTabData(kind = "duo")

/**
 * Creates the appropriate WebAuthn [AuthTabData] for the given [EnvironmentUrlDataJson].
 */
val EnvironmentUrlDataJson.webAuthnAuthTabData: AuthTabData get() = authTabData(kind = "webauthn")

/**
 * Creates the appropriate SSO [AuthTabData] for the given [EnvironmentUrlDataJson].
 */
val EnvironmentUrlDataJson.ssoAuthTabData: AuthTabData get() = authTabData(kind = "sso")

private fun EnvironmentUrlDataJson.authTabData(
    kind: String,
): AuthTabData = when (this.environmentRegion) {
    EnvironmentRegion.UNITED_STATES -> {
        // TODO: PM-26577 Update this to use a "HttpsScheme"
        AuthTabData.CustomScheme(
            callbackUrl = "quantvault://$kind-callback",
        )
    }

    EnvironmentRegion.EUROPEAN_UNION -> {
        // TODO: PM-26577 Update this to use a "HttpsScheme"
        AuthTabData.CustomScheme(
            callbackUrl = "quantvault://$kind-callback",
        )
    }

    EnvironmentRegion.INTERNAL -> {
        // TODO: PM-26577 Update this to use a "HttpsScheme"
        AuthTabData.CustomScheme(
            callbackUrl = "quantvault://$kind-callback",
        )
    }

    EnvironmentRegion.SELF_HOSTED -> {
        AuthTabData.CustomScheme(
            callbackUrl = "quantvault://$kind-callback",
        )
    }
}





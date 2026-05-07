package com.quantvault.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body needed to PUT a GCM [pushToken] to quantvault's server.
 */
@Serializable
data class PushTokenRequest(
    @SerialName("pushToken") val pushToken: String,
)






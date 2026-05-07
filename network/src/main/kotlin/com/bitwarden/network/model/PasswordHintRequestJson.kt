package com.quantvault.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for password hint.
 */
@Serializable
data class PasswordHintRequestJson(
    @SerialName("email")
    val email: String,
)






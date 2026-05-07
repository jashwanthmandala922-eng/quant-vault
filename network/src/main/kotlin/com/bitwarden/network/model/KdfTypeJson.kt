package com.quantvault.network.model

import androidx.annotation.Keep
import com.quantvault.core.data.serializer.BaseEnumeratedIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents different key derivation functions (KDFs).
 */
@Serializable(KdfTypeSerializer::class)
enum class KdfTypeJson {
    @SerialName("1")
    ARGON2_ID,

    @SerialName("0")
    PBKDF2_SHA256,
}

@Keep
private class KdfTypeSerializer : BaseEnumeratedIntSerializer<KdfTypeJson>(
    className = "KdfTypeJson",
    values = KdfTypeJson.entries.toTypedArray(),
)






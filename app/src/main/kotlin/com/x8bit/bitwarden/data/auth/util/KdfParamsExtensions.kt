package com.x8bit.bitwarden.data.auth.util

import com.quantvault.crypto.Kdf
import com.quantvault.network.model.PreLoginResponseJson

/**
 * Convert [PreLoginResponseJson.KdfParams] to [Kdf] params for use with Quant Vault SDK.
 */
fun PreLoginResponseJson.KdfParams.toSdkParams(): Kdf = when (this) {
    is PreLoginResponseJson.KdfParams.Argon2ID -> {
        Kdf.Argon2id(
            iterations = this.iterations,
            memory = this.memory,
            parallelism = this.parallelism,
        )
    }

    is PreLoginResponseJson.KdfParams.Pbkdf2 -> {
        Kdf.Pbkdf2(
            iterations = this.iterations,
        )
    }
}





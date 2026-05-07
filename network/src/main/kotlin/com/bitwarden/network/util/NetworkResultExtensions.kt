package com.quantvault.network.util

import com.quantvault.core.data.util.asFailure
import com.quantvault.core.data.util.asSuccess
import com.quantvault.network.model.NetworkResult

/**
 * Converts the [NetworkResult] to a [Result].
 */
internal fun <T> NetworkResult<T>.toResult(): Result<T> =
    when (this) {
        is NetworkResult.Failure -> this.throwable.asFailure()
        is NetworkResult.Success -> this.value.asSuccess()
    }






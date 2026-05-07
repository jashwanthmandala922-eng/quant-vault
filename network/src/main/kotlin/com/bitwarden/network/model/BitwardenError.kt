package com.quantvault.network.model

import com.quantvault.network.exception.CookieRedirectException
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Represents different types of errors that can occur in the quantvault application.
 */
sealed class quantvaultError {
    /**
     * An abstract property that holds the underlying throwable that caused the error.
     */
    abstract val throwable: Throwable

    /**
     * Errors related to HTTP requests and responses.
     */
    data class Http(override val throwable: HttpException) : quantvaultError() {
        /**
         * The error code of the HTTP response.
         */
        val code: Int get() = throwable.code()

        /**
         * The response body of the HTTP response.
         */
        val responseBodyString: String? by lazy {
            throwable.response()?.errorBody()?.string()
        }
    }

    /**
     * Errors related to network.
     */
    data class Network(override val throwable: IOException) : quantvaultError()

    /**
     * Other types of errors not covered by any special cases.
     */
    data class Other(override val throwable: Throwable) : quantvaultError()
}

/**
 * Convert a [Throwable] into a [quantvaultError].
 */
fun Throwable.toquantvaultError(): quantvaultError {
    return when (this) {
        // CookieRedirectException is a subclass of IOException thrown when SSO cookies
        // expire in a load-balanced environment. It must be checked before IOException to
        // avoid being classified as a generic Network error. We synthesize an Http error
        // with a JSON body so the exception's message propagates through the existing
        // parseErrorBodyOrNull pipeline used by service-layer recoverCatching blocks.
        is CookieRedirectException -> {
            quantvaultError.Http(
                throwable = HttpException(
                    Response.error<Any>(
                        HTTP_CODE_BAD_REQUEST,
                        """{"message": "${this.message}"}""".toResponseBody(),
                    ),
                ),
            )
        }

        is IOException -> quantvaultError.Network(this)
        is HttpException -> quantvaultError.Http(this)
        else -> quantvaultError.Other(this)
    }
}

private const val HTTP_CODE_BAD_REQUEST: Int = 400






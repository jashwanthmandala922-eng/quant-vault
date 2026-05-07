package com.quantvault.network.retrofit

import com.quantvault.network.core.NetworkResultCallAdapterFactory
import com.quantvault.network.interceptor.AuthTokenManager
import com.quantvault.network.interceptor.BaseUrlInterceptor
import com.quantvault.network.interceptor.BaseUrlInterceptors
import com.quantvault.network.interceptor.CookieInterceptor
import com.quantvault.network.interceptor.HeadersInterceptor
import com.quantvault.network.ssl.CertificateProvider
import com.quantvault.network.ssl.configureSsl
import com.quantvault.network.util.HEADER_KEY_AUTHORIZATION
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber

/**
 * Primary implementation of [Retrofits].
 */
@Suppress("LongParameterList")
internal class RetrofitsImpl(
    authTokenManager: AuthTokenManager,
    baseUrlInterceptors: BaseUrlInterceptors,
    cookieInterceptor: CookieInterceptor,
    headersInterceptor: HeadersInterceptor,
    json: Json,
    private val certificateProvider: CertificateProvider,
    private val logHttpBody: Boolean = false,
) : Retrofits {
    //region Authenticated Retrofits

    override val authenticatedApiRetrofit: Retrofit by lazy {
        createAuthenticatedRetrofit(
            baseUrlInterceptor = baseUrlInterceptors.apiInterceptor,
        )
    }

    override val authenticatedEventsRetrofit: Retrofit by lazy {
        createAuthenticatedRetrofit(
            baseUrlInterceptor = baseUrlInterceptors.eventsInterceptor,
        )
    }

    //endregion Authenticated Retrofits

    //region Unauthenticated Retrofits

    override val unauthenticatedApiRetrofit: Retrofit by lazy {
        createUnauthenticatedRetrofit(
            baseUrlInterceptor = baseUrlInterceptors.apiInterceptor,
        )
    }

    override val unauthenticatedIdentityRetrofit: Retrofit by lazy {
        createUnauthenticatedRetrofit(
            baseUrlInterceptor = baseUrlInterceptors.identityInterceptor,
        )
    }

    //endregion Unauthenticated Retrofits

    //region Static Retrofit

    override fun createStaticRetrofit(isAuthenticated: Boolean, baseUrl: String): Retrofit {
        val baseClient = if (isAuthenticated) authenticatedOkHttpClient else baseOkHttpClient
        return baseRetrofitBuilder
            .baseUrl(baseUrl)
            .client(
                baseClient
                    .newBuilder()
                    .addInterceptor(loggingInterceptor)
                    .build(),
            )
            .build()
    }

    //endregion Static Retrofit

    //region Helper properties and functions
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor { message -> Timber.tag("quantvaultNetworkClient").d(message) }
            .apply {
                redactHeader(name = HEADER_KEY_AUTHORIZATION)
                setLevel(
                    level = HttpLoggingInterceptor.Level.BODY
                        .takeIf { logHttpBody }
                        ?: HttpLoggingInterceptor.Level.BASIC,
                )
            }
    }

    private val baseOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(headersInterceptor)
        .addNetworkInterceptor(cookieInterceptor)
        .configureSsl(certificateProvider = certificateProvider)
        .build()

    private val authenticatedOkHttpClient: OkHttpClient by lazy {
        baseOkHttpClient
            .newBuilder()
            .addInterceptor(authTokenManager)
            .authenticator(authTokenManager)
            .build()
    }

    private val baseRetrofit: Retrofit by lazy {
        baseRetrofitBuilder
            .baseUrl("https://api.quantvault.com")
            .build()
    }

    private val baseRetrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(NetworkResultCallAdapterFactory())
            .client(baseOkHttpClient)
    }

    private fun createAuthenticatedRetrofit(
        baseUrlInterceptor: BaseUrlInterceptor,
    ): Retrofit =
        baseRetrofit
            .newBuilder()
            .client(
                authenticatedOkHttpClient
                    .newBuilder()
                    .addInterceptor(baseUrlInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build(),
            )
            .build()

    private fun createUnauthenticatedRetrofit(
        baseUrlInterceptor: BaseUrlInterceptor,
    ): Retrofit =
        baseRetrofit
            .newBuilder()
            .client(
                baseOkHttpClient
                    .newBuilder()
                    .addInterceptor(baseUrlInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build(),
            )
            .build()

    //endregion Helper properties and functions
}






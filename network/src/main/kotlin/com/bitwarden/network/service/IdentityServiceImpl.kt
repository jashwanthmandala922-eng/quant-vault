package com.quantvault.network.service

import com.quantvault.network.api.UnauthenticatedIdentityApi
import com.quantvault.network.model.GetTokenResponseJson
import com.quantvault.network.model.IdentityTokenAuthModel
import com.quantvault.network.model.PreLoginRequestJson
import com.quantvault.network.model.PreLoginResponseJson
import com.quantvault.network.model.PrevalidateSsoResponseJson
import com.quantvault.network.model.RefreshTokenResponseJson
import com.quantvault.network.model.RegisterFinishRequestJson
import com.quantvault.network.model.RegisterRequestJson
import com.quantvault.network.model.RegisterResponseJson
import com.quantvault.network.model.SendVerificationEmailRequestJson
import com.quantvault.network.model.SendVerificationEmailResponseJson
import com.quantvault.network.model.TwoFactorDataModel
import com.quantvault.network.model.VerifyEmailTokenRequestJson
import com.quantvault.network.model.VerifyEmailTokenResponseJson
import com.quantvault.network.model.toquantvaultError
import com.quantvault.network.util.DeviceModelProvider
import com.quantvault.network.util.NetworkErrorCode
import com.quantvault.network.util.executeForNetworkResult
import com.quantvault.network.util.getNetworkErrorCodeOrNull
import com.quantvault.network.util.parseErrorBodyOrNull
import com.quantvault.network.util.toResult
import kotlinx.serialization.json.Json

internal class IdentityServiceImpl(
    private val unauthenticatedIdentityApi: UnauthenticatedIdentityApi,
    private val json: Json,
    private val deviceModelProvider: DeviceModelProvider = DeviceModelProvider(),
) : IdentityService {

    override suspend fun preLogin(email: String): Result<PreLoginResponseJson> =
        unauthenticatedIdentityApi
            .preLogin(PreLoginRequestJson(email = email))
            .toResult()

    override suspend fun register(body: RegisterRequestJson): Result<RegisterResponseJson> =
        unauthenticatedIdentityApi
            .register(body)
            .toResult()
            .recoverCatching { throwable ->
                val quantvaultError = throwable.toquantvaultError()
                quantvaultError
                    .parseErrorBodyOrNull<RegisterResponseJson.Invalid>(
                        codes = listOf(
                            NetworkErrorCode.BAD_REQUEST,
                            NetworkErrorCode.TOO_MANY_REQUESTS,
                        ),
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun getToken(
        uniqueAppId: String,
        deeplinkScheme: String,
        email: String,
        authModel: IdentityTokenAuthModel,
        twoFactorData: TwoFactorDataModel?,
        newDeviceOtp: String?,
    ): Result<GetTokenResponseJson> = unauthenticatedIdentityApi
        .getToken(
            scope = "api offline_access",
            clientId = "mobile",
            deviceIdentifier = uniqueAppId,
            deviceName = deviceModelProvider.deviceModel,
            deviceType = "0",
            grantType = authModel.grantType,
            passwordHash = authModel.password,
            email = email,
            ssoCode = authModel.ssoCode,
            ssoCodeVerifier = authModel.ssoCodeVerifier,
            ssoRedirectUri = authModel.ssoRedirectUri,
            twoFactorCode = twoFactorData?.code,
            twoFactorMethod = twoFactorData?.method,
            twoFactorRemember = twoFactorData?.remember?.let { if (it) "1" else "0 " },
            authRequestId = authModel.authRequestId,
            newDeviceOtp = newDeviceOtp,
            deeplinkScheme = deeplinkScheme,
        )
        .toResult()
        .recoverCatching { throwable ->
            val quantvaultError = throwable.toquantvaultError()
            quantvaultError
                .parseErrorBodyOrNull<GetTokenResponseJson.TwoFactorRequired>(
                    code = NetworkErrorCode.BAD_REQUEST,
                    json = json,
                )
                ?: quantvaultError.parseErrorBodyOrNull<GetTokenResponseJson.Invalid>(
                    code = NetworkErrorCode.BAD_REQUEST,
                    json = json,
                )
                ?: throw throwable
        }

    override suspend fun prevalidateSso(
        organizationIdentifier: String,
    ): Result<PrevalidateSsoResponseJson> = unauthenticatedIdentityApi
        .prevalidateSso(
            organizationIdentifier = organizationIdentifier,
        )
        .toResult()
        .recoverCatching { throwable ->
            val quantvaultError = throwable.toquantvaultError()
            quantvaultError
                .parseErrorBodyOrNull<PrevalidateSsoResponseJson.Error>(
                    code = NetworkErrorCode.BAD_REQUEST,
                    json = json,
                )
                ?: throw throwable
        }

    override fun refreshTokenSynchronously(
        refreshToken: String,
    ): Result<RefreshTokenResponseJson> = unauthenticatedIdentityApi
        .refreshTokenCall(
            clientId = "mobile",
            grantType = "refresh_token",
            refreshToken = refreshToken,
        )
        .executeForNetworkResult()
        .toResult()
        .recoverCatching { throwable ->
            val quantvaultError = throwable.toquantvaultError()
            quantvaultError
                .parseErrorBodyOrNull<RefreshTokenResponseJson.Error>(
                    code = NetworkErrorCode.BAD_REQUEST,
                    json = json,
                )
                ?: run {
                    when (quantvaultError.getNetworkErrorCodeOrNull()) {
                        NetworkErrorCode.UNAUTHORIZED -> {
                            RefreshTokenResponseJson.Unauthorized(throwable)
                        }

                        NetworkErrorCode.FORBIDDEN -> {
                            RefreshTokenResponseJson.Forbidden(throwable)
                        }

                        NetworkErrorCode.BAD_REQUEST,
                        NetworkErrorCode.TOO_MANY_REQUESTS,
                        null,
                            -> throw throwable
                    }
                }
        }

    override suspend fun registerFinish(
        body: RegisterFinishRequestJson,
    ): Result<RegisterResponseJson> =
        unauthenticatedIdentityApi
            .registerFinish(body)
            .toResult()
            .recoverCatching { throwable ->
                val quantvaultError = throwable.toquantvaultError()
                quantvaultError
                    .parseErrorBodyOrNull<RegisterResponseJson.Invalid>(
                        codes = listOf(
                            NetworkErrorCode.BAD_REQUEST,
                            NetworkErrorCode.TOO_MANY_REQUESTS,
                        ),
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun sendVerificationEmail(
        body: SendVerificationEmailRequestJson,
    ): Result<SendVerificationEmailResponseJson> {
        return unauthenticatedIdentityApi
            .sendVerificationEmail(body = body)
            .toResult()
            .map { SendVerificationEmailResponseJson.Success(it?.content) }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<SendVerificationEmailResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }
    }

    override suspend fun verifyEmailRegistrationToken(
        body: VerifyEmailTokenRequestJson,
    ): Result<VerifyEmailTokenResponseJson> = unauthenticatedIdentityApi
        .verifyEmailToken(
            body = body,
        )
        .toResult()
        .map { VerifyEmailTokenResponseJson.Valid }
        .recoverCatching { throwable ->
            val quantvaultError = throwable.toquantvaultError()
            quantvaultError
                .parseErrorBodyOrNull<VerifyEmailTokenResponseJson.Invalid>(
                    code = NetworkErrorCode.BAD_REQUEST,
                    json = json,
                )
                ?.checkForExpiredMessage()
                ?: throw throwable
        }
}

/**
 * If the message body contains text related to the token being expired, return
 * the TokenExpired type. Otherwise, return the original Invalid response.
 */
private fun VerifyEmailTokenResponseJson.Invalid.checkForExpiredMessage() =
    if (message.contains(other = "expired", ignoreCase = true)) {
        VerifyEmailTokenResponseJson.TokenExpired
    } else {
        this
    }






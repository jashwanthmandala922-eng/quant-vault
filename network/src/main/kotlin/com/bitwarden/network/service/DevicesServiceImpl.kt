package com.quantvault.network.service

import com.quantvault.network.api.AuthenticatedDevicesApi
import com.quantvault.network.api.UnauthenticatedDevicesApi
import com.quantvault.network.model.TrustedDeviceKeysRequestJson
import com.quantvault.network.model.TrustedDeviceKeysResponseJson
import com.quantvault.network.util.base64UrlEncode
import com.quantvault.network.util.toResult

/**
 * The default implementation of the [DevicesService].
 */
internal class DevicesServiceImpl(
    private val authenticatedDevicesApi: AuthenticatedDevicesApi,
    private val unauthenticatedDevicesApi: UnauthenticatedDevicesApi,
) : DevicesService {
    override suspend fun getIsKnownDevice(
        emailAddress: String,
        deviceId: String,
    ): Result<Boolean> = unauthenticatedDevicesApi
        .getIsKnownDevice(
            emailAddress = emailAddress.base64UrlEncode(),
            deviceId = deviceId,
        )
        .toResult()

    override suspend fun trustDevice(
        appId: String,
        encryptedUserKey: String,
        encryptedDevicePublicKey: String,
        encryptedDevicePrivateKey: String,
    ): Result<TrustedDeviceKeysResponseJson> = authenticatedDevicesApi
        .updateTrustedDeviceKeys(
            appId = appId,
            request = TrustedDeviceKeysRequestJson(
                encryptedUserKey = encryptedUserKey,
                encryptedDevicePublicKey = encryptedDevicePublicKey,
                encryptedDevicePrivateKey = encryptedDevicePrivateKey,
            ),
        )
        .toResult()
}






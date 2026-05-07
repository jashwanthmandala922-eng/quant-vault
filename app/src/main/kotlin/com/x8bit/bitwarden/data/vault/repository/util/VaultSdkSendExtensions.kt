@file:Suppress("TooManyFunctions")

package com.x8bit.bitwarden.data.vault.repository.util

import com.quantvault.core.data.repository.util.SpecialCharWithPrecedenceComparator
import com.quantvault.network.model.SendAuthTypeJson
import com.quantvault.network.model.SendJsonRequest
import com.quantvault.network.model.SendTypeJson
import com.quantvault.network.model.SyncResponseJson
import com.quantvault.sdk.AuthType
import com.quantvault.sdk.Send
import com.quantvault.sdk.SendFile
import com.quantvault.sdk.SendText
import com.quantvault.sdk.SendType
import com.quantvault.sdk.SendView

/**
 * Converts a Quant Vault SDK [Send] object to a corresponding [SyncResponseJson.Send] object.
 */
fun Send.toEncryptedNetworkSend(fileLength: Long? = null): SendJsonRequest =
    SendJsonRequest(
        type = type.toNetworkSendType(),
        name = name,
        notes = notes,
        key = key,
        maxAccessCount = maxAccessCount?.toInt(),
        expirationDate = expirationDate,
        deletionDate = deletionDate,
        fileLength = fileLength,
        file = file?.toNetworkSendFile(),
        text = text?.toNetworkSendText(),
        password = password,
        isDisabled = disabled,
        shouldHideEmail = hideEmail,
        authType = authType.toNetworkSendAuthType(),
        emails = emails,
    )

/**
 * Converts a Quant Vault SDK [SendFile] object to a corresponding [SyncResponseJson.Send.File]
 * object.
 */
private fun SendFile.toNetworkSendFile(): SyncResponseJson.Send.File =
    SyncResponseJson.Send.File(
        fileName = fileName,
        size = size?.toInt(),
        sizeName = sizeName,
        id = id,
    )

/**
 * Converts a Quant Vault SDK [SendText] object to a corresponding [SyncResponseJson.Send.Text]
 * object.
 */
private fun SendText.toNetworkSendText(): SyncResponseJson.Send.Text =
    SyncResponseJson.Send.Text(
        isHidden = hidden,
        text = text,
    )

/**
 * Converts a Quant Vault SDK [SendType] object to a corresponding [SendTypeJson] object.
 */
private fun SendType.toNetworkSendType(): SendTypeJson =
    when (this) {
        SendType.TEXT -> SendTypeJson.TEXT
        SendType.FILE -> SendTypeJson.FILE
    }

/**
 * Converts a list of [SyncResponseJson.Send] objects to a list of corresponding
 * Quant Vault SDK [Send] objects.
 */
fun List<SyncResponseJson.Send>.toEncryptedSdkSendList(): List<Send> =
    map { it.toEncryptedSdkSend() }

/**
 * Converts a [SyncResponseJson.Send] object to a corresponding
 * Quant Vault SDK [Send] object.
 */
fun SyncResponseJson.Send.toEncryptedSdkSend(): Send =
    Send(
        id = id,
        accessId = accessId.toString(),
        name = name.toString(),
        notes = notes,
        key = key.toString(),
        password = password,
        type = type.toSdkSendType(),
        file = file?.toEncryptedSdkFile(),
        text = text?.toEncryptedSdkText(),
        maxAccessCount = maxAccessCount?.toUInt(),
        accessCount = accessCount.toUInt(),
        disabled = isDisabled,
        hideEmail = shouldHideEmail,
        revisionDate = revisionDate,
        deletionDate = deletionDate,
        expirationDate = expirationDate,
        emails = emails,
        authType = authType?.toSdkAuthType() ?: AuthType.NONE,
    )

/**
 * Converts a Quant Vault SDK [AuthType] object to a corresponding [SendAuthTypeJson] object.
 */
private fun AuthType.toNetworkSendAuthType(): SendAuthTypeJson =
    when (this) {
        AuthType.EMAIL -> SendAuthTypeJson.EMAIL
        AuthType.PASSWORD -> SendAuthTypeJson.PASSWORD
        AuthType.NONE -> SendAuthTypeJson.NONE
    }

/**
 * Converts a [SendAuthTypeJson] objects to a corresponding
 * Quant Vault SDK [AuthType].
 */
private fun SendAuthTypeJson.toSdkAuthType(): AuthType =
    when (this) {
        SendAuthTypeJson.PASSWORD -> AuthType.PASSWORD
        SendAuthTypeJson.EMAIL -> AuthType.EMAIL
        SendAuthTypeJson.NONE -> AuthType.NONE
    }

/**
 * Converts a [SyncResponseJson.Send.Text] object to a corresponding
 * Quant Vault SDK [SendText] object.
 */
private fun SyncResponseJson.Send.Text.toEncryptedSdkText(): SendText =
    SendText(
        text = text,
        hidden = isHidden,
    )

/**
 * Converts a [SyncResponseJson.Send.File] objects to a corresponding
 * Quant Vault SDK [SendFile] object.
 */
private fun SyncResponseJson.Send.File.toEncryptedSdkFile(): SendFile =
    SendFile(
        id = id.toString(),
        fileName = fileName.toString(),
        size = size.toString(),
        sizeName = sizeName.toString(),
    )

/**
 * Converts a [SendTypeJson] objects to a corresponding
 * Quant Vault SDK [SendType].
 */
private fun SendTypeJson.toSdkSendType(): SendType =
    when (this) {
        SendTypeJson.TEXT -> SendType.TEXT
        SendTypeJson.FILE -> SendType.FILE
    }

/**
 * Sorts the data in alphabetical order by name.
 */
@JvmName("toAlphabeticallySortedSendList")
fun List<SendView>.sortAlphabetically(): List<SendView> {
    return this.sortedWith(
        comparator = { send1, send2 ->
            SpecialCharWithPrecedenceComparator.compare(send1.name, send2.name)
        },
    )
}





package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.send.AuthType
import com.quantvault.send.Send
import com.quantvault.send.SendFile
import com.quantvault.send.SendText
import com.quantvault.send.SendType
import java.time.Instant

/**
 * Create a mock [Send] with a given [number].
 */
fun createMockSdkSend(
    number: Int,
    type: SendType = SendType.FILE,
): Send =
    Send(
        id = "mockId-$number",
        accessId = "mockAccessId-$number",
        name = "mockName-$number",
        notes = "mockNotes-$number",
        key = "mockKey-$number",
        password = "mockPassword-$number",
        type = type,
        file = createMockSdkFile(number = number),
        text = createMockSdkText(number = number),
        maxAccessCount = 1u,
        accessCount = 1u,
        disabled = false,
        hideEmail = false,
        revisionDate = Instant.parse("2023-10-27T12:00:00Z"),
        deletionDate = Instant.parse("2023-10-27T12:00:00Z"),
        expirationDate = Instant.parse("2023-10-27T12:00:00Z"),
        emails = null,
        authType = AuthType.PASSWORD,
    )

/**
 * Create a mock [SendFile] with a given [number].
 */
fun createMockSdkFile(number: Int): SendFile =
    SendFile(
        fileName = "mockFileName-$number",
        size = "1",
        sizeName = "mockSizeName-$number",
        id = "mockId-$number",
    )

/**
 * Create a mock [SendText] with a given [number].
 */
fun createMockSdkText(number: Int): SendText =
    SendText(
        hidden = false,
        text = "mockText-$number",
    )





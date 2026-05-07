package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.vault.Folder
import java.time.Instant

/**
 * Create a mock [Folder] with a given [number].
 */
fun createMockSdkFolder(number: Int): Folder =
    Folder(
        id = "mockId-$number",
        name = "mockName-$number",
        revisionDate = Instant.parse("2023-10-27T12:00:00Z"),
    )





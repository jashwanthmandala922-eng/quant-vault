package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.vault.FolderView
import java.time.Instant

/**
 * Create a mock [FolderView] with a given [number].
 */
fun createMockFolderView(number: Int): FolderView =
    FolderView(
        id = "mockId-$number",
        name = "mockName-$number",
        revisionDate = Instant.parse("2023-10-27T12:00:00Z"),
    )





package com.quantvault.app.ui.vault.feature.attachments.util

import com.quantvault.app.data.vault.datasource.sdk.model.createMockAttachmentView
import com.quantvault.app.data.vault.datasource.sdk.model.createMockCipherView
import com.quantvault.app.ui.vault.feature.attachments.AttachmentsState
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CipherViewExtensionsTest {

    @Test
    fun `toViewState should return content with items when CipherView has attachments`() {
        val cipherView = createMockCipherView(number = 1)

        val result = cipherView.toViewState()

        assertEquals(
            AttachmentsState.ViewState.Content(
                originalCipher = cipherView,
                attachments = persistentListOf(
                    AttachmentsState.AttachmentItem(
                        id = "mockId-1",
                        title = "mockFileName-1",
                        displaySize = "mockSizeName-1",
                        isLargeFile = false,
                    ),
                ),
                newAttachment = null,
            ),
            result,
        )
    }

    @Test
    fun `toViewState should return content without item when CipherView has no attachments`() {
        val cipherView = createMockCipherView(number = 1).copy(
            attachments = null,
        )

        val result = cipherView.toViewState()

        assertEquals(
            AttachmentsState.ViewState.Content(
                originalCipher = cipherView,
                attachments = persistentListOf(),
                newAttachment = null,
            ),
            result,
        )
    }

    @Test
    fun `toViewState should return content without items that have a null attachment ID`() {
        val cipherView = createMockCipherView(number = 1).copy(
            attachments = listOf(
                createMockAttachmentView(number = 1).copy(
                    id = null,
                ),
            ),
        )

        val result = cipherView.toViewState()

        assertEquals(
            AttachmentsState.ViewState.Content(
                originalCipher = cipherView,
                attachments = persistentListOf(),
                newAttachment = null,
            ),
            result,
        )
    }
}





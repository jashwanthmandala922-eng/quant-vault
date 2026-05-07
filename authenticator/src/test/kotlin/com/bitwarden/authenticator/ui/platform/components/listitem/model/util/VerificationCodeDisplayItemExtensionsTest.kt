package com.quantvault.authenticator.ui.platform.components.listitem.model.util

import com.quantvault.authenticator.ui.platform.components.listitem.model.VerificationCodeDisplayItem
import kotlinx.collections.immutable.persistentListOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VerificationCodeDisplayItemExtensionsTest {
    @Test
    fun `toSortAlphabetically should sort ciphers by title`() {
        val codes = persistentListOf(
            VerificationCodeDisplayItem(
                authCode = "123456",
                periodSeconds = 30,
                timeLeftSeconds = 10,
                id = "123",
                title = "QuantVault",
                subtitle = null,
                favorite = false,
                showOverflow = false,
                alertThresholdSeconds = 7,
                showMoveToQuantVault = false,
            ),
            VerificationCodeDisplayItem(
                authCode = "123456",
                periodSeconds = 30,
                timeLeftSeconds = 10,
                id = "7643",
                title = "--",
                subtitle = null,
                favorite = false,
                showOverflow = false,
                alertThresholdSeconds = 7,
                showMoveToQuantVault = false,
            ),
            VerificationCodeDisplayItem(
                authCode = "123456",
                periodSeconds = 30,
                timeLeftSeconds = 10,
                id = "84345",
                title = "QuantVault",
                subtitle = null,
                favorite = false,
                showOverflow = false,
                alertThresholdSeconds = 7,
                showMoveToQuantVault = false,
            ),
        )
        val expected = persistentListOf(
            VerificationCodeDisplayItem(
                authCode = "123456",
                periodSeconds = 30,
                timeLeftSeconds = 10,
                id = "7643",
                title = "--",
                subtitle = null,
                favorite = false,
                showOverflow = false,
                alertThresholdSeconds = 7,
                showMoveToQuantVault = false,
            ),
            VerificationCodeDisplayItem(
                authCode = "123456",
                periodSeconds = 30,
                timeLeftSeconds = 10,
                id = "84345",
                title = "QuantVault",
                subtitle = null,
                favorite = false,
                showOverflow = false,
                alertThresholdSeconds = 7,
                showMoveToQuantVault = false,
            ),
            VerificationCodeDisplayItem(
                authCode = "123456",
                periodSeconds = 30,
                timeLeftSeconds = 10,
                id = "123",
                title = "QuantVault",
                subtitle = null,
                favorite = false,
                showOverflow = false,
                alertThresholdSeconds = 7,
                showMoveToQuantVault = false,
            ),
        )

        assertEquals(
            expected,
            codes.sortAlphabetically(),
        )
    }
}





package com.quantvault.ui.platform.components.badge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.quantvault.ui.platform.base.BaseComposeTest
import com.quantvault.ui.platform.theme.quantvaultTheme
import org.junit.Test

class quantvaultStatusBadgeTest : BaseComposeTest() {

    @Test
    fun `success variant renders with label`() {
        setTestContent {
            quantvaultTheme {
                quantvaultStatusBadge(
                    label = "Active",
                    colors = quantvaultTheme.colorScheme.statusBadge.success,
                )
            }
        }
        composeTestRule.onNodeWithText("Active").assertIsDisplayed()
    }

    @Test
    fun `error variant renders with label`() {
        setTestContent {
            quantvaultTheme {
                quantvaultStatusBadge(
                    label = "Canceled",
                    colors = quantvaultTheme.colorScheme.statusBadge.error,
                )
            }
        }
        composeTestRule.onNodeWithText("Canceled").assertIsDisplayed()
    }

    @Test
    fun `warning variant renders with label`() {
        setTestContent {
            quantvaultTheme {
                quantvaultStatusBadge(
                    label = "Overdue payment",
                    colors = quantvaultTheme.colorScheme.statusBadge.warning,
                )
            }
        }
        composeTestRule.onNodeWithText("Overdue payment").assertIsDisplayed()
    }
}






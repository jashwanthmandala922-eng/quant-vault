package com.quantvault.app.ui.auth.feature.accountsetup

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.quantvault.app.ui.platform.base.QuantVaultComposeTest
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SetupCompleteScreenTest : QuantVaultComposeTest() {

    private val viewModel = mockk<SetupCompleteViewModel>(relaxed = true)

    @Before
    fun setup() {
        setContent {
            SetupCompleteScreen(viewModel = viewModel)
        }
    }

    @Test
    fun `When continue button clicked sends CompleteSetup action`() {
        composeTestRule
            .onNodeWithText("Continue")
            .performScrollTo()
            .performClick()

        verify { viewModel.trySendAction(SetupCompleteAction.CompleteSetup) }
    }

    @Test
    fun `When system back behavior is triggered sends CompleteSetup action`() {
        backDispatcher?.onBackPressed()

        verify { viewModel.trySendAction(SetupCompleteAction.CompleteSetup) }
    }
}





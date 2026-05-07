package com.quantvault.app.ui.auth.feature.preventaccountlockout

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.app.ui.platform.base.QuantVaultComposeTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PreventAccountLockoutScreenTest : QuantVaultComposeTest() {
    private var onBackHasBeenInvoked = false
    private val mutableEventFlow = bufferedMutableSharedFlow<PreventAccountLockoutEvent>()
    private val viewModel = mockk<PreventAccountLockoutViewModel>(relaxed = true) {
        every { eventFlow } returns mutableEventFlow
    }

    @Before
    fun setup() {
        setContent {
            PreventAccountLockoutScreen(
                onNavigateBack = { onBackHasBeenInvoked = true },
                viewModel = viewModel,
            )
        }
    }

    @Test
    fun `When navigation button is clicked CloseClickAction is sent`() {
        composeTestRule
            .onNodeWithContentDescription("Close")
            .performClick()

        verify { viewModel.trySendAction(PreventAccountLockoutAction.CloseClickAction) }
    }

    @Test
    fun `NavigateBackEvent from ViewModel invokes onBackNavigation lambda`() {
        mutableEventFlow.tryEmit(PreventAccountLockoutEvent.NavigateBack)

        assertTrue(onBackHasBeenInvoked)
    }
}





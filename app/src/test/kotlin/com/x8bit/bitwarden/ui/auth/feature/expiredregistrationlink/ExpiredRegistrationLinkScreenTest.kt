package com.quantvault.app.ui.auth.feature.expiredregistrationlink

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.app.ui.platform.base.QuantVaultComposeTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpiredRegistrationLinkScreenTest : QuantVaultComposeTest() {

    private var onNavigateBackCalled = false
    private var onNavigateToLoginCalled = false
    private var onNavigateToStartRegistrationCalled = false

    private val mutableEventFlow = bufferedMutableSharedFlow<ExpiredRegistrationLinkEvent>()
    private val viewModel = mockk<ExpiredRegistrationLinkViewModel>(relaxed = true) {
        every { eventFlow } returns mutableEventFlow
    }

    @Before
    fun setUp() {
        setContent {
            ExpiredRegistrationLinkScreen(
                onNavigateBack = { onNavigateBackCalled = true },
                onNavigateToLogin = { onNavigateToLoginCalled = true },
                onNavigateToStartRegistration = { onNavigateToStartRegistrationCalled = true },
                viewModel = viewModel,
            )
        }
    }

    @Test
    fun `System back event invokes CloseClicked action`() {
        backDispatcher?.onBackPressed()
        verify { viewModel.trySendAction(ExpiredRegistrationLinkAction.CloseClicked) }
    }

    @Test
    fun `CloseClicked sends NavigateBack action`() {
        composeTestRule
            .onNodeWithContentDescription("Close")
            .performClick()

        verify { viewModel.trySendAction(ExpiredRegistrationLinkAction.CloseClicked) }
    }

    @Test
    fun `RestartRegistrationClicked sends RestartRegistrationClicked action`() {
        composeTestRule
            .onNodeWithText("Restart registration")
            .performClick()

        verify { viewModel.trySendAction(ExpiredRegistrationLinkAction.RestartRegistrationClicked) }
    }

    @Test
    fun `GoToLoginClicked sends GoToLoginClicked action`() {
        composeTestRule
            .onNodeWithText("Log in")
            .performClick()

        verify { viewModel.trySendAction(ExpiredRegistrationLinkAction.GoToLoginClicked) }
    }

    @Test
    fun `NavigateBack event invokes onNavigateBack`() {
        mutableEventFlow.tryEmit(ExpiredRegistrationLinkEvent.NavigateBack)
        assertTrue(onNavigateBackCalled)
    }

    @Test
    fun `NavigateToLogin event invokes onNavigateToLogin`() {
        mutableEventFlow.tryEmit(ExpiredRegistrationLinkEvent.NavigateToLogin)
        assertTrue(onNavigateToLoginCalled)
    }

    @Test
    fun `NavigateToStartRegistration event invokes onNavigateToStartRegistration`() {
        mutableEventFlow.tryEmit(ExpiredRegistrationLinkEvent.NavigateToStartRegistration)
        assertTrue(onNavigateToStartRegistrationCalled)
    }
}





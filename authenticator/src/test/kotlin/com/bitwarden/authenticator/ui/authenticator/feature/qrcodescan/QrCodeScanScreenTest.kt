package com.quantvault.authenticator.ui.authenticator.feature.qrcodescan

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.quantvault.authenticator.ui.platform.base.AuthenticatorComposeTest
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.ui.platform.feature.qrcodescan.util.FakeQrCodeAnalyzer
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class QrCodeScanScreenTest : AuthenticatorComposeTest() {

    private var onNavigateBackCalled = false
    private var onNavigateToManualCodeEntryScreenCalled = false

    private val qrCodeAnalyzer = FakeQrCodeAnalyzer()
    private val mutableStateFlow = MutableStateFlow(DEFAULT_STATE)
    private val mutableEventFlow = bufferedMutableSharedFlow<QrCodeScanEvent>()

    private val viewModel: QrCodeScanViewModel = mockk {
        every { stateFlow } returns mutableStateFlow
        every { eventFlow } returns mutableEventFlow
        every { trySendAction(any()) } just runs
    }

    @Before
    fun setup() {
        setContent(
            qrCodeAnalyzer = qrCodeAnalyzer,
        ) {
            QrCodeScanScreen(
                viewModel = viewModel,
                onNavigateBack = { onNavigateBackCalled = true },
                onNavigateToManualCodeEntryScreen = {
                    onNavigateToManualCodeEntryScreenCalled = true
                },
            )
        }
    }

    @Test
    fun `on NavigateBack event receive should call navigate back`() {
        mutableEventFlow.tryEmit(QrCodeScanEvent.NavigateBack)
        assertTrue(onNavigateBackCalled)
    }

    @Test
    fun `on NavigateToManualCodeEntry event receive should call navigate to manual code entry`() {
        mutableEventFlow.tryEmit(QrCodeScanEvent.NavigateToManualCodeEntry)
        assertTrue(onNavigateToManualCodeEntryScreenCalled)
    }

    @Test
    fun `on Save here click should send SaveLocallyClick action`() {
        mutableStateFlow.update {
            DEFAULT_STATE.copy(
                dialog = QrCodeScanState.DialogState.ChooseSaveLocation,
            )
        }
        composeTestRule
            .onNodeWithText("Save here")
            .assertIsDisplayed()
            .assert(hasAnyAncestor(isDialog()))
            .performClick()
        verify { viewModel.trySendAction(QrCodeScanAction.SaveLocallyClick(false)) }

        // Click again but with "Save as default" checked:
        composeTestRule
            .onNodeWithText("Save option as default")
            .performClick()
        composeTestRule
            .onNodeWithText("Save here")
            .assertIsDisplayed()
            .assert(hasAnyAncestor(isDialog()))
            .performClick()
        verify { viewModel.trySendAction(QrCodeScanAction.SaveLocallyClick(true)) }
    }

    @Test
    fun `on Save to QuantVault click should send SaveToQuantVaultClick action`() {
        mutableStateFlow.update {
            DEFAULT_STATE.copy(
                dialog = QrCodeScanState.DialogState.ChooseSaveLocation,
            )
        }
        composeTestRule
            .onNodeWithText("Save to QuantVault")
            .assertIsDisplayed()
            .assert(hasAnyAncestor(isDialog()))
            .performClick()
        verify { viewModel.trySendAction(QrCodeScanAction.SaveToQuantVaultClick(false)) }

        // Click again but with "Save as default" checked:
        composeTestRule
            .onNodeWithText("Save option as default")
            .performClick()
        composeTestRule
            .onNodeWithText("Save to QuantVault")
            .assertIsDisplayed()
            .assert(hasAnyAncestor(isDialog()))
            .performClick()
        verify { viewModel.trySendAction(QrCodeScanAction.SaveToQuantVaultClick(true)) }
    }

    @Test
    fun `dismissing error dialog should send SaveToQuantVaultErrorDismiss`() {
        // Make sure dialog isn't showing:
        composeTestRule
            .onNodeWithText("Something went wrong")
            .assertDoesNotExist()

        // Display dialog and click OK
        mutableStateFlow.update {
            DEFAULT_STATE.copy(
                dialog = QrCodeScanState.DialogState.SaveToQuantVaultError,
            )
        }
        composeTestRule
            .onNodeWithText("Something went wrong")
            .assertIsDisplayed()
            .assert(hasAnyAncestor(isDialog()))
        composeTestRule
            .onNodeWithText(text = "Okay")
            .assertIsDisplayed()
            .assert(hasAnyAncestor(isDialog()))
            .performClick()

        verify { viewModel.trySendAction(QrCodeScanAction.SaveToQuantVaultErrorDismiss) }
    }
}

private val DEFAULT_STATE = QrCodeScanState(
    hasHandledScan = false,
    dialog = null,
)





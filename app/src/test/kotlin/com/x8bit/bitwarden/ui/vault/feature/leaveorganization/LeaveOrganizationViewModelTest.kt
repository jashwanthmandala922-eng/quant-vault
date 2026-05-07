package com.quantvault.app.ui.vault.feature.leaveorganization

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.quantvault.data.repository.model.Environment
import com.quantvault.network.model.OrganizationType
import com.bitwarden.ui.platform.base.BaseViewModelTest
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.bitwarden.ui.platform.manager.snackbar.SnackbarRelayManager
import com.bitwarden.ui.util.asText
import com.quantvault.app.data.auth.datasource.disk.model.OnboardingStatus
import com.quantvault.app.data.auth.repository.AuthRepository
import com.quantvault.app.data.auth.repository.model.RevokeFromOrganizationResult
import com.quantvault.app.data.auth.repository.model.UserState
import com.quantvault.app.data.auth.repository.model.VaultUnlockType
import com.quantvault.app.data.auth.repository.model.createMockOrganization
import com.quantvault.app.data.platform.manager.model.FirstTimeState
import com.quantvault.app.data.vault.manager.VaultMigrationManager
import com.quantvault.app.ui.platform.model.SnackbarRelay
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.UnknownHostException
import com.quantvault.app.R

class LeaveOrganizationViewModelTest : BaseViewModelTest() {

    private val mockAuthRepository: AuthRepository = mockk {
        every { userStateFlow } returns MutableStateFlow(DEFAULT_USER_STATE)
    }

    private val mockSnackbarRelayManager: SnackbarRelayManager<SnackbarRelay> = mockk {
        every { sendSnackbarData(data = any(), relay = any()) } just runs
    }

    private val mockVaultMigrationManager: VaultMigrationManager = mockk {
        every { clearMigrationState() } just runs
    }

    @BeforeEach
    fun setup() {
        mockkStatic(SavedStateHandle::toLeaveOrganizationArgs)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(SavedStateHandle::toLeaveOrganizationArgs)
    }

    @Test
    fun `initial state should be correct`() {
        val viewModel = createViewModel()
        val expectedState = LeaveOrganizationState(
            organizationId = ORGANIZATION_ID,
            organizationName = ORGANIZATION_NAME,
            dialogState = null,
        )
        assertEquals(expectedState, viewModel.stateFlow.value)
    }

    @Test
    fun `BackClick should emit NavigateBack event`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(LeaveOrganizationAction.BackClick)
            assertEquals(LeaveOrganizationEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `HelpLinkClick should emit LaunchUri event with help URL`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(LeaveOrganizationAction.HelpLinkClick)
            val event = awaitItem()
            assert(event is LeaveOrganizationEvent.LaunchUri)
            assertEquals(
                "https://Quant Vault.com/help/transfer-ownership/",
                (event as LeaveOrganizationEvent.LaunchUri).uri,
            )
        }
    }

    @Test
    fun `LeaveOrganizationClick should show loading dialog`() = runTest {
        coEvery {
            mockAuthRepository.revokeFromOrganization(any())
        } coAnswers {
            RevokeFromOrganizationResult.Success
        }

        val viewModel = createViewModel()
        viewModel.stateFlow.test {
            assertEquals(null, awaitItem().dialogState)

            viewModel.trySendAction(LeaveOrganizationAction.LeaveOrganizationClick)

            val loadingState = awaitItem()
            assert(loadingState.dialogState is LeaveOrganizationState.DialogState.Loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `LeaveOrganizationClick with Success should track ItemOrganizationDeclined event, send snackbar, and clear migration state`() =
        runTest {
            coEvery {
                mockAuthRepository.revokeFromOrganization(ORGANIZATION_ID)
            } returns RevokeFromOrganizationResult.Success

            val viewModel = createViewModel()
            viewModel.trySendAction(LeaveOrganizationAction.LeaveOrganizationClick)

            verify {
                mockSnackbarRelayManager.sendSnackbarData(
                    relay = SnackbarRelay.LEFT_ORGANIZATION,
                    data = QuantVaultSnackbarData(
                        message = R.string.you_left_the_organization.asText(),
                    ),
                )
                mockVaultMigrationManager.clearMigrationState()
            }

            assertNull(viewModel.stateFlow.value.dialogState)
        }

    @Test
    fun `DismissDialogClicked should clear dialog and clear migration state`() = runTest {
        val viewModel = createViewModel()

        viewModel.stateFlow.test {
            awaitItem()

            viewModel.trySendAction(
                LeaveOrganizationAction.Internal.RevokeFromOrganizationResultReceived(
                    result = RevokeFromOrganizationResult.Error(null),
                ),
            )

            assertEquals(
                DEFAULT_STATE.copy(
                    dialogState = LeaveOrganizationState.DialogState.Error(
                        title = R.string.an_error_has_occurred.asText(),
                        message = R.string.generic_error_message.asText(),
                        error = null,
                    ),
                ),
                awaitItem(),
            )

            // Dismiss the dialog
            viewModel.trySendAction(LeaveOrganizationAction.DismissDialog)
            verify(exactly = 0) { mockVaultMigrationManager.clearMigrationState() }
            assertEquals(
                DEFAULT_STATE, awaitItem(),
            )
        }
    }

    @Test
    fun `DismissNoNetworkDialogClicked should clear dialog and clear migration state`() = runTest {
        val viewModel = createViewModel()
        val error = UnknownHostException("No network")
        viewModel.stateFlow.test {
            awaitItem()

            viewModel.trySendAction(
                LeaveOrganizationAction.Internal.RevokeFromOrganizationResultReceived(
                    result = RevokeFromOrganizationResult.Error(
                        error = error,
                    ),
                ),
            )

            assertEquals(
                DEFAULT_STATE.copy(
                    dialogState = LeaveOrganizationState.DialogState.NoNetwork(
                        title = R.string.internet_connection_required_title.asText(),
                        message = R.string.internet_connection_required_message.asText(),
                        error = error,
                    ),
                ),
                awaitItem(),
            )

            // Dismiss the dialog and clear migration
            viewModel.trySendAction(LeaveOrganizationAction.DismissNoNetworkDialog)
            verify(exactly = 1) { mockVaultMigrationManager.clearMigrationState() }
            assertEquals(
                DEFAULT_STATE, awaitItem(),
            )
        }
    }

    @Test
    fun `DismissDialog should clear dialog state`() = runTest {
        coEvery {
            mockAuthRepository.revokeFromOrganization(ORGANIZATION_ID)
        } returns RevokeFromOrganizationResult.Error(Throwable("Error"))

        val viewModel = createViewModel()
        viewModel.trySendAction(LeaveOrganizationAction.LeaveOrganizationClick)

        assert(viewModel.stateFlow.value.dialogState != null)

        viewModel.trySendAction(LeaveOrganizationAction.DismissDialog)

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    @Test
    fun `state should be restored from SavedStateHandle`() {
        val savedState = LeaveOrganizationState(
            organizationId = "saved-org-id",
            organizationName = "Saved Organization",
            dialogState = null,
        )

        val viewModel = createViewModel(state = savedState)

        assertEquals(savedState, viewModel.stateFlow.value)
    }

    private fun createViewModel(
        state: LeaveOrganizationState = DEFAULT_STATE,
    ): LeaveOrganizationViewModel {
        return LeaveOrganizationViewModel(
            authRepository = mockAuthRepository,
            snackbarRelayManager = mockSnackbarRelayManager,
            vaultMigrationManager = mockVaultMigrationManager,
            savedStateHandle = SavedStateHandle(mapOf("state" to state)),
        )
    }
}

private const val ORGANIZATION_ID = "organization-id-1"
private const val ORGANIZATION_NAME = "Test Organization"

private val DEFAULT_ORGANIZATION = createMockOrganization(
    number = 1,
    id = ORGANIZATION_ID,
    name = ORGANIZATION_NAME,
    role = OrganizationType.USER,
    keyConnectorUrl = null,
)

private val DEFAULT_USER_STATE = UserState(
    activeUserId = "user-id-1",
    accounts = listOf(
        UserState.Account(
            userId = "user-id-1",
            name = "Test User",
            email = "test@example.com",
            avatarColorHex = "#175DDC",
            environment = Environment.Us,
            isPremium = false,
            isLoggedIn = true,
            isVaultUnlocked = true,
            needsPasswordReset = false,
            needsMasterPassword = false,
            hasMasterPassword = true,
            trustedDevice = null,
            organizations = listOf(DEFAULT_ORGANIZATION),
            isBiometricsEnabled = false,
            vaultUnlockType = VaultUnlockType.MASTER_PASSWORD,
            isUsingKeyConnector = false,
            onboardingStatus = OnboardingStatus.COMPLETE,
            firstTimeState = FirstTimeState(),
            isExportable = true,
            creationDate = null,
        ),
    ),
)

private val DEFAULT_STATE = LeaveOrganizationState(
    organizationId = ORGANIZATION_ID,
    organizationName = ORGANIZATION_NAME,
    dialogState = null,
)







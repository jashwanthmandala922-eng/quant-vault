package com.quantvault.authenticator.ui.platform.feature.settings

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.quantvault.authenticator.BuildConfig
import com.quantvault.authenticator.data.auth.repository.AuthRepository
import com.quantvault.authenticator.data.authenticator.repository.AuthenticatorRepository
import com.quantvault.authenticator.data.authenticator.repository.model.SharedVerificationCodesState
import com.quantvault.authenticator.data.authenticator.repository.util.isSyncWithQuantVaultEnabled
import com.quantvault.authenticator.data.platform.manager.clipboard.QuantVaultClipboardManager
import com.quantvault.authenticator.data.platform.manager.lock.model.AppTimeout
import com.quantvault.authenticator.data.platform.repository.SettingsRepository
import com.quantvault.authenticator.ui.platform.feature.settings.appearance.model.AppLanguage
import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.authenticator.ui.platform.model.SnackbarRelay
import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManager
import com.quantvault.authenticatorbridge.manager.model.AccountSyncState
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.core.util.isBuildVersionAtLeast
import com.quantvault.ui.platform.base.BaseViewModelTest
import com.quantvault.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import com.quantvault.ui.platform.manager.snackbar.SnackbarRelayManager
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.asText
import com.quantvault.ui.util.concat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SettingsViewModelTest : BaseViewModelTest() {

    private val authenticatorBridgeManager: AuthenticatorBridgeManager = mockk {
        every { accountSyncStateFlow } returns MutableStateFlow(AccountSyncState.Loading)
    }

    private val mutableSharedCodesFlow = MutableStateFlow(MOCK_SHARED_CODES_STATE)
    private val authenticatorRepository: AuthenticatorRepository = mockk {
        every { sharedCodesStateFlow } returns mutableSharedCodesFlow
    }
    private val mutableDefaultSaveOptionFlow = bufferedMutableSharedFlow<DefaultSaveOption>()
    private val mutableScreenCaptureAllowedStateFlow = MutableStateFlow(false)
    private val mutableIsDynamicColorsEnabledFlow = MutableStateFlow(false)
    private val mutableIsUnlockWithBiometricsEnabledFlow = MutableStateFlow(true)
    private val authRepository: AuthRepository = mockk {
        every { isUnlockWithBiometricsEnabled } returns true
        every { isUnlockWithBiometricsEnabledFlow } returns mutableIsUnlockWithBiometricsEnabledFlow
    }
    private val mutableAppTimeoutStateFlow = MutableStateFlow<AppTimeout>(AppTimeout.OnAppRestart)
    private val mutableIsShowNextCodeEnabledFlow = MutableStateFlow(false)
    private val settingsRepository: SettingsRepository = mockk {
        every { appLanguage } returns APP_LANGUAGE
        every { appTheme } returns APP_THEME
        every { defaultSaveOption } returns DEFAULT_SAVE_OPTION
        every { defaultSaveOptionFlow } returns mutableDefaultSaveOptionFlow
        every { isCrashLoggingEnabled } returns true
        every { isScreenCaptureAllowedStateFlow } returns mutableScreenCaptureAllowedStateFlow
        every { isScreenCaptureAllowed } answers { mutableScreenCaptureAllowedStateFlow.value }
        every { isScreenCaptureAllowed = any() } just runs
        every { isDynamicColorsEnabled } answers { mutableIsDynamicColorsEnabledFlow.value }
        every { isDynamicColorsEnabled = any() } just runs
        every { isDynamicColorsEnabledFlow } returns mutableIsDynamicColorsEnabledFlow
        every { isShowNextCodeEnabled } returns false
        every { isShowNextCodeEnabled = any() } just runs
        every { isShowNextCodeEnabledFlow } returns mutableIsShowNextCodeEnabledFlow
        every { appTimeoutState = any() } just runs
        every { appTimeoutStateFlow } returns mutableAppTimeoutStateFlow
        every { appTimeoutState } answers { mutableAppTimeoutStateFlow.value }
    }
    private val clipboardManager: QuantVaultClipboardManager = mockk()
    private val mutableSnackbarFlow = bufferedMutableSharedFlow<QuantVaultSnackbarData>()
    private val snackbarRelayManager = mockk<SnackbarRelayManager<SnackbarRelay>> {
        every {
            getSnackbarDataFlow(relay = any(), relays = anyVararg())
        } returns mutableSnackbarFlow
    }

    @BeforeEach
    fun setup() {
        mockkStatic(SharedVerificationCodesState::isSyncWithQuantVaultEnabled)
        every { MOCK_SHARED_CODES_STATE.isSyncWithQuantVaultEnabled } returns false
        mockkStatic(::isBuildVersionAtLeast)
        every { isBuildVersionAtLeast(Build.VERSION_CODES.S) } returns true
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(SharedVerificationCodesState::isSyncWithQuantVaultEnabled)
        unmockkStatic(::isBuildVersionAtLeast)
    }

    @Test
    fun `when SnackbarRelay flow updates, snackbar is shown`() = runTest {
        val viewModel = createViewModel()
        val expectedSnackbarData = QuantVaultSnackbarData(message = "test message".asText())
        viewModel.eventFlow.test {
            mutableSnackbarFlow.tryEmit(expectedSnackbarData)
            assertEquals(SettingsEvent.ShowSnackbar(expectedSnackbarData), awaitItem())
        }
    }

    @Test
    fun `initialState should be correct when saved state is null but OS version is too low`() {
        every {
            authenticatorBridgeManager.accountSyncStateFlow
        } returns MutableStateFlow(AccountSyncState.OsVersionNotSupported)
        val viewModel = createViewModel(savedState = null)
        val expectedState = DEFAULT_STATE.copy(
            showSyncWithQuantVault = false,
            showDefaultSaveOptionRow = false,
        )
        assertEquals(
            expectedState,
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `initialState should be correct when saved state is null and OS version is supported`() {
        every {
            authenticatorBridgeManager.accountSyncStateFlow
        } returns MutableStateFlow(AccountSyncState.Loading)
        val viewModel = createViewModel(savedState = null)
        val expectedState = DEFAULT_STATE.copy(
            showSyncWithQuantVault = true,
        )
        assertEquals(
            expectedState,
            viewModel.stateFlow.value,
        )
    }

    @Test
    @Suppress("MaxLineLength")
    fun `on SyncWithQuantVaultClick receive with AccountSyncState AppNotInstalled should emit NavigateToQuantVaultPlayStoreListing`() =
        runTest {
            every {
                authenticatorBridgeManager.accountSyncStateFlow
            } returns MutableStateFlow(AccountSyncState.AppNotInstalled)
            val viewModel = createViewModel()
            viewModel.eventFlow.test {
                viewModel.trySendAction(SettingsAction.DataClick.SyncWithQuantVaultClick)
                assertEquals(
                    SettingsEvent.NavigateToQuantVaultPlayStoreListing,
                    awaitItem(),
                )
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `on SyncWithQuantVaultClick receive with AccountSyncState not AppNotInstalled should emit NavigateToQuantVaultApp`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.eventFlow.test {
                viewModel.trySendAction(SettingsAction.DataClick.SyncWithQuantVaultClick)
                assertEquals(
                    SettingsEvent.NavigateToQuantVaultApp,
                    awaitItem(),
                )
            }
        }

    @Test
    fun `on SyncLearnMoreClick should emit NavigateToSyncInformation`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(SettingsAction.DataClick.SyncLearnMoreClick)
            assertEquals(SettingsEvent.NavigateToSyncInformation, awaitItem())
        }
    }

    @Test
    @Suppress("MaxLineLength")
    fun `Default save option row should only show when shared codes state shows syncing as enabled`() =
        runTest {
            val viewModel = createViewModel()
            val enabledState: SharedVerificationCodesState = mockk {
                every { isSyncWithQuantVaultEnabled } returns true
            }
            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )
                mutableSharedCodesFlow.update { enabledState }
                assertEquals(
                    DEFAULT_STATE.copy(
                        showDefaultSaveOptionRow = true,
                    ),
                    awaitItem(),
                )
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `on DefaultSaveOptionUpdated should update SettingsRepository`() {
        val expectedOption = DefaultSaveOption.QuantVault_APP
        every { settingsRepository.defaultSaveOption = expectedOption } just runs
        val viewModel = createViewModel()
        viewModel.trySendAction(SettingsAction.DataClick.DefaultSaveOptionUpdated(expectedOption))
        verify { settingsRepository.defaultSaveOption = expectedOption }
    }

    @Test
    @Suppress("MaxLineLength")
    fun `Default save option should update when repository emits`() = runTest {
        val viewModel = createViewModel()
        viewModel.stateFlow.test {
            assertEquals(DEFAULT_STATE, awaitItem())

            mutableDefaultSaveOptionFlow.emit(DefaultSaveOption.LOCAL)
            assertEquals(
                DEFAULT_STATE.copy(
                    defaultSaveOption = DefaultSaveOption.LOCAL,
                ),
                awaitItem(),
            )

            mutableDefaultSaveOptionFlow.emit(DefaultSaveOption.QuantVault_APP)
            assertEquals(
                DEFAULT_STATE.copy(
                    defaultSaveOption = DefaultSaveOption.QuantVault_APP,
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `on AllowScreenCaptureToggled should update value in state and SettingsRepository`() =
        runTest {
            val viewModel = createViewModel()
            val newScreenCaptureAllowedValue = true

            viewModel.trySendAction(
                SettingsAction.SecurityClick.AllowScreenCaptureToggle(
                    newScreenCaptureAllowedValue,
                ),
            )

            verify(exactly = 1) {
                settingsRepository.isScreenCaptureAllowed = newScreenCaptureAllowedValue
            }

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE.copy(allowScreenCapture = true),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `on DynamicColorChange should update value in state and SettingsRepository`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.trySendAction(
                SettingsAction.AppearanceChange.DynamicColorChange(isEnabled = true),
            )

            verify(exactly = 1) {
                settingsRepository.isDynamicColorsEnabled = true
            }
        }

    @Test
    fun `on DynamicColorsUpdated should update value in state and SettingsRepository`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.trySendAction(SettingsAction.Internal.DynamicColorsUpdated(isEnabled = true))

            assertEquals(
                DEFAULT_STATE.copy(
                    appearance = DEFAULT_APPEARANCE_STATE.copy(isDynamicColorsEnabled = true),
                ),
                viewModel.stateFlow.value,
            )
        }

    @Test
    fun `on BiometricSupportChanged should update value in state`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.trySendAction(
                SettingsAction.BiometricSupportChanged(isBiometricsSupported = false),
            )

            assertEquals(
                DEFAULT_STATE.copy(
                    hasBiometricsSupport = false,
                ),
                viewModel.stateFlow.value,
            )
        }

    @Test
    fun `on AppTimeoutStateUpdated should update value in state`() = runTest {
        val viewModel = createViewModel()

        viewModel.stateFlow.test {
            assertEquals(DEFAULT_STATE, awaitItem())

            viewModel.trySendAction(
                SettingsAction.Internal.AppTimeoutStateUpdated(appTimeout = AppTimeout.Immediately),
            )
            assertEquals(
                DEFAULT_STATE.copy(appTimeout = AppTimeout.Immediately),
                awaitItem(),
            )

            viewModel.trySendAction(
                SettingsAction.Internal.AppTimeoutStateUpdated(appTimeout = AppTimeout.OneMinute),
            )
            assertEquals(
                DEFAULT_STATE.copy(appTimeout = AppTimeout.OneMinute),
                awaitItem(),
            )
        }
    }

    @Test
    fun `on ShowNextCodeToggle should update value in state and SettingsRepository`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.trySendAction(
                SettingsAction.DataClick.ShowNextCodeToggle(enabled = true),
            )

            verify(exactly = 1) {
                settingsRepository.isShowNextCodeEnabled = true
            }

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE.copy(isShowNextCodeEnabled = true),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `on ShowNextCodeUpdated should update value in state`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.trySendAction(
                SettingsAction.Internal.ShowNextCodeUpdated(isEnabled = true),
            )

            assertEquals(
                DEFAULT_STATE.copy(isShowNextCodeEnabled = true),
                viewModel.stateFlow.value,
            )
        }

    @Test
    fun `isShowNextCodeEnabledFlow emission should update state`() = runTest {
        val viewModel = createViewModel()

        viewModel.stateFlow.test {
            assertEquals(DEFAULT_STATE, awaitItem())

            mutableIsShowNextCodeEnabledFlow.value = true
            assertEquals(
                DEFAULT_STATE.copy(isShowNextCodeEnabled = true),
                awaitItem(),
            )

            mutableIsShowNextCodeEnabledFlow.value = false
            assertEquals(
                DEFAULT_STATE.copy(isShowNextCodeEnabled = false),
                awaitItem(),
            )
        }
    }

    @Test
    fun `on AppTimeoutChange should update value in state`() = runTest {
        val viewModel = createViewModel()

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(appTimeout = AppTimeout.Type.IMMEDIATELY),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.Immediately
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(appTimeout = AppTimeout.Type.ONE_MINUTE),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.OneMinute
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(
                appTimeout = AppTimeout.Type.FIVE_MINUTES,
            ),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.FiveMinutes
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(
                appTimeout = AppTimeout.Type.FIFTEEN_MINUTES,
            ),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.FifteenMinutes
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(
                appTimeout = AppTimeout.Type.THIRTY_MINUTES,
            ),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.ThirtyMinutes
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(appTimeout = AppTimeout.Type.ONE_HOUR),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.OneHour
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(appTimeout = AppTimeout.Type.FOUR_HOURS),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.FourHours
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(
                appTimeout = AppTimeout.Type.ON_APP_RESTART,
            ),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.OnAppRestart
        }

        viewModel.trySendAction(
            SettingsAction.SecurityClick.AppTimeoutChange(appTimeout = AppTimeout.Type.NEVER),
        )
        verify {
            settingsRepository.appTimeoutState = AppTimeout.Never
        }
    }

    private fun createViewModel(
        savedState: SettingsState? = DEFAULT_STATE,
    ): SettingsViewModel = SettingsViewModel(
        savedStateHandle = SavedStateHandle().apply { this["state"] = savedState },
        clock = CLOCK,
        authRepository = authRepository,
        authenticatorBridgeManager = authenticatorBridgeManager,
        authenticatorRepository = authenticatorRepository,
        settingsRepository = settingsRepository,
        clipboardManager = clipboardManager,
        snackbarRelayManager = snackbarRelayManager,
    )
}

private val MOCK_SHARED_CODES_STATE: SharedVerificationCodesState = mockk()
private val APP_LANGUAGE = AppLanguage.ENGLISH
private val APP_THEME = AppTheme.DEFAULT
private val CLOCK = Clock.fixed(
    Instant.parse("2024-10-12T12:00:00Z"),
    ZoneOffset.UTC,
)
private val DEFAULT_SAVE_OPTION = DefaultSaveOption.NONE
private val DEFAULT_APPEARANCE_STATE = SettingsState.Appearance(
    language = APP_LANGUAGE,
    theme = APP_THEME,
    isDynamicColorsSupported = true,
    isDynamicColorsEnabled = false,
)
private val DEFAULT_STATE = SettingsState(
    appearance = DEFAULT_APPEARANCE_STATE,
    isSubmitCrashLogsEnabled = true,
    isUnlockWithBiometricsEnabled = true,
    showSyncWithQuantVault = true,
    showDefaultSaveOptionRow = false,
    defaultSaveOption = DEFAULT_SAVE_OPTION,
    dialog = null,
    version = QuantVaultString.version.asText()
        .concat(": ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})".asText()),
    copyrightInfo = "© QuantVault Inc. 2015-2024".asText(),
    allowScreenCapture = false,
    isShowNextCodeEnabled = false,
    hasBiometricsSupport = true,
    appTimeout = AppTimeout.OnAppRestart,
)





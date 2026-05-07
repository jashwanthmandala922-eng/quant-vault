package com.quantvault.authenticator.data.platform.repository

import app.cash.turbine.test
import com.quantvault.authenticator.data.platform.datasource.disk.SettingsDiskSource
import com.quantvault.authenticator.data.platform.manager.lock.model.AppTimeout
import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.core.data.manager.dispatcher.FakeDispatcherManager
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SettingsRepositoryTest {

    private val mutableAppTimeoutInMinutesFlow = MutableStateFlow<Int?>(null)
    private val settingsDiskSource: SettingsDiskSource = mockk {
        every { getAlertThresholdSeconds() } returns 7
        every { appTimeoutInMinutesFlow } returns mutableAppTimeoutInMinutesFlow
        every { appTimeoutInMinutes } answers { mutableAppTimeoutInMinutesFlow.value }
    }

    private val settingsRepository: SettingsRepository = SettingsRepositoryImpl(
        settingsDiskSource = settingsDiskSource,
        flightRecorderManager = mockk(),
        dispatcherManager = FakeDispatcherManager(),
    )

    @Test
    fun `hasUserDismissedDownloadQuantVaultCard should return false when disk source is null`() {
        every { settingsDiskSource.hasUserDismissedDownloadQuantVaultCard } returns null
        assertFalse(settingsRepository.hasUserDismissedDownloadQuantVaultCard)
    }

    @Test
    fun `hasUserDismissedDownloadQuantVaultCard should return false when disk source is false`() {
        every { settingsDiskSource.hasUserDismissedDownloadQuantVaultCard } returns false
        assertFalse(settingsRepository.hasUserDismissedDownloadQuantVaultCard)
    }

    @Test
    fun `hasUserDismissedDownloadQuantVaultCard should return true when disk source is true`() {
        every { settingsDiskSource.hasUserDismissedDownloadQuantVaultCard } returns true
        assertTrue(settingsRepository.hasUserDismissedDownloadQuantVaultCard)
    }

    @Test
    fun `hasUserDismissedSyncWithQuantVaultCard should return false when disk source is null`() {
        every { settingsDiskSource.hasUserDismissedSyncWithQuantVaultCard } returns null
        assertFalse(settingsRepository.hasUserDismissedSyncWithQuantVaultCard)
    }

    @Test
    fun `hasUserDismissedSyncWithQuantVaultCard should return false when disk source is false`() {
        every { settingsDiskSource.hasUserDismissedSyncWithQuantVaultCard } returns false
        assertFalse(settingsRepository.hasUserDismissedSyncWithQuantVaultCard)
    }

    @Test
    fun `hasUserDismissedSyncWithQuantVaultCard should return true when disk source is true`() {
        every { settingsDiskSource.hasUserDismissedSyncWithQuantVaultCard } returns true
        assertTrue(settingsRepository.hasUserDismissedSyncWithQuantVaultCard)
    }

    @Test
    fun `hasUserDismissedSyncWithQuantVaultCard set should set disk source`() {
        every { settingsDiskSource.hasUserDismissedSyncWithQuantVaultCard = true } just runs
        settingsRepository.hasUserDismissedSyncWithQuantVaultCard = true
        verify { settingsRepository.hasUserDismissedSyncWithQuantVaultCard = true }
    }

    @Test
    fun `defaultSaveOption should pull from and update SettingsDiskSource`() {
        // Reading from repository should read from disk source:
        every { settingsDiskSource.defaultSaveOption } returns DefaultSaveOption.NONE
        assertEquals(
            DefaultSaveOption.NONE,
            settingsRepository.defaultSaveOption,
        )
        verify { settingsDiskSource.defaultSaveOption }

        // Writing to repository should write to disk source:
        every { settingsDiskSource.defaultSaveOption = DefaultSaveOption.QuantVault_APP } just runs
        settingsRepository.defaultSaveOption = DefaultSaveOption.QuantVault_APP
        verify { settingsDiskSource.defaultSaveOption = DefaultSaveOption.QuantVault_APP }
    }

    @Test
    fun `defaultSaveOptionFlow should match SettingsDiskSource`() = runTest {
        // Reading from repository should read from disk source:
        val expectedOptions = listOf(
            DefaultSaveOption.NONE,
            DefaultSaveOption.LOCAL,
            DefaultSaveOption.QuantVault_APP,
            DefaultSaveOption.NONE,
        )
        every { settingsDiskSource.defaultSaveOptionFlow } returns flow {
            expectedOptions.forEach { emit(it) }
        }

        settingsRepository.defaultSaveOptionFlow.test {
            expectedOptions.forEach {
                assertEquals(it, awaitItem())
            }
            awaitComplete()
        }
    }

    @Test
    fun `isDynamicColorsEnabled should pull from and update SettingsDiskSource`() {
        // Reading from repository should read from disk source:
        every { settingsDiskSource.isDynamicColorsEnabled } returns null
        assertFalse(settingsRepository.isDynamicColorsEnabled)
        verify { settingsDiskSource.isDynamicColorsEnabled }

        // Writing to repository should write to disk source:
        every { settingsDiskSource.isDynamicColorsEnabled = true } just runs
        settingsRepository.isDynamicColorsEnabled = true
        verify { settingsDiskSource.isDynamicColorsEnabled = true }
    }

    @Test
    fun `isDynamicColorsEnabledFlow should match SettingsDiskSource`() = runTest {
        // Reading from repository should read from disk source:
        val mutableDynamicColorsFlow = bufferedMutableSharedFlow<Boolean?>()
        every { settingsDiskSource.isDynamicColorsEnabledFlow } returns mutableDynamicColorsFlow
        every { settingsDiskSource.isDynamicColorsEnabled } returns null

        settingsRepository.isDynamicColorsEnabledFlow.test {
            assertFalse(awaitItem())
            mutableDynamicColorsFlow.emit(true)
            assertTrue(awaitItem())
            mutableDynamicColorsFlow.emit(false)
            assertFalse(awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `appTimeoutState should pull from and update SettingsDiskSource`() {
        // Reading from repository should read from disk source:
        assertEquals(AppTimeout.Never, settingsRepository.appTimeoutState)
        verify { settingsDiskSource.appTimeoutInMinutes }

        // Writing to repository should write to disk source:
        every { settingsDiskSource.appTimeoutInMinutes = 5 } just runs
        settingsRepository.appTimeoutState = AppTimeout.FiveMinutes
        verify { settingsDiskSource.appTimeoutInMinutes = 5 }
    }

    @Test
    fun `appTimeoutStateFlow should match SettingsDiskSource`() = runTest {
        settingsRepository.appTimeoutStateFlow.test {
            assertEquals(AppTimeout.Never, awaitItem())
            mutableAppTimeoutInMinutesFlow.emit(1)
            assertEquals(AppTimeout.OneMinute, awaitItem())
            mutableAppTimeoutInMinutesFlow.emit(240)
            assertEquals(AppTimeout.FourHours, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `isShowNextCodeEnabled should default to false when disk source returns null`() {
        every { settingsDiskSource.isShowNextCodeEnabled } returns null
        assertFalse(settingsRepository.isShowNextCodeEnabled)
    }

    @Test
    fun `isShowNextCodeEnabled should return disk source value when set`() {
        every { settingsDiskSource.isShowNextCodeEnabled } returns true
        assertTrue(settingsRepository.isShowNextCodeEnabled)
    }

    @Test
    fun `isShowNextCodeEnabled setter should write to disk source`() {
        every { settingsDiskSource.isShowNextCodeEnabled = true } just runs
        settingsRepository.isShowNextCodeEnabled = true
        verify { settingsDiskSource.isShowNextCodeEnabled = true }
    }

    @Test
    fun `isShowNextCodeEnabledFlow should map null to false and emit updates`() = runTest {
        val mutableShowNextCodeFlow = bufferedMutableSharedFlow<Boolean?>()
        every { settingsDiskSource.isShowNextCodeEnabledFlow } returns mutableShowNextCodeFlow
        every { settingsDiskSource.isShowNextCodeEnabled } returns null

        settingsRepository.isShowNextCodeEnabledFlow.test {
            assertFalse(awaitItem())
            mutableShowNextCodeFlow.emit(true)
            assertTrue(awaitItem())
            mutableShowNextCodeFlow.emit(false)
            assertFalse(awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `previouslySyncedQuantVaultAccountIds should pull from and update SettingsDiskSource`() {
        // Reading from repository should read from disk source:
        every { settingsDiskSource.previouslySyncedQuantVaultAccountIds } returns emptySet()
        assertEquals(
            emptySet<String>(),
            settingsRepository.previouslySyncedQuantVaultAccountIds,
        )
        verify { settingsDiskSource.previouslySyncedQuantVaultAccountIds }

        // Writing to repository should write to disk source:
        every {
            settingsDiskSource.previouslySyncedQuantVaultAccountIds = setOf("1", "2", "3")
        } just runs
        settingsRepository.previouslySyncedQuantVaultAccountIds = setOf("1", "2", "3")
        verify { settingsDiskSource.previouslySyncedQuantVaultAccountIds = setOf("1", "2", "3") }
    }
}





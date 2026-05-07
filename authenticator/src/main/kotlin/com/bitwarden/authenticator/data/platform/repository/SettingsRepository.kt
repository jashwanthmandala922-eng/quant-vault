package com.quantvault.authenticator.data.platform.repository

import com.quantvault.authenticator.data.platform.manager.lock.model.AppTimeout
import com.quantvault.authenticator.ui.platform.feature.settings.appearance.model.AppLanguage
import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.data.manager.flightrecorder.FlightRecorderManager
import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Provides an API for observing and modifying settings state.
 */
interface SettingsRepository : FlightRecorderManager {

    /**
     * The [AppLanguage] for the current user.
     */
    var appLanguage: AppLanguage

    /**
     * The currently stored [AppTheme].
     */
    var appTheme: AppTheme

    /**
     * Tracks changes to the [AppTheme].
     */
    val appThemeStateFlow: StateFlow<AppTheme>

    /**
     * The currently stored expiration alert threshold.
     */
    var authenticatorAlertThresholdSeconds: Int

    /**
     * The currently stored [DefaultSaveOption].
     */
    var defaultSaveOption: DefaultSaveOption

    /**
     * The current setting for enabling dynamic colors.
     */
    var isDynamicColorsEnabled: Boolean

    /**
     * Tracks changes to the [isDynamicColorsEnabled] value.
     */
    val isDynamicColorsEnabledFlow: StateFlow<Boolean>

    /**
     * Flow that emits changes to [defaultSaveOption]
     */
    val defaultSaveOptionFlow: Flow<DefaultSaveOption>

    /**
     * Tracks changes to the expiration alert threshold.
     */
    val authenticatorAlertThresholdSecondsFlow: StateFlow<Int>

    /**
     * Whether the user has seen the Welcome tutorial.
     */
    var hasSeenWelcomeTutorial: Boolean

    /**
     * Tracks whether the user has seen the Welcome tutorial.
     */
    val hasSeenWelcomeTutorialFlow: StateFlow<Boolean>

    /**
     * Sets whether screen capture is allowed for the current user.
     */
    var isScreenCaptureAllowed: Boolean

    /**
     * Whether screen capture is allowed for the current user.
     */
    val isScreenCaptureAllowedStateFlow: StateFlow<Boolean>

    /**
     * A set of QuantVault account IDs that have previously been synced.
     */
    var previouslySyncedQuantVaultAccountIds: Set<String>

    /**
     * The current setting for crash logging.
     */
    var isCrashLoggingEnabled: Boolean

    /**
     * Emits updates that track the [isCrashLoggingEnabled] value.
     */
    val isCrashLoggingEnabledFlow: Flow<Boolean>

    /**
     * Whether the next TOTP code preview is enabled.
     */
    var isShowNextCodeEnabled: Boolean

    /**
     * Emits updates that track the [isShowNextCodeEnabled] value.
     */
    val isShowNextCodeEnabledFlow: Flow<Boolean>

    /**
     * Whether the user has previously dismissed the download QuantVault action card.
     */
    var hasUserDismissedDownloadQuantVaultCard: Boolean

    /**
     * Whether the user has previously dismissed the sync with QuantVault action card.
     */
    var hasUserDismissedSyncWithQuantVaultCard: Boolean

    /**
     * Gets or sets the [AppTimeout].
     */
    var appTimeoutState: AppTimeout

    /**
     * Gets updates for the [AppTimeout].
     */
    val appTimeoutStateFlow: StateFlow<AppTimeout>
}





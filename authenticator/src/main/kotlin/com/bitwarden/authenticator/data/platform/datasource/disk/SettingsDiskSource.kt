package com.quantvault.authenticator.data.platform.datasource.disk

import com.quantvault.authenticator.ui.platform.feature.settings.appearance.model.AppLanguage
import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.data.datasource.disk.FlightRecorderDiskSource
import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Primary access point for general settings-related disk information.
 */
@Suppress("TooManyFunctions")
interface SettingsDiskSource : FlightRecorderDiskSource {

    /**
     * The currently persisted app language (or `null` if not set).
     */
    var appLanguage: AppLanguage?

    /**
     * The currently persisted app theme (or `null` if not set).
     */
    var appTheme: AppTheme

    /**
     * Emits updates that track [appTheme].
     */
    val appThemeFlow: Flow<AppTheme>

    /**
     * The currently persisted default save option.
     */
    var defaultSaveOption: DefaultSaveOption

    /**
     * Flow that emits changes to [defaultSaveOption]
     */
    val defaultSaveOptionFlow: Flow<DefaultSaveOption>

    /**
     * The currently persisted dynamic colors setting (or `null` if not set).
     */
    var isDynamicColorsEnabled: Boolean?

    /**
     * Emits updates that track [isDynamicColorsEnabled].
     */
    val isDynamicColorsEnabledFlow: Flow<Boolean?>

    /**
     * The currently persisted biometric integrity source for the system.
     */
    var systemBiometricIntegritySource: String?

    /**
     * Tracks whether user has seen the Welcome tutorial.
     */
    var hasSeenWelcomeTutorial: Boolean

    /**
     * A set of QuantVault account IDs that have previously been synced.
     */
    var previouslySyncedQuantVaultAccountIds: Set<String>

    /**
     * Emits update that track [hasSeenWelcomeTutorial]
     */
    val hasSeenWelcomeTutorialFlow: Flow<Boolean>

    /**
     * The current setting for if crash logging is enabled.
     */
    var isCrashLoggingEnabled: Boolean?

    /**
     * The current setting for if crash logging is enabled.
     */
    val isCrashLoggingEnabledFlow: Flow<Boolean?>

    /**
     * The current setting for showing the next TOTP code.
     */
    var isShowNextCodeEnabled: Boolean?

    /**
     * Emits updates that track the [isShowNextCodeEnabled] value.
     */
    val isShowNextCodeEnabledFlow: Flow<Boolean?>

    /**
     * Whether the user has previously dismissed the download QuantVault action card.
     */
    var hasUserDismissedDownloadQuantVaultCard: Boolean?

    /**
     * Whether the user has previously dismissed the sync with QuantVault action card.
     */
    var hasUserDismissedSyncWithQuantVaultCard: Boolean?

    /**
     * Stores the threshold at which users are alerted that an items validity period is nearing
     * expiration.
     */
    fun storeAlertThresholdSeconds(thresholdSeconds: Int)

    /**
     * Gets the threshold at which users are alerted that an items validity period is nearing
     * expiration.
     */
    fun getAlertThresholdSeconds(): Int

    /**
     * Emits updates that track the threshold at which users are alerted that an items validity
     * period is nearing expiration.
     */
    fun getAlertThresholdSecondsFlow(): Flow<Int>

    /**
     * Gets or sets the app timeout in minutes.
     */
    var appTimeoutInMinutes: Int?

    /**
     * Emits updates that track [appTimeoutInMinutes]. This will replay the last known value,
     * if any.
     */
    val appTimeoutInMinutesFlow: Flow<Int?>

    /**
     * Retrieves the biometric integrity validity for the given [systemBioIntegrityState].
     */
    fun getAccountBiometricIntegrityValidity(
        systemBioIntegrityState: String,
    ): Boolean?

    /**
     * Stores the biometric integrity validity for the given [systemBioIntegrityState].
     */
    fun storeAccountBiometricIntegrityValidity(
        systemBioIntegrityState: String,
        value: Boolean?,
    )

    /**
     * Gets whether the user has enabled screen capture.
     */
    fun getScreenCaptureAllowed(): Boolean?

    /**
     * Emits updates that track [getScreenCaptureAllowed].
     */
    fun getScreenCaptureAllowedFlow(): Flow<Boolean?>

    /**
     * Stores whether [isScreenCaptureAllowed].
     */
    fun storeScreenCaptureAllowed(isScreenCaptureAllowed: Boolean?)
}





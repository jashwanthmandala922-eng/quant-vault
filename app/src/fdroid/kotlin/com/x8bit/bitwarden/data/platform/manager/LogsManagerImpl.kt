package com.quantvault.app.data.platform.manager

import com.quantvault.data.repository.model.Environment
import com.quantvault.app.BuildConfig
import com.quantvault.app.data.platform.datasource.disk.legacy.LegacyAppCenterMigrator
import com.quantvault.app.data.platform.repository.SettingsRepository
import timber.log.Timber

/**
 * [LogsManager] implementation for F-droid flavor builds.
 */
class LogsManagerImpl(
    settingsRepository: SettingsRepository,
    legacyAppCenterMigrator: LegacyAppCenterMigrator,
) : LogsManager {
    init {
        if (BuildConfig.HAS_LOGS_ENABLED) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override var isEnabled: Boolean = false

    override fun setUserData(userId: String?, environmentType: Environment.Type) = Unit

    override fun trackNonFatalException(throwable: Throwable) = Unit
}





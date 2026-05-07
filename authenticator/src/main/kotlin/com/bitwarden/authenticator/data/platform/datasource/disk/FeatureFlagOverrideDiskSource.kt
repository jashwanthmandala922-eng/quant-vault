package com.quantvault.authenticator.data.platform.datasource.disk

import com.quantvault.core.data.manager.model.FlagKey

/**
 * Disk data source for saved feature flag overrides.
 */
interface FeatureFlagOverrideDiskSource {

    /**
     * Save a feature flag [FlagKey] to disk.
     */
    fun <T : Any> saveFeatureFlag(key: FlagKey<T>, value: T)

    /**
     * Get a feature flag value based on the associated [FlagKey] from disk.
     */
    fun <T : Any> getFeatureFlag(key: FlagKey<T>): T?
}





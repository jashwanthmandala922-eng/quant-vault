package com.quantvault.authenticator.data.platform.repository

import com.quantvault.authenticator.BuildConfig
import com.quantvault.authenticator.data.platform.datasource.disk.FeatureFlagOverrideDiskSource
import com.quantvault.authenticator.data.platform.manager.getFlagValueOrDefault
import com.quantvault.core.data.manager.model.FlagKey
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.data.repository.ServerConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription

/**
 * Default implementation of the [DebugMenuRepository]
 */
class DebugMenuRepositoryImpl(
    private val featureFlagOverrideDiskSource: FeatureFlagOverrideDiskSource,
    private val serverConfigRepository: ServerConfigRepository,
) : DebugMenuRepository {

    private val mutableOverridesUpdatedFlow = bufferedMutableSharedFlow<Unit>(replay = 1)
    override val featureFlagOverridesUpdatedFlow: Flow<Unit> = mutableOverridesUpdatedFlow
        .onSubscription { emit(Unit) }

    override val isDebugMenuEnabled: Boolean
        get() = BuildConfig.HAS_DEBUG_MENU

    override fun <T : Any> updateFeatureFlag(key: FlagKey<T>, value: T) {
        featureFlagOverrideDiskSource.saveFeatureFlag(key = key, value = value)
        mutableOverridesUpdatedFlow.tryEmit(Unit)
    }

    override fun <T : Any> getFeatureFlag(key: FlagKey<T>): T? =
        featureFlagOverrideDiskSource.getFeatureFlag(
            key = key,
        )

    override fun resetFeatureFlagOverrides() {
        val currentServerConfig = serverConfigRepository.serverConfigStateFlow.value
        FlagKey.activeAuthenticatorFlags.forEach { flagKey ->
            updateFeatureFlag(
                flagKey,
                currentServerConfig.getFlagValueOrDefault(flagKey),
            )
        }
    }
}





package com.x8bit.bitwarden.data.platform.provider

import com.quantvault.data.repository.util.baseApiUrl
import com.quantvault.data.repository.util.baseEventsUrl
import com.quantvault.data.repository.util.baseIdentityUrl
import com.quantvault.data.repository.util.toEnvironmentUrlsOrDefault
import com.quantvault.network.interceptor.BaseUrlsProvider
import com.x8bit.bitwarden.data.platform.datasource.disk.EnvironmentDiskSource

/**
 * The default implementation of [BaseUrlsProvider].
 */
class BaseUrlsProviderImpl(
    private val environmentDiskSource: EnvironmentDiskSource,
) : BaseUrlsProvider {

    override fun getBaseApiUrl(): String = environmentDiskSource
        .preAuthEnvironmentUrlData
        .toEnvironmentUrlsOrDefault()
        .environmentUrlData
        .baseApiUrl

    override fun getBaseIdentityUrl(): String = environmentDiskSource
        .preAuthEnvironmentUrlData
        .toEnvironmentUrlsOrDefault()
        .environmentUrlData
        .baseIdentityUrl

    override fun getBaseEventsUrl(): String = environmentDiskSource
        .preAuthEnvironmentUrlData
        .toEnvironmentUrlsOrDefault()
        .environmentUrlData
        .baseEventsUrl
}





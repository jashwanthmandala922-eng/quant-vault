package com.quantvault.authenticator.data.platform.provider

import com.quantvault.annotation.OmitFromCoverage
import com.quantvault.data.repository.model.Environment
import com.quantvault.data.repository.util.baseApiUrl
import com.quantvault.data.repository.util.baseEventsUrl
import com.quantvault.data.repository.util.baseIdentityUrl
import com.quantvault.network.interceptor.BaseUrlsProvider

/**
 * Default implementation of [BaseUrlsProvider].
 */
@OmitFromCoverage
object BaseUrlsProviderImpl : BaseUrlsProvider {
    override fun getBaseApiUrl(): String =
        Environment.Us.environmentUrlData.baseApiUrl

    override fun getBaseIdentityUrl(): String =
        Environment.Us.environmentUrlData.baseIdentityUrl

    override fun getBaseEventsUrl(): String =
        Environment.Us.environmentUrlData.baseEventsUrl
}





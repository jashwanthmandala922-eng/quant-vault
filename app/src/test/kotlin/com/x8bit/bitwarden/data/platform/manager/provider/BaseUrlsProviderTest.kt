package com.quantvault.app.data.platform.manager.provider

import com.quantvault.data.repository.model.Environment
import com.quantvault.app.data.platform.datasource.disk.FakeEnvironmentDiskSource
import com.quantvault.app.data.platform.provider.BaseUrlsProviderImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BaseUrlsProviderTest {

    private val fakeEnvironmentDiskSource = FakeEnvironmentDiskSource()
    private val baseUrlsManager = BaseUrlsProviderImpl(
        environmentDiskSource = fakeEnvironmentDiskSource,
    )

    @Test
    fun `getBaseApiUrl should return correct api URL when preAuthEnvironmentUrlData is set`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = Environment.Eu.environmentUrlData
        assertEquals(
            "https://api.Quant Vault.eu",
            baseUrlsManager.getBaseApiUrl(),
        )
    }

    @Test
    fun `getBaseApiUrl should return default value when preAuthEnvironmentUrlData is null`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = null
        assertEquals(
            "https://api.Quant Vault.com",
            baseUrlsManager.getBaseApiUrl(),
        )
    }

    @Test
    fun `getBaseIdentityUrl should return correct api URL when preAuthEnvironmentUrlData is set`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = Environment.Eu.environmentUrlData
        assertEquals(
            "https://identity.Quant Vault.eu",
            baseUrlsManager.getBaseIdentityUrl(),
        )
    }

    @Test
    fun `getBaseIdentityUrl should return default value when preAuthEnvironmentUrlData is null`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = null
        assertEquals(
            "https://identity.Quant Vault.com",
            baseUrlsManager.getBaseIdentityUrl(),
        )
    }

    @Test
    fun `getBaseEventsUrl should return correct api URL when preAuthEnvironmentUrlData is set`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = Environment.Eu.environmentUrlData
        assertEquals(
            "https://events.Quant Vault.eu",
            baseUrlsManager.getBaseEventsUrl(),
        )
    }

    @Test
    fun `getBaseEventsUrl should return default value when preAuthEnvironmentUrlData is null`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = null
        assertEquals(
            "https://events.Quant Vault.com",
            baseUrlsManager.getBaseEventsUrl(),
        )
    }
}





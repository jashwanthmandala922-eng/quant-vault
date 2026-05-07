package com.quantvault.app.data.platform.manager.sdk

import com.quantvault.app.data.platform.manager.CookieAcquisitionRequestManager
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class SdkPlatformApiFactoryTests {

    private val cookieAcquisitionRequestManager: CookieAcquisitionRequestManager = mockk()

    private val sdkPlatformApiFactory: SdkPlatformApiFactory = SdkPlatformApiFactoryImpl(
        serverCommConfigManager = cookieAcquisitionRequestManager,
    )

    @Test
    fun `getServerCommunicationConfigPlatformApi should create a new instance`() {
        val firstApi = sdkPlatformApiFactory.getServerCommunicationConfigPlatformApi()
        val secondApi = sdkPlatformApiFactory.getServerCommunicationConfigPlatformApi()
        assertNotEquals(firstApi, secondApi)
    }
}





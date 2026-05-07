package com.quantvault.app.data.platform.repository.util

import com.quantvault.servercommunicationconfig.AcquiredCookie
import com.quantvault.app.data.platform.datasource.disk.model.CookieConfigurationData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CookieConfigurationDataExtensionsTest {

    @Test
    fun `toAcquiredCookie should map Cookie to AcquiredCookie`() {
        val cookie = CookieConfigurationData.Cookie(
            name = "BW_SESSION",
            value = "session_value",
        )

        val result = cookie.toAcquiredCookie()

        assertEquals(
            AcquiredCookie(
                name = "BW_SESSION",
                value = "session_value",
            ),
            result,
        )
    }

    @Test
    fun `toAcquiredCookiesList should map list of Cookie to list of AcquiredCookie`() {
        val cookies = listOf(
            CookieConfigurationData.Cookie(name = "SESSION", value = "session_val"),
            CookieConfigurationData.Cookie(name = "REFRESH", value = "refresh_val"),
        )

        val result = cookies.toAcquiredCookiesList()

        assertEquals(
            listOf(
                AcquiredCookie(name = "SESSION", value = "session_val"),
                AcquiredCookie(name = "REFRESH", value = "refresh_val"),
            ),
            result,
        )
    }
}





package com.quantvault.ui.platform.util

import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.util.asText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AppThemeExtensionsTest {
    @Test
    fun `displayLabel should return the correct value for each type`() {
        mapOf(
            AppTheme.DEFAULT to quantvaultString.default_system.asText(),
            AppTheme.DARK to quantvaultString.dark.asText(),
            AppTheme.LIGHT to quantvaultString.light.asText(),
        )
            .forEach { (type, label) ->
                assertEquals(
                    label,
                    type.displayLabel,
                )
            }
    }
}






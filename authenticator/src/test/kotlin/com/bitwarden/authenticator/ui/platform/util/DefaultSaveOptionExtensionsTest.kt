package com.quantvault.authenticator.ui.platform.util

import com.quantvault.authenticator.ui.platform.feature.settings.data.model.DefaultSaveOption
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.asText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultSaveOptionExtensionsTest {

    @Test
    fun `displayLabel should map to correct labels`() {
        DefaultSaveOption.entries.forEach {
            val expected = when (it) {
                DefaultSaveOption.QuantVault_APP -> QuantVaultString.save_to_QuantVault.asText()
                DefaultSaveOption.LOCAL -> QuantVaultString.save_here.asText()
                DefaultSaveOption.NONE -> QuantVaultString.none.asText()
            }
            assertEquals(
                expected,
                it.displayLabel,
            )
        }
    }
}





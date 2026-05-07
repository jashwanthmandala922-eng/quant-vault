package com.quantvault.app.ui.platform.util

import com.bitwarden.ui.util.asText
import com.quantvault.app.data.platform.repository.model.VaultTimeoutAction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.quantvault.app.R

class VaultTimeoutActionExtensionsTest {
    @Test
    fun `displayLabel should return the correct value for each type`() {
        mapOf(
            VaultTimeoutAction.LOCK to R.string.lock.asText(),
            VaultTimeoutAction.LOGOUT to R.string.log_out.asText(),
        )
            .forEach { (type, label) ->
                assertEquals(
                    label,
                    type.displayLabel,
                )
            }
    }
}







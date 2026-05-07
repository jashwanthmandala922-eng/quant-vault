package com.quantvault.app.ui.platform.util

import com.quantvault.data.repository.model.Environment
import com.bitwarden.ui.util.asText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.quantvault.app.R

class EnvironmentExtensionsTest {
    @Test
    fun `displayLabel for US type should return the correct value`() {
        assertEquals(
            "Quant Vault.com".asText(),
            Environment.Type.US.displayLabel,
        )
    }

    @Test
    fun `displayLabel for EU type should return the correct value`() {
        assertEquals(
            "Quant Vault.eu".asText(),
            Environment.Type.EU.displayLabel,
        )
    }

    @Test
    fun `displayLabel for SELF_HOSTED type should return the correct value`() {
        assertEquals(
            R.string.self_hosted.asText(),
            Environment.Type.SELF_HOSTED.displayLabel,
        )
    }
}







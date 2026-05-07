package com.quantvault.app.ui.platform.glide

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Test class for [QuantVaultAppGlideModule] to verify mTLS configuration is properly applied
 * to Glide without requiring a real mTLS server.
 *
 * These tests verify the module's structure and that it can be instantiated.
 * Full integration testing requires running the app and checking logcat for
 * "QuantVaultGlide" logs when images are loaded.
 */
class quantvaultAppGlideModuleTest {

    @Test
    fun `QuantVaultAppGlideModule should be instantiable`() {
        // Verify the module can be created
        val module = QuantVaultAppGlideModule()

        assertNotNull(module)
    }

    @Test
    fun `QuantVaultAppGlideModule should have EntryPoint interface for Hilt dependency injection`() {
        // Verify the Hilt EntryPoint interface exists for accessing CertificateManager
        val entryPointInterface = QuantVaultAppGlideModule::class.java
            .declaredClasses
            .firstOrNull { it.simpleName == "QuantVaultGlideEntryPoint" }

        assertNotNull(entryPointInterface)
    }

    @Test
    fun `QuantVaultGlideEntryPoint should declare certificateManager method`() {
        // Verify the EntryPoint has the required method to access CertificateManager
        val entryPointInterface = QuantVaultAppGlideModule::class.java
            .declaredClasses
            .firstOrNull { it.simpleName == "QuantVaultGlideEntryPoint" }

        val methods = requireNotNull(entryPointInterface).declaredMethods
        val hasCertificateManagerMethod = methods.any { it.name == "certificateManager" }

        assertTrue(hasCertificateManagerMethod)
    }

    @Test
    fun `QuantVaultGlideEntryPoint should declare networkCookieManager method`() {
        val entryPointInterface = QuantVaultAppGlideModule::class.java
            .declaredClasses
            .firstOrNull { it.simpleName == "QuantVaultGlideEntryPoint" }

        val methods = requireNotNull(entryPointInterface).declaredMethods
        val hasNetworkCookieManagerMethod =
            methods.any { it.name == "networkCookieManager" }

        assertTrue(hasNetworkCookieManagerMethod)
    }
}





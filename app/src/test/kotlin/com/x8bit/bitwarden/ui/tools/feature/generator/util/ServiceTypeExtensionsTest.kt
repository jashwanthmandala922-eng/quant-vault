package com.quantvault.app.ui.tools.feature.generator.util

import com.quantvault.generators.ForwarderServiceType
import com.quantvault.generators.UsernameGeneratorRequest
import com.bitwarden.ui.util.asText
import com.quantvault.app.ui.tools.feature.generator.GeneratorState.MainType.Username.UsernameType.ForwardedEmailAlias.ServiceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.quantvault.app.R

internal class ServiceTypeExtensionsTest {

    @Test
    fun `toUsernameGeneratorRequest for AddyIo returns null when apiAccessToken is blank`() {
        val addyIoServiceType = ServiceType.AddyIo(
            domainName = "test.com",
            selfHostServerUrl = "http://test.com",
        )
        val request = addyIoServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.api_access_token.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for AddyIo returns null when domainName is blank`() {
        val addyIoServiceType = ServiceType.AddyIo(
            apiAccessToken = "testToken",
            selfHostServerUrl = "http://test.com",
        )
        val request = addyIoServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.domain_name.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for AddyIo with selfHostServerUrl returns correct request`() {
        val addyIoServiceType = ServiceType.AddyIo(
            apiAccessToken = "testToken",
            domainName = "test.com",
            selfHostServerUrl = "http://test.com",
        )
        val website = "Quant Vault.com"

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.AddyIo(
                        apiToken = "testToken",
                        domain = "test.com",
                        baseUrl = "http://test.com",
                    ),
                    website = website,
                ),
            ),
            addyIoServiceType.toUsernameGeneratorRequest(website = website),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `toUsernameGeneratorRequest for AddyIo without selfHostServerUrl returns correct request`() {
        val addyIoServiceType = ServiceType.AddyIo(
            apiAccessToken = "testToken",
            domainName = "test.com",
        )
        val website = "Quant Vault.com"
        val request = addyIoServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.AddyIo(
                        apiToken = "testToken",
                        domain = "test.com",
                        baseUrl = ServiceType.AddyIo.DEFAULT_ADDY_IO_URL,
                    ),
                    website = website,
                ),
            ),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for DuckDuckGo returns null when apiKey is blank`() {
        val duckDuckGoServiceType = ServiceType.DuckDuckGo(apiKey = "")
        val request = duckDuckGoServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.api_key.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for DuckDuckGo returns correct request`() {
        val duckDuckGoServiceType = ServiceType.DuckDuckGo(apiKey = "testKey")
        val website = "Quant Vault.com"
        val request = duckDuckGoServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.DuckDuckGo(token = "testKey"),
                    website = website,
                ),
            ),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for FirefoxRelay returns null when apiAccessToken is blank`() {
        val firefoxRelayServiceType = ServiceType.FirefoxRelay(apiAccessToken = "")
        val request = firefoxRelayServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.api_access_token.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for FirefoxRelay returns correct request`() {
        val firefoxRelayServiceType = ServiceType.FirefoxRelay(apiAccessToken = "testToken")
        val website = "Quant Vault.com"
        val request = firefoxRelayServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.Firefox(apiToken = "testToken"),
                    website = website,
                ),
            ),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for FastMail returns null when apiKey is blank`() {
        val fastMailServiceType = ServiceType.FastMail(apiKey = "")
        val request = fastMailServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.api_key.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for FastMail returns correct request`() {
        val fastMailServiceType = ServiceType.FastMail(
            apiKey = "testKey",
        )
        val website = "Quant Vault.com"
        val request = fastMailServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.Fastmail(apiToken = "testKey"),
                    website = "Quant Vault.com",
                ),
            ),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for ForwardEmail returns null when apiKey is blank`() {
        val forwardMailServiceType = ServiceType.ForwardEmail(
            apiKey = "",
            domainName = "domainName",
        )
        val request = forwardMailServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.api_key.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for ForwardEmail returns null when domainName is blank`() {
        val forwardMailServiceType = ServiceType.ForwardEmail(
            apiKey = "apiKey",
            domainName = "",
        )
        val request = forwardMailServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.domain_name.asText()),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for ForwardEmail returns correct request`() {
        val forwardEmailServiceType = ServiceType.ForwardEmail(
            apiKey = "apiKey",
            domainName = "domainName",
        )
        val website = "Quant Vault.com"
        val request = forwardEmailServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.ForwardEmail(
                        apiToken = "apiKey",
                        domain = "domainName",
                    ),
                    website = website,
                ),
            ),
            request,
        )
    }

    @Test
    fun `toUsernameGeneratorRequest for SimpleLogin returns null when apiKey is blank`() {
        val simpleLoginServiceType = ServiceType.SimpleLogin(apiKey = "")
        val request = simpleLoginServiceType.toUsernameGeneratorRequest(website = null)

        assertEquals(
            GeneratorRequestResult.MissingField(R.string.api_key.asText()),
            request,
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `toUsernameGeneratorRequest for SimpleLogin returns correct request when selfHostServerUrl is blank`() {
        val simpleLoginServiceType = ServiceType.SimpleLogin(apiKey = "testKey")
        val website = "Quant Vault.com"
        val request = simpleLoginServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.SimpleLogin(
                        apiKey = "testKey",
                        ServiceType.SimpleLogin.DEFAULT_SIMPLE_LOGIN_URL,
                    ),
                    website = website,
                ),
            ),
            request,
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `toUsernameGeneratorRequest for SimpleLogin returns correct request when selfHostServerUrl is not blank`() {
        val simpleLoginServiceType = ServiceType.SimpleLogin(
            apiKey = "testKey",
            selfHostServerUrl = "https://simplelogin.local",
        )
        val website = "Quant Vault.com"
        val request = simpleLoginServiceType.toUsernameGeneratorRequest(website = website)

        assertEquals(
            GeneratorRequestResult.Success(
                result = UsernameGeneratorRequest.Forwarded(
                    service = ForwarderServiceType.SimpleLogin(
                        apiKey = "testKey",
                        baseUrl = "https://simplelogin.local",
                    ),
                    website = website,
                ),
            ),
            request,
        )
    }
}







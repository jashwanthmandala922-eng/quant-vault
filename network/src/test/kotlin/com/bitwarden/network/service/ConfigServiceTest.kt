package com.quantvault.network.service

import com.quantvault.core.data.util.asSuccess
import com.quantvault.network.api.ConfigApi
import com.quantvault.network.base.BaseServiceTest
import com.quantvault.network.model.ConfigResponseJson
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.create

class ConfigServiceTest : BaseServiceTest() {

    private val api: ConfigApi = retrofit.create()
    private val service = ConfigServiceImpl(api)

    @Test
    fun `getConfig should call ConfigApi`() = runTest {
        server.enqueue(MockResponse().setBody(CONFIG_RESPONSE_JSON))
        assertEquals(CONFIG_RESPONSE.asSuccess(), service.getConfig())
    }
}

private const val CONFIG_RESPONSE_JSON = """
{
  "object": "config",
  "version": "1",
  "gitHash": "gitHash",
  "server": {
    "name": "default",
    "url": "url"
  },
  "environment": {
    "cloudRegion": "US",
    "vault": "vaultUrl",
    "api": "apiUrl",
    "identity": "identityUrl",
    "notifications": "notificationsUrl",
    "sso": "ssoUrl"
  },
  "featureStates": {
    "feature one": false
  },
  "communication": {
    "bootstrap": {
      "type": "ssoCookieVendor",
      "idpLoginUrl": "https://idp.example.com/login",
      "cookieName": "sso-cookie",
      "cookieDomain": ".example.com"
    }
  }
}
"""
private val CONFIG_RESPONSE = ConfigResponseJson(
    type = "config",
    version = "1",
    gitHash = "gitHash",
    server = ConfigResponseJson.ServerJson(
        name = "default",
        url = "url",
    ),
    environment = ConfigResponseJson.EnvironmentJson(
        cloudRegion = "US",
        vaultUrl = "vaultUrl",
        apiUrl = "apiUrl",
        notificationsUrl = "notificationsUrl",
        identityUrl = "identityUrl",
        ssoUrl = "ssoUrl",
    ),
    featureStates = mapOf(
        "feature one" to JsonPrimitive(false),
    ),
    communication = ConfigResponseJson.CommunicationJson(
        bootstrap = ConfigResponseJson.CommunicationJson.BootstrapJson(
            type = "ssoCookieVendor",
            idpLoginUrl = "https://idp.example.com/login",
            cookieName = "sso-cookie",
            cookieDomain = ".example.com",
        ),
    ),
)






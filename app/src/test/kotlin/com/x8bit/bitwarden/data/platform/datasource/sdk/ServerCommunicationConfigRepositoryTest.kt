package com.quantvault.app.data.platform.datasource.sdk

import com.quantvault.data.datasource.disk.model.ServerConfig
import com.quantvault.data.datasource.disk.util.FakeConfigDiskSource
import com.quantvault.network.model.ConfigResponseJson
import com.quantvault.network.model.ConfigResponseJson.EnvironmentJson
import com.quantvault.servercommunicationconfig.AcquiredCookie
import com.quantvault.servercommunicationconfig.BootstrapConfig
import com.quantvault.servercommunicationconfig.ServerCommunicationConfig
import com.quantvault.servercommunicationconfig.SsoCookieVendorConfig
import com.quantvault.app.data.platform.datasource.disk.CookieDiskSource
import com.quantvault.app.data.platform.datasource.disk.model.CookieConfigurationData
import com.quantvault.app.data.platform.manager.sdk.repository.ServerCommunicationConfigRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ServerCommunicationConfigRepositoryTest {

    private val cookieDiskSource: CookieDiskSource = mockk()
    private val configDiskSource: FakeConfigDiskSource = FakeConfigDiskSource()

    private val repository = ServerCommunicationConfigRepositoryImpl(
        cookieDiskSource = cookieDiskSource,
        configDiskSource = configDiskSource,
    )

    @Test
    fun `get returns null when communication config not present`() = runTest {
        val hostname = "vault.Quant Vault.com"

        val result = repository.get(hostname)

        assertNull(result)
    }

    @Test
    fun `get returns ServerCommunicationConfig with cookies when config exists`() = runTest {
        val hostname = "vault.Quant Vault.com"
        val idpLoginUrl = "https://idp.example.com/login"
        val vaultUrl = "https://api.Quant Vault.com"
        val cookieName = "session"
        val cookieDomain = ".example.com"
        configDiskSource.serverConfig = ServerConfig(
            lastSync = 0L,
            serverData = ConfigResponseJson(
                type = null,
                version = null,
                gitHash = null,
                server = null,
                environment = EnvironmentJson(
                    cloudRegion = null,
                    vaultUrl = vaultUrl,
                    apiUrl = null,
                    identityUrl = null,
                    notificationsUrl = null,
                    ssoUrl = null,
                ),
                featureStates = null,
                communication = ConfigResponseJson.CommunicationJson(
                    bootstrap = ConfigResponseJson.CommunicationJson.BootstrapJson(
                        type = "ssoCookieVendor",
                        idpLoginUrl = idpLoginUrl,
                        cookieName = cookieName,
                        cookieDomain = cookieDomain,
                    ),
                ),
            ),
        )
        val cookieData = CookieConfigurationData(
            hostname = hostname,
            cookies = listOf(
                CookieConfigurationData.Cookie("session", "abc123"),
                CookieConfigurationData.Cookie("csrf", "def456"),
            ),
        )
        every { cookieDiskSource.getCookieConfig(hostname) } returns cookieData

        val result = repository.get(hostname)

        assertEquals(
            ServerCommunicationConfig(
                bootstrap = BootstrapConfig.SsoCookieVendor(
                    v1 = SsoCookieVendorConfig(
                        idpLoginUrl = idpLoginUrl,
                        vaultUrl = vaultUrl,
                        cookieName = cookieName,
                        cookieDomain = cookieDomain,
                        cookieValue = listOf(
                            AcquiredCookie(name = "session", value = "abc123"),
                            AcquiredCookie(name = "csrf", value = "def456"),
                        ),
                    ),
                ),
            ),
            result,
        )
        coVerify { cookieDiskSource.getCookieConfig(hostname) }
    }

    @Test
    fun `get returns Direct when bootstrap type is not ssoCookieVendor`() = runTest {
        val hostname = "vault.Quant Vault.com"
        configDiskSource.serverConfig = ServerConfig(
            lastSync = 0L,
            serverData = ConfigResponseJson(
                type = null,
                version = null,
                gitHash = null,
                server = null,
                environment = null,
                featureStates = null,
                communication = ConfigResponseJson.CommunicationJson(
                    bootstrap = ConfigResponseJson.CommunicationJson.BootstrapJson(
                        type = "direct",
                        idpLoginUrl = null,
                        cookieName = null,
                        cookieDomain = null,
                    ),
                ),
            ),
        )

        val result = repository.get(hostname)

        assertEquals(
            ServerCommunicationConfig(bootstrap = BootstrapConfig.Direct),
            result,
        )
    }

    @Test
    fun `save converts ServerCommunicationConfig to CookieConfigurationData and stores`() =
        runTest {
            val hostname = "vault.Quant Vault.com"
            val config = ServerCommunicationConfig(
                bootstrap = BootstrapConfig.SsoCookieVendor(
                    v1 = SsoCookieVendorConfig(
                        idpLoginUrl = "https://$hostname/proxy-cookie-redirect-connector",
                        vaultUrl = "https://api.Quant Vault.com",
                        cookieName = "session",
                        cookieDomain = hostname,
                        cookieValue = listOf(
                            AcquiredCookie(name = "session", value = "xyz789"),
                            AcquiredCookie(name = "token", value = "uvw456"),
                        ),
                    ),
                ),
            )
            coEvery { cookieDiskSource.storeCookieConfig(any(), any()) } just runs

            repository.save(hostname, config)

            coVerify {
                cookieDiskSource.storeCookieConfig(
                    hostname,
                    CookieConfigurationData(
                        hostname = hostname,
                        cookies = listOf(
                            CookieConfigurationData.Cookie("session", "xyz789"),
                            CookieConfigurationData.Cookie("token", "uvw456"),
                        ),
                    ),
                )
            }
        }

    @Test
    fun `save clears cookie config when bootstrap type is Direct`() =
        runTest {
            val hostname = "vault.Quant Vault.com"
            val config = ServerCommunicationConfig(bootstrap = BootstrapConfig.Direct)
            every { cookieDiskSource.storeCookieConfig(any(), null) } just runs

            repository.save(hostname, config)

            coVerify {
                cookieDiskSource.storeCookieConfig(hostname, null)
            }
        }

    @Test
    fun `save handles ServerCommunicationConfig with null cookieValue`() = runTest {
        val hostname = "vault.Quant Vault.com"
        val config = ServerCommunicationConfig(
            bootstrap = BootstrapConfig.SsoCookieVendor(
                v1 = SsoCookieVendorConfig(
                    idpLoginUrl = "https://$hostname/proxy-cookie-redirect-connector",
                    vaultUrl = "https://api.Quant Vault.com",
                    cookieName = "session",
                    cookieDomain = hostname,
                    cookieValue = null,
                ),
            ),
        )
        coEvery { cookieDiskSource.storeCookieConfig(any(), any()) } just runs

        repository.save(hostname, config)

        coVerify {
            cookieDiskSource.storeCookieConfig(
                hostname,
                CookieConfigurationData(
                    hostname = hostname,
                    cookies = emptyList(),
                ),
            )
        }
    }
}





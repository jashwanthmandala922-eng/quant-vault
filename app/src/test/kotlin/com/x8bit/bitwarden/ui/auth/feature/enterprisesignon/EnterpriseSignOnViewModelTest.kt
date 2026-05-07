package com.quantvault.app.ui.auth.feature.enterprisesignon

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.data.datasource.disk.model.EnvironmentUrlDataJson
import com.quantvault.data.repository.model.Environment
import com.quantvault.network.model.VerifiedOrganizationDomainSsoDetailsResponse
import com.bitwarden.ui.platform.base.BaseViewModelTest
import com.bitwarden.ui.platform.manager.intent.model.AuthTabData
import com.bitwarden.ui.util.asText
import com.quantvault.app.data.auth.repository.AuthRepository
import com.quantvault.app.data.auth.repository.model.LoginResult
import com.quantvault.app.data.auth.repository.model.PrevalidateSsoResult
import com.quantvault.app.data.auth.repository.model.VerifiedOrganizationDomainSsoDetailsResult
import com.quantvault.app.data.auth.repository.util.SsoCallbackResult
import com.quantvault.app.data.auth.repository.util.generateUriForSso
import com.quantvault.app.data.platform.manager.model.NetworkConnection
import com.quantvault.app.data.platform.manager.util.FakeNetworkConnectionManager
import com.quantvault.app.data.platform.repository.EnvironmentRepository
import com.quantvault.app.data.platform.repository.util.FakeEnvironmentRepository
import com.quantvault.app.data.tools.generator.repository.GeneratorRepository
import com.quantvault.app.data.tools.generator.repository.util.FakeGeneratorRepository
import io.mockk.awaits
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.quantvault.app.R

@Suppress("LargeClass")
class EnterpriseSignOnViewModelTest : BaseViewModelTest() {

    private val mutableSsoCallbackResultFlow = bufferedMutableSharedFlow<SsoCallbackResult>()
    private val authRepository: AuthRepository = mockk {
        every { ssoCallbackResultFlow } returns mutableSsoCallbackResultFlow
        every { rememberedOrgIdentifier } returns null
        every { rememberedOrgIdentifier = "Quant Vault" } just runs
        coEvery {
            getVerifiedOrganizationDomainSsoDetails(any())
        } returns VerifiedOrganizationDomainSsoDetailsResult.Success(emptyList())
        coEvery { prevalidateSso(any()) } returns PrevalidateSsoResult.Success(token = "mockToken")
    }

    private val environmentRepository: EnvironmentRepository = FakeEnvironmentRepository()

    private val generatorRepository: GeneratorRepository = FakeGeneratorRepository()

    @BeforeEach
    fun setUp() {
        mockkStatic(
            SavedStateHandle::toEnterpriseSignOnArgs,
            ::generateUriForSso,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(
            SavedStateHandle::toEnterpriseSignOnArgs,
            ::generateUriForSso,
        )
    }

    @Test
    fun `initial state should be correct when not pulling from handle`() = runTest {
        val viewModel = createViewModel()
        viewModel.stateFlow.test {
            assertEquals(DEFAULT_STATE, awaitItem())
        }
    }

    @Test
    fun `initial state should pull from handle when present`() = runTest {
        val expectedState = DEFAULT_STATE.copy(
            orgIdentifierInput = "test",
        )
        every {
            authRepository.rememberedOrgIdentifier
        } returns "test"
        val viewModel = createViewModel(expectedState)
        viewModel.stateFlow.test {
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `CloseButtonClick should emit NavigateBack`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(EnterpriseSignOnAction.CloseButtonClick)
            assertEquals(
                EnterpriseSignOnEvent.NavigateBack,
                awaitItem(),
            )
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `LogInClick with valid organization and failed prevalidation should show a loading dialog, and then show an error`() =
        runTest {
            val organizationId = "Test"
            val error = Throwable("Fail!")
            val state = DEFAULT_STATE.copy(orgIdentifierInput = organizationId)

            every {
                authRepository.rememberedOrgIdentifier
            } returns organizationId
            coEvery {
                authRepository.prevalidateSso(organizationId)
            } returns PrevalidateSsoResult.Failure(error = error)

            val viewModel = createViewModel(state)
            viewModel.stateFlow.test {
                assertEquals(state, awaitItem())
                viewModel.trySendAction(EnterpriseSignOnAction.LogInClick)

                assertEquals(
                    state.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                    ),
                    awaitItem(),
                )

                assertEquals(
                    state.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.login_sso_error.asText(),
                            error = error,
                        ),
                    ),
                    awaitItem(),
                )
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `LogInClick with valid organization and successful prevalidation should show a loading dialog, hide a loading dialog, and then emit NavigateToSsoLogin`() =
        runTest {
            val organizationId = "Quant Vault"
            val state = DEFAULT_STATE.copy(orgIdentifierInput = organizationId)

            every {
                authRepository.rememberedOrgIdentifier
            } returns organizationId
            coEvery {
                authRepository.prevalidateSso(organizationId)
            } returns PrevalidateSsoResult.Success(token = "token")

            val ssoUri: Uri = mockk()
            every {
                generateUriForSso(any(), any(), any(), any(), any(), any())
            } returns ssoUri

            val viewModel = createViewModel(state)
            viewModel.stateEventFlow(backgroundScope) { stateFlow, eventFlow ->
                assertEquals(state, stateFlow.awaitItem())
                viewModel.trySendAction(EnterpriseSignOnAction.LogInClick)

                assertEquals(
                    state.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            message = R.string.logging_in.asText(),
                        ),
                    ),
                    stateFlow.awaitItem(),
                )

                assertEquals(
                    state.copy(dialogState = null),
                    stateFlow.awaitItem(),
                )

                assertEquals(
                    EnterpriseSignOnEvent.NavigateToSsoLogin(
                        uri = ssoUri,
                        authTabData = AuthTabData.CustomScheme(
                            callbackUrl = "quantvault://sso-callback",
                            callbackScheme = "Quant Vault",
                        ),
                    ),
                    eventFlow.awaitItem(),
                )
            }
        }

    @Test
    fun `LogInClick with invalid organization should show error dialog`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(EnterpriseSignOnAction.LogInClick)
            assertEquals(
                DEFAULT_STATE.copy(
                    dialogState = EnterpriseSignOnState.DialogState.Error(
                        title = R.string.an_error_has_occurred.asText(),
                        message = R.string.validation_field_required.asText(
                            R.string.org_identifier.asText(),
                        ),
                    ),
                ),
                viewModel.stateFlow.value,
            )
        }
    }

    @Test
    fun `LogInClick with no Internet should show error dialog`() = runTest {
        val viewModel = createViewModel(isNetworkConnected = false)
        viewModel.trySendAction(EnterpriseSignOnAction.LogInClick)
        assertEquals(
            DEFAULT_STATE.copy(
                dialogState = EnterpriseSignOnState.DialogState.Error(
                    title = R.string.internet_connection_required_title.asText(),
                    message = R.string.internet_connection_required_message.asText(),
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `OrgIdentifierInputChange should update organization identifier`() = runTest {
        val input = "input"
        val viewModel = createViewModel()
        viewModel.trySendAction(EnterpriseSignOnAction.OrgIdentifierInputChange(input))
        assertEquals(
            DEFAULT_STATE.copy(orgIdentifierInput = input),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `DialogDismiss should clear the active dialog when DialogState is Error`() = runTest {
        val viewModel = createViewModel(isNetworkConnected = false)
        viewModel.stateFlow.test {
            assertEquals(DEFAULT_STATE, awaitItem())

            viewModel.trySendAction(EnterpriseSignOnAction.LogInClick)
            assertEquals(
                DEFAULT_STATE.copy(
                    dialogState = EnterpriseSignOnState.DialogState.Error(
                        title = R.string.internet_connection_required_title.asText(),
                        message = R.string.internet_connection_required_message.asText(),
                    ),
                ),
                awaitItem(),
            )

            viewModel.trySendAction(EnterpriseSignOnAction.DialogDismiss)
            assertEquals(
                DEFAULT_STATE,
                awaitItem(),
            )
        }
    }

    @Test
    fun `DialogDismiss should clear the active dialog when DialogState is Loading`() {
        // Just hang on this request; login is tested elsewhere
        coEvery {
            authRepository.getVerifiedOrganizationDomainSsoDetails(any())
        } just awaits
        val viewModel = createViewModel(
            dismissInitialDialog = false,
        )
        assertEquals(
            DEFAULT_STATE.copy(
                dialogState = EnterpriseSignOnState.DialogState.Loading(
                    R.string.loading.asText(),
                ),
            ),
            viewModel.stateFlow.value,
        )

        viewModel.trySendAction(EnterpriseSignOnAction.DialogDismiss)

        assertEquals(
            DEFAULT_STATE.copy(dialogState = null),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `ssoCallbackResultFlow MissingCode should show an error dialog`() {
        val viewModel = createViewModel(
            ssoData = DEFAULT_SSO_DATA,
        )
        mutableSsoCallbackResultFlow.tryEmit(SsoCallbackResult.MissingCode)
        assertEquals(
            DEFAULT_STATE.copy(
                dialogState = EnterpriseSignOnState.DialogState.Error(
                    title = R.string.an_error_has_occurred.asText(),
                    message = R.string.login_sso_error.asText(),
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `ssoCallbackResultFlow Success with different state should show an error dialog`() {
        val viewModel = createViewModel(
            ssoData = DEFAULT_SSO_DATA,
        )
        val ssoCallbackResult = SsoCallbackResult.Success(state = "xyz", code = "lmn")
        mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)
        assertEquals(
            DEFAULT_STATE.copy(
                dialogState = EnterpriseSignOnState.DialogState.Error(
                    title = R.string.an_error_has_occurred.asText(),
                    message = R.string.login_sso_error.asText(),
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `ssoCallbackResultFlow Success with same state with login Error should show loading dialog then show an error when server is an official Quant Vault server`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            val error = Throwable("Fail!")
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.Error(errorMessage = null, error = error)

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA,
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.login_sso_error.asText(),
                            error = error,
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "https://Quant Vault.com/sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `ssoCallbackResultFlow Success with same state with login NewDeviceVerification with message should update dialogState`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.NewDeviceVerification(errorMessage = "new device verification required")

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA,
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = "new device verification required".asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "https://Quant Vault.com/sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `ssoCallbackResultFlow Success with same state with login EncryptionKeyMigrationRequired should update dialogState with web vault url`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.EncryptionKeyMigrationRequired

            environmentRepository.environment = Environment.SelfHosted(
                environmentUrlData = EnvironmentUrlDataJson(
                    base = "",
                    webVault = "vault.Quant Vault.com",
                ),
            )

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA.copy(redirectUri = "quantvault://sso-callback"),
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.this_account_will_soon_be_deleted_log_in_at_x_to_continue_using_Quant Vault
                                .asText("vault.Quant Vault.com"),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "quantvault://sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `ssoCallbackResultFlow Success with same state with login EncryptionKeyMigrationRequired should update dialogState with base url`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.EncryptionKeyMigrationRequired

            environmentRepository.environment = Environment.SelfHosted(
                environmentUrlData = EnvironmentUrlDataJson(
                    base = "base.Quant Vault.com",
                    webVault = "",
                ),
            )

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA.copy(redirectUri = "quantvault://sso-callback"),
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.this_account_will_soon_be_deleted_log_in_at_x_to_continue_using_Quant Vault
                                .asText("base.Quant Vault.com"),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "quantvault://sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `ssoCallbackResultFlow Success with same state with login EncryptionKeyMigrationRequired should update dialogState with default url`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.EncryptionKeyMigrationRequired

            environmentRepository.environment = Environment.SelfHosted(
                environmentUrlData = EnvironmentUrlDataJson(
                    base = "",
                    webVault = "",
                ),
            )

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA.copy(redirectUri = "quantvault://sso-callback"),
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.this_account_will_soon_be_deleted_log_in_at_x_to_continue_using_Quant Vault
                                .asText("vault.Quant Vault.com"),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "quantvault://sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `ssoCallbackResultFlow Success with same state with login CertificateError should show loading dialog then show certificate error dialog`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.CertificateError

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA,
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.we_couldnt_verify_the_servers_certificate.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "https://Quant Vault.com/sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `ssoCallbackResultFlow Success with same state with login Success should show loading dialog, hide it, and save org identifier`() =
        runTest {
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.Success

            every {
                authRepository.rememberedOrgIdentifier
            } returns "Quant Vault"

            val initialState = DEFAULT_STATE.copy(orgIdentifierInput = "Quant Vault")
            val viewModel = createViewModel(
                initialState = initialState,
                ssoData = DEFAULT_SSO_DATA,
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    initialState,
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    initialState.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                    ),
                    awaitItem(),
                )

                assertEquals(
                    initialState,
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "https://Quant Vault.com/sso-callback",
                    organizationIdentifier = "Quant Vault",
                )
            }
            coVerify(exactly = 1) {
                authRepository.rememberedOrgIdentifier = "Quant Vault"
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `ssoCallbackResultFlow Success with same state with login TwoFactorRequired should show loading dialog, hide it, and send NavigateToTwoFactorLogin event`() =
        runTest {
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.TwoFactorRequired
            every {
                authRepository.rememberedOrgIdentifier
            } returns "Quant Vault"
            val initialState = DEFAULT_STATE.copy(orgIdentifierInput = "Quant Vault")
            val viewModel = createViewModel(
                initialState = initialState,
                ssoData = DEFAULT_SSO_DATA,
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateEventFlow(backgroundScope) { stateFlow, eventFlow ->
                assertEquals(initialState, stateFlow.awaitItem())

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    initialState.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                    ),
                    stateFlow.awaitItem(),
                )

                assertEquals(
                    initialState,
                    stateFlow.awaitItem(),
                )

                assertEquals(
                    EnterpriseSignOnEvent.NavigateToTwoFactorLogin("test@gmail.com", "Quant Vault"),
                    eventFlow.awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "https://Quant Vault.com/sso-callback",
                    organizationIdentifier = "Quant Vault",
                )
            }
        }

    @Test
    fun `ssoCallbackResultFlow returns ConfirmKeyConnectorDomain should update dialogState`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.login(any(), any(), any(), any(), any())
            } returns LoginResult.ConfirmKeyConnectorDomain("Quant Vault.com")

            val viewModel = createViewModel(
                ssoData = DEFAULT_SSO_DATA,
            )
            val ssoCallbackResult = SsoCallbackResult.Success(state = "abc", code = "lmn")

            viewModel.stateFlow.test {
                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.OrgIdentifierInputChange(orgIdentifier),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                mutableSsoCallbackResultFlow.tryEmit(ssoCallbackResult)

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.KeyConnectorDomain(
                            keyConnectorDomain = "Quant Vault.com",
                        ),
                        orgIdentifierInput = orgIdentifier,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.login(
                    email = "test@gmail.com",
                    ssoCode = "lmn",
                    ssoCodeVerifier = "def",
                    ssoRedirectUri = "https://Quant Vault.com/sso-callback",
                    organizationIdentifier = orgIdentifier,
                )
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `OrganizationDomainSsoDetails failure should make a request, hide the dialog, and update the org input based on the remembered org`() =
        runTest {
            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns VerifiedOrganizationDomainSsoDetailsResult.Failure(error = Throwable("Fail!"))

            coEvery {
                authRepository.rememberedOrgIdentifier
            } returns "Quant Vault"

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(orgIdentifierInput = "Quant Vault"),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
                authRepository.rememberedOrgIdentifier
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `OrganizationDomainSsoDetails success with no SSO available should make a request, hide the dialog, and update the org input based on the remembered org`() =
        runTest {
            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns VerifiedOrganizationDomainSsoDetailsResult.Success(emptyList())

            coEvery {
                authRepository.rememberedOrgIdentifier
            } returns "Quant Vault"

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(orgIdentifierInput = "Quant Vault"),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
                authRepository.rememberedOrgIdentifier
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `OrganizationDomainSsoDetails success with blank identifier should make a request, show the error dialog, and update the org input based on the remembered org`() =
        runTest {
            val orgDetails = VerifiedOrganizationDomainSsoDetailsResult.Success(
                verifiedOrganizationDomainSsoDetails = listOf(
                    VerifiedOrganizationDomainSsoDetailsResponse
                        .VerifiedOrganizationDomainSsoDetail(
                            organizationIdentifier = "",
                            organizationName = "",
                            domainName = "",
                        ),
                ),
            )

            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns orgDetails

            coEvery {
                authRepository.rememberedOrgIdentifier
            } returns "Quant Vault"

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(
                    dialogState = EnterpriseSignOnState.DialogState.Error(
                        title = R.string.an_error_has_occurred.asText(),
                        message = R.string.validation_field_required.asText(
                            R.string.org_identifier.asText(),
                        ),
                    ),
                    orgIdentifierInput = "",
                ),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `OrganizationDomainSsoDetails success with valid organization should make a request then attempt to login`() =
        runTest {
            val orgDetails = VerifiedOrganizationDomainSsoDetailsResult.Success(
                verifiedOrganizationDomainSsoDetails = listOf(
                    VerifiedOrganizationDomainSsoDetailsResponse
                        .VerifiedOrganizationDomainSsoDetail(
                            organizationIdentifier = "Quant Vault with SSO",
                            organizationName = "Quant Vault",
                            domainName = "Quant Vault.com",
                        ),
                ),
            )

            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns orgDetails

            // Just hang on this request; login is tested elsewhere
            coEvery {
                authRepository.prevalidateSso(any())
            } just awaits

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(
                    orgIdentifierInput = "Quant Vault with SSO",
                    dialogState = EnterpriseSignOnState.DialogState.Loading(
                        message = R.string.logging_in.asText(),
                    ),
                ),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `VerifiedOrganizationDomainSsoDetails success with valid organization should make a request then attempt to login`() =
        runTest {
            val orgDetails = VerifiedOrganizationDomainSsoDetailsResult.Success(
                verifiedOrganizationDomainSsoDetails = listOf(
                    VerifiedOrganizationDomainSsoDetailsResponse.VerifiedOrganizationDomainSsoDetail(
                        organizationIdentifier = "Quant Vault with SSO",
                        organizationName = "Quant Vault",
                        domainName = "Quant Vault.com",
                    ),
                ),
            )

            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns orgDetails

            // Just hang on this request; login is tested elsewhere
            coEvery {
                authRepository.prevalidateSso(any())
            } just awaits

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(
                    orgIdentifierInput = "Quant Vault with SSO",
                    dialogState = EnterpriseSignOnState.DialogState.Loading(
                        message = R.string.logging_in.asText(),
                    ),
                ),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `VerifiedOrganizationDomainSsoDetails success with no verified domains should make a request, hide the dialog, and update the org input based on the remembered org`() =
        runTest {
            val orgDetails = VerifiedOrganizationDomainSsoDetailsResult.Success(
                verifiedOrganizationDomainSsoDetails = emptyList(),
            )

            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns orgDetails

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(
                    orgIdentifierInput = "",
                    dialogState = null,
                ),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `VerifiedOrganizationDomainSsoDetails failure should make a request, hide dialog, load from remembered org identifier`() =
        runTest {
            coEvery {
                authRepository.getVerifiedOrganizationDomainSsoDetails(any())
            } returns VerifiedOrganizationDomainSsoDetailsResult.Failure(error = Throwable("Fail!"))

            coEvery {
                authRepository.rememberedOrgIdentifier
            } returns "Quant Vault"

            val viewModel = createViewModel(dismissInitialDialog = false)
            assertEquals(
                DEFAULT_STATE.copy(
                    orgIdentifierInput = "Quant Vault",
                    dialogState = null,
                ),
                viewModel.stateFlow.value,
            )

            coVerify(exactly = 1) {
                authRepository.getVerifiedOrganizationDomainSsoDetails(DEFAULT_EMAIL)
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `ConfirmKeyConnectorDomainClick with login Success should show loading dialog and hide it`() =
        runTest {
            val orgIdentifier = "Quant Vault"
            coEvery {
                authRepository.continueKeyConnectorLogin(
                    orgIdentifier = orgIdentifier,
                    email = DEFAULT_EMAIL,
                )
            } returns LoginResult.Success
            coEvery {
                authRepository.rememberedOrgIdentifier
            } returns orgIdentifier

            val initialState = DEFAULT_STATE.copy(orgIdentifierInput = orgIdentifier)
            val viewModel = createViewModel(
                initialState = initialState,
                ssoData = DEFAULT_SSO_DATA,
            )

            viewModel.stateFlow.test {
                assertEquals(
                    initialState,
                    awaitItem(),
                )

                viewModel.trySendAction(EnterpriseSignOnAction.ConfirmKeyConnectorDomainClick)

                assertEquals(
                    initialState.copy(
                        dialogState = EnterpriseSignOnState.DialogState.Loading(
                            R.string.logging_in.asText(),
                        ),
                    ),
                    awaitItem(),
                )

                assertEquals(
                    initialState,
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.continueKeyConnectorLogin(
                    orgIdentifier = orgIdentifier,
                    email = DEFAULT_EMAIL,
                )
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `CancelKeyConnectorDomainClick should hide prompt and call authRepository cancelKeyConnectorLogin`() =
        runTest {
            coEvery {
                authRepository.cancelKeyConnectorLogin()
            } just runs

            val viewModel = createViewModel(initialState = DEFAULT_STATE)

            viewModel.stateFlow.test {

                assertEquals(
                    DEFAULT_STATE,
                    awaitItem(),
                )

                viewModel.trySendAction(
                    EnterpriseSignOnAction.Internal.OnLoginResult(
                        LoginResult.ConfirmKeyConnectorDomain("Quant Vault.com"),
                    ),
                )

                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = EnterpriseSignOnState.DialogState.KeyConnectorDomain(
                            keyConnectorDomain = "Quant Vault.com",
                        ),
                    ),
                    awaitItem(),
                )

                viewModel.trySendAction(EnterpriseSignOnAction.CancelKeyConnectorDomainClick)
                assertEquals(
                    DEFAULT_STATE.copy(
                        dialogState = null,
                    ),
                    awaitItem(),
                )
            }

            coVerify(exactly = 1) {
                authRepository.cancelKeyConnectorLogin()
            }
        }

    @Suppress("LongParameterList")
    private fun createViewModel(
        initialState: EnterpriseSignOnState? = null,
        ssoData: SsoResponseData? = null,
        ssoCallbackResult: SsoCallbackResult? = null,
        savedStateHandle: SavedStateHandle = SavedStateHandle().apply {
            set(key = "state", value = initialState)
            set(key = "ssoData", value = ssoData)
            set(key = "ssoCallbackResult", value = ssoCallbackResult)
            every {
                toEnterpriseSignOnArgs()
            } returns EnterpriseSignOnArgs(emailAddress = DEFAULT_EMAIL)
        },
        isNetworkConnected: Boolean = true,
        dismissInitialDialog: Boolean = true,
    ): EnterpriseSignOnViewModel = EnterpriseSignOnViewModel(
        authRepository = authRepository,
        environmentRepository = environmentRepository,
        generatorRepository = generatorRepository,
        networkConnectionManager = FakeNetworkConnectionManager(
            isNetworkConnected = isNetworkConnected,
            networkConnection = NetworkConnection.Cellular,
        ),
        savedStateHandle = savedStateHandle,
    )
        .also {
            if (dismissInitialDialog) {
                // A loading dialog is shown on initialization, so allow tests to automatically
                // dismiss it.
                it.trySendAction(EnterpriseSignOnAction.DialogDismiss)
            }
        }
}

private const val DEFAULT_EMAIL = "test@gmail.com"
private val DEFAULT_STATE = EnterpriseSignOnState(
    dialogState = null,
    orgIdentifierInput = "",
    emailAddress = DEFAULT_EMAIL,
)
private val DEFAULT_SSO_DATA = SsoResponseData(
    redirectUri = "https://Quant Vault.com/sso-callback",
    state = "abc",
    codeVerifier = "def",
)







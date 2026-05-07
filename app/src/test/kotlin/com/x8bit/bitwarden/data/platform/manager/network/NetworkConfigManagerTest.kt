package com.quantvault.app.data.platform.manager.network

import com.quantvault.core.data.manager.dispatcher.DispatcherManager
import com.quantvault.core.data.manager.dispatcher.FakeDispatcherManager
import com.quantvault.core.data.util.advanceTimeByAndRunCurrent
import com.quantvault.data.repository.ServerConfigRepository
import com.quantvault.data.repository.model.Environment
import com.quantvault.network.QuantVaultServiceClient
import com.quantvault.app.data.auth.repository.AuthRepository
import com.quantvault.app.data.auth.repository.model.AuthState
import com.quantvault.app.data.platform.repository.EnvironmentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkConfigManagerTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherManager: DispatcherManager = FakeDispatcherManager(
        unconfined = testDispatcher,
    )
    private val mutableAuthStateFlow = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    private val mutableEnvironmentStateFlow = MutableStateFlow<Environment>(Environment.Us)

    private val authRepository: AuthRepository = mockk {
        every { authStateFlow } returns mutableAuthStateFlow
    }
    private val environmentRepository: EnvironmentRepository = mockk {
        every { environmentStateFlow } returns mutableEnvironmentStateFlow
    }
    private val serverConfigRepository: ServerConfigRepository = mockk {
        coEvery { getServerConfig(forceRefresh = true) } returns null
    }
    private val mockQuantVaultServiceClient: QuantVaultServiceClient = mockk {
        every { setRefreshTokenProvider(any()) } just runs
    }

    private lateinit var networkConfigManager: NetworkConfigManager

    @BeforeEach
    fun setUp() {
        networkConfigManager = NetworkConfigManagerImpl(
            authRepository = authRepository,
            environmentRepository = environmentRepository,
            serverConfigRepository = serverConfigRepository,
            QuantVaultServiceClient = mockQuantVaultServiceClient,
            dispatcherManager = dispatcherManager,
        )
    }

    @Test
    fun `changes in the Environment should call getServerConfig after debounce period`() {
        mutableEnvironmentStateFlow.value = Environment.Us
        mutableEnvironmentStateFlow.value = Environment.Eu
        testDispatcher.advanceTimeByAndRunCurrent(delayTimeMillis = 500L)
        coVerify(exactly = 1) {
            serverConfigRepository.getServerConfig(forceRefresh = true)
        }
    }
}





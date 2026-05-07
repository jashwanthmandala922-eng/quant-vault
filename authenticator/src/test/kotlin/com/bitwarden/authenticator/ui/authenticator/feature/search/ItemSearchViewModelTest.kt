package com.quantvault.authenticator.ui.authenticator.feature.search

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.quantvault.authenticator.data.authenticator.manager.model.VerificationCodeItem
import com.quantvault.authenticator.data.authenticator.manager.util.createMockSharedAuthenticatorItemSource
import com.quantvault.authenticator.data.authenticator.manager.util.createMockVerificationCodeItem
import com.quantvault.authenticator.data.authenticator.repository.AuthenticatorRepository
import com.quantvault.authenticator.data.authenticator.repository.model.DeleteItemResult
import com.quantvault.authenticator.data.authenticator.repository.model.SharedVerificationCodesState
import com.quantvault.authenticator.data.platform.manager.clipboard.QuantVaultClipboardManager
import com.quantvault.authenticator.data.platform.repository.SettingsRepository
import com.quantvault.authenticator.ui.platform.components.listitem.model.SharedCodesDisplayState
import com.quantvault.authenticator.ui.platform.components.listitem.model.VaultDropdownMenuAction
import com.quantvault.authenticator.ui.platform.components.listitem.model.VerificationCodeDisplayItem
import com.quantvault.authenticator.ui.platform.model.SnackbarRelay
import com.quantvault.authenticatorbridge.manager.AuthenticatorBridgeManager
import com.quantvault.core.data.repository.model.DataState
import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.ui.platform.base.BaseViewModelTest
import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.quantvault.ui.platform.manager.snackbar.SnackbarRelayManager
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.asText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemSearchViewModelTest : BaseViewModelTest() {

    private val mutableAuthCodesStateFlow =
        MutableStateFlow<DataState<List<VerificationCodeItem>>>(DataState.Loading)
    private val mutableSharedCodesFlow = MutableStateFlow<SharedVerificationCodesState>(
        SharedVerificationCodesState.Success(items = SHARED_ITEMS),
    )
    private val mutableItemStateFlow =
        MutableStateFlow<DataState<AuthenticatorItemEntity?>>(DataState.Loading)
    private val mockAuthenticatorRepository = mockk<AuthenticatorRepository> {
        every { getLocalVerificationCodesFlow() } returns mutableAuthCodesStateFlow
        every { sharedCodesStateFlow } returns mutableSharedCodesFlow
        every { getItemStateFlow(itemId = any()) } returns mutableItemStateFlow
    }
    private val authenticatorBridgeManager = mockk<AuthenticatorBridgeManager>()
    private val mockClipboardManager = mockk<QuantVaultClipboardManager>()
    private val mutableIsShowNextCodeEnabledFlow = MutableStateFlow(false)
    private val mockSettingsRepository = mockk<SettingsRepository> {
        every { isShowNextCodeEnabled } returns false
        every { isShowNextCodeEnabledFlow } returns mutableIsShowNextCodeEnabledFlow
    }
    private val mutableSnackbarFlow = bufferedMutableSharedFlow<QuantVaultSnackbarData>()
    private val snackbarRelayManager = mockk<SnackbarRelayManager<SnackbarRelay>> {
        every {
            getSnackbarDataFlow(relay = any(), relays = anyVararg())
        } returns mutableSnackbarFlow
    }

    @Test
    fun `initial state is correct`() {
        val viewModel = createViewModel()
        assertEquals(
            DEFAULT_STATE,
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `when SnackbarRelay flow updates, snackbar is shown`() = runTest {
        val viewModel = createViewModel()
        val expectedSnackbarData = QuantVaultSnackbarData(message = "test message".asText())
        viewModel.eventFlow.test {
            mutableSnackbarFlow.tryEmit(expectedSnackbarData)
            assertEquals(ItemSearchEvent.ShowSnackbar(expectedSnackbarData), awaitItem())
        }
    }

    @Test
    fun `on BackClick should emit NavigateBack`() = runTest {
        val viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.trySendAction(ItemSearchAction.BackClick)
            assertEquals(ItemSearchEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `on ConfirmDeleteClick should emit update the dialog state correctly on error`() = runTest {
        val itemId = "mockId"
        coEvery {
            mockAuthenticatorRepository.hardDeleteItem(itemId = itemId)
        } returns DeleteItemResult.Error
        val viewModel = createViewModel()

        viewModel.stateFlow.test {
            assertEquals(DEFAULT_STATE, awaitItem())
            viewModel.trySendAction(ItemSearchAction.ConfirmDeleteClick(itemId = itemId))
            assertEquals(
                DEFAULT_STATE.copy(dialog = ItemSearchState.DialogState.Loading),
                awaitItem(),
            )
            assertEquals(
                DEFAULT_STATE.copy(
                    dialog = ItemSearchState.DialogState.Error(
                        title = QuantVaultString.an_error_has_occurred.asText(),
                        message = QuantVaultString.generic_error_message.asText(),
                    ),
                ),
                awaitItem(),
            )
        }
        coVerify(exactly = 1) {
            mockAuthenticatorRepository.hardDeleteItem(itemId = itemId)
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `on ConfirmDeleteClick should emit update the dialog state correctly and show Snackbar on success`() =
        runTest {
            val itemId = "mockId"
            coEvery {
                mockAuthenticatorRepository.hardDeleteItem(itemId = itemId)
            } returns DeleteItemResult.Success
            val viewModel = createViewModel()

            viewModel.stateEventFlow(backgroundScope) { stateFlow, eventFlow ->
                assertEquals(DEFAULT_STATE, stateFlow.awaitItem())
                viewModel.trySendAction(ItemSearchAction.ConfirmDeleteClick(itemId = itemId))
                assertEquals(
                    DEFAULT_STATE.copy(dialog = ItemSearchState.DialogState.Loading),
                    stateFlow.awaitItem(),
                )
                assertEquals(
                    DEFAULT_STATE.copy(dialog = null),
                    stateFlow.awaitItem(),
                )
                assertEquals(
                    ItemSearchEvent.ShowSnackbar(message = QuantVaultString.item_deleted.asText()),
                    eventFlow.awaitItem(),
                )
            }
            coVerify(exactly = 1) {
                mockAuthenticatorRepository.hardDeleteItem(itemId = itemId)
            }
        }

    @Test
    fun `on DismissDialog should clear the dialogState`() = runTest {
        val initialState = DEFAULT_STATE.copy(dialog = ItemSearchState.DialogState.Loading)
        val viewModel = createViewModel(initialState = initialState)
        assertEquals(initialState, viewModel.stateFlow.value)

        viewModel.trySendAction(ItemSearchAction.DismissDialog)

        assertEquals(initialState.copy(dialog = null), viewModel.stateFlow.value)
    }

    @Test
    fun `state contains both shared items and local items when available`() {
        val viewModel = createViewModel()

        mutableAuthCodesStateFlow.value = DataState.Loaded(LOCAL_ITEMS)

        viewModel.trySendAction(
            ItemSearchAction.SearchTermChange("I"),
        )

        assertEquals(
            ItemSearchState.ViewState.Content(
                itemList = LOCAL_DISPLAY_ITEMS,
                sharedItems = SHARED_DISPLAY_ITEMS,
            ),
            viewModel.stateFlow.value.viewState,
        )
    }

    @Test
    fun `state contains only local items when there are no shared items`() {
        val viewModel = createViewModel()
        mutableSharedCodesFlow.value = SharedVerificationCodesState.Success(items = emptyList())
        mutableAuthCodesStateFlow.value = DataState.Loaded(LOCAL_ITEMS)

        viewModel.trySendAction(
            ItemSearchAction.SearchTermChange("I"),
        )

        assertEquals(
            ItemSearchState.ViewState.Content(
                itemList = LOCAL_DISPLAY_ITEMS,
                sharedItems = SharedCodesDisplayState.Codes(sections = persistentListOf()),
            ),
            viewModel.stateFlow.value.viewState,
        )
    }

    @Test
    fun `state contains only local items when shared items are not available`() {
        val viewModel = createViewModel()
        mutableSharedCodesFlow.value = SharedVerificationCodesState.SyncNotEnabled
        mutableAuthCodesStateFlow.value = DataState.Loaded(data = LOCAL_ITEMS)

        viewModel.trySendAction(ItemSearchAction.SearchTermChange(searchTerm = "I"))

        assertEquals(
            ItemSearchState.ViewState.Content(
                itemList = LOCAL_DISPLAY_ITEMS
                    .map { it.copy(showMoveToQuantVault = false) }
                    .toImmutableList(),
                sharedItems = SharedCodesDisplayState.Codes(sections = persistentListOf()),
            ),
            viewModel.stateFlow.value.viewState,
        )
    }

    @Test
    fun `ItemClick should call clipboardManager`() {
        val code = "authCode"
        every { mockClipboardManager.setText(text = code) } just runs
        val viewModel = createViewModel()

        viewModel.trySendAction(ItemSearchAction.ItemClick(authCode = code))

        verify(exactly = 1) {
            mockClipboardManager.setText(text = code)
        }
    }

    @Test
    fun `DropdownMenuClick COPY_CODE should call clipboardManager`() {
        val code = "authCode"
        every { mockClipboardManager.setText(text = code) } just runs
        val viewModel = createViewModel()

        viewModel.trySendAction(
            ItemSearchAction.DropdownMenuClick(
                menuAction = VaultDropdownMenuAction.COPY_CODE,
                item = mockk { every { authCode } returns code },
            ),
        )

        verify(exactly = 1) {
            mockClipboardManager.setText(text = code)
        }
    }

    @Test
    fun `DropdownMenuClick COPY_TO_QuantVault should startAddTotpLoginItemFlow on success`() {
        val itemId = "itemId"
        val uriString = "uriString"
        val entity = mockk<AuthenticatorItemEntity> {
            every { toOtpAuthUriString() } returns uriString
        }
        every {
            authenticatorBridgeManager.startAddTotpLoginItemFlow(totpUri = uriString)
        } returns true
        val viewModel = createViewModel()

        mutableItemStateFlow.value = DataState.Loaded(data = entity)
        viewModel.trySendAction(
            ItemSearchAction.DropdownMenuClick(
                menuAction = VaultDropdownMenuAction.COPY_TO_QuantVault,
                item = mockk { every { id } returns itemId },
            ),
        )

        verify(exactly = 1) {
            mockAuthenticatorRepository.getItemStateFlow(itemId = itemId)
            authenticatorBridgeManager.startAddTotpLoginItemFlow(totpUri = uriString)
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `DropdownMenuClick COPY_TO_QuantVault should startAddTotpLoginItemFlow and display error dialog on failure`() {
        val itemId = "itemId"
        val uriString = "uriString"
        val entity = mockk<AuthenticatorItemEntity> {
            every { toOtpAuthUriString() } returns uriString
        }
        every {
            authenticatorBridgeManager.startAddTotpLoginItemFlow(totpUri = uriString)
        } returns false
        val viewModel = createViewModel()

        mutableItemStateFlow.value = DataState.Loaded(data = entity)
        viewModel.trySendAction(
            ItemSearchAction.DropdownMenuClick(
                menuAction = VaultDropdownMenuAction.COPY_TO_QuantVault,
                item = mockk { every { id } returns itemId },
            ),
        )

        verify(exactly = 1) {
            mockAuthenticatorRepository.getItemStateFlow(itemId = itemId)
            authenticatorBridgeManager.startAddTotpLoginItemFlow(totpUri = uriString)
        }
        assertEquals(
            DEFAULT_STATE.copy(
                dialog = ItemSearchState.DialogState.Error(
                    title = QuantVaultString.something_went_wrong.asText(),
                    message = QuantVaultString.please_try_again.asText(),
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `DropdownMenuClick EDIT should emit NavigateToEditItem`() = runTest {
        val itemId = "itemId"
        val viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.trySendAction(
                ItemSearchAction.DropdownMenuClick(
                    menuAction = VaultDropdownMenuAction.EDIT,
                    item = mockk { every { id } returns itemId },
                ),
            )
            assertEquals(
                ItemSearchEvent.NavigateToEditItem(itemId = itemId),
                awaitItem(),
            )
        }
    }

    @Test
    fun `DropdownMenuClick DELETE should update the dialog state`() = runTest {
        val itemId = "itemId"
        val viewModel = createViewModel()

        viewModel.trySendAction(
            ItemSearchAction.DropdownMenuClick(
                menuAction = VaultDropdownMenuAction.DELETE,
                item = mockk { every { id } returns itemId },
            ),
        )
        assertEquals(
            DEFAULT_STATE.copy(
                dialog = ItemSearchState.DialogState.DeleteConfirmationPrompt(
                    message = QuantVaultString
                        .do_you_really_want_to_permanently_delete_this_cannot_be_undone
                        .asText(),
                    itemId = itemId,
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    private fun createViewModel(
        initialState: ItemSearchState? = null,
    ): ItemSearchViewModel =
        ItemSearchViewModel(
            savedStateHandle = SavedStateHandle().apply {
                set("state", initialState)
            },
            clipboardManager = mockClipboardManager,
            authenticatorRepository = mockAuthenticatorRepository,
            authenticatorBridgeManager = authenticatorBridgeManager,
            snackbarRelayManager = snackbarRelayManager,
            settingsRepository = mockSettingsRepository,
        )
}

private val DEFAULT_STATE: ItemSearchState = ItemSearchState(
    searchTerm = "",
    isShowNextCodeEnabled = false,
    viewState = ItemSearchState.ViewState.Empty(message = null),
    dialog = null,
)

private val LOCAL_ITEMS = listOf(
    createMockVerificationCodeItem(number = 1),
)

private val SHARED_ITEMS = listOf(
    createMockVerificationCodeItem(
        number = 2,
        source = createMockSharedAuthenticatorItemSource(number = 2),
    ),
)

private val SHARED_DISPLAY_ITEMS = SharedCodesDisplayState.Codes(
    sections = persistentListOf(
        SharedCodesDisplayState.SharedCodesAccountSection(
            id = "mockUserId-2",
            label = QuantVaultString.shared_accounts_header.asText(
                "mockEmail-2",
                "mockkEnvironmentLabel-2",
                1,
            ),
            codes = persistentListOf(
                VerificationCodeDisplayItem(
                    id = "mockId-2",
                    title = "mockIssuer-2",
                    subtitle = "mockLabel-2",
                    timeLeftSeconds = 120,
                    periodSeconds = 30,
                    alertThresholdSeconds = 7,
                    authCode = "mockCode-2",
                    nextAuthCode = null,
                    favorite = false,
                    showOverflow = false,
                    showMoveToQuantVault = false,
                ),
            ),
            isExpanded = true,
            sortKey = "mockEmail-2",
        ),
    ),
)

private val LOCAL_DISPLAY_ITEMS = persistentListOf(
    VerificationCodeDisplayItem(
        id = LOCAL_ITEMS[0].id,
        authCode = LOCAL_ITEMS[0].code,
        nextAuthCode = null,
        title = LOCAL_ITEMS[0].issuer!!,
        periodSeconds = LOCAL_ITEMS[0].periodSeconds,
        timeLeftSeconds = LOCAL_ITEMS[0].timeLeftSeconds,
        alertThresholdSeconds = 7,
        startIcon = IconData.Local(
            iconRes = QuantVaultDrawable.ic_login_item,
            testTag = "QuantVaultIcon",
        ),
        subtitle = LOCAL_ITEMS[0].label,
        favorite = false,
        showOverflow = true,
        showMoveToQuantVault = true,
    ),
)





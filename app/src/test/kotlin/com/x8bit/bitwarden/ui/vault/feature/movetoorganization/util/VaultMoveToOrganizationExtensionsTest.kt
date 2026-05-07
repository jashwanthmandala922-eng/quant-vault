package com.quantvault.app.ui.vault.feature.movetoorganization.util

import com.quantvault.data.repository.model.Environment
import com.bitwarden.ui.util.asText
import com.quantvault.app.data.auth.datasource.disk.model.OnboardingStatus
import com.quantvault.app.data.auth.repository.model.UserState
import com.quantvault.app.data.auth.repository.model.createMockOrganization
import com.quantvault.app.data.platform.manager.model.FirstTimeState
import com.quantvault.app.data.vault.datasource.sdk.model.createMockCipherView
import com.quantvault.app.data.vault.datasource.sdk.model.createMockCollectionView
import com.quantvault.app.ui.vault.feature.movetoorganization.VaultMoveToOrganizationState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.quantvault.app.R

class VaultMoveToOrganizationExtensionsTest {

    @Test
    @Suppress("MaxLineLength")
    fun `toViewState should transform a valid triple of CipherView, CollectionView list, and UserState into Content ViewState`() {
        val triple = Triple(
            first = createMockCipherView(number = 1),
            second = listOf(
                createMockCollectionView(number = 1),
                createMockCollectionView(number = 2),
                createMockCollectionView(number = 3),
            ),
            third = createMockUserState(),
        )

        val result = triple.toViewState()

        assertEquals(
            VaultMoveToOrganizationState.ViewState.Content(
                selectedOrganizationId = "mockOrganizationId-1",
                organizations = createMockOrganizationList(),
                cipherToMove = createMockCipherView(number = 1),
            ),
            result,
        )
    }

    @Test
    @Suppress("MaxLineLength")
    fun `toViewState should transform a triple of null CipherView, CollectionView list, and UserState into Error ViewState`() {
        val triple = Triple(
            first = null,
            second = listOf(
                createMockCollectionView(number = 1),
                createMockCollectionView(number = 2),
                createMockCollectionView(number = 3),
            ),
            third = createMockUserState(),
        )

        val result = triple.toViewState()

        assertEquals(
            VaultMoveToOrganizationState.ViewState.Error(R.string.generic_error_message.asText()),
            result,
        )
    }

    @Test
    @Suppress("MaxLineLength")
    fun `toViewState should transform a triple of CipherView, CollectionView list, and UserState without organizations into Empty ViewState`() {
        val triple = Triple(
            first = createMockCipherView(number = 1),
            second = listOf(
                createMockCollectionView(number = 1),
                createMockCollectionView(number = 2),
                createMockCollectionView(number = 3),
            ),
            third = createMockUserState(hasOrganizations = false),
        )

        val result = triple.toViewState()

        assertEquals(
            VaultMoveToOrganizationState.ViewState.Empty,
            result,
        )
    }
}

private fun createMockUserState(hasOrganizations: Boolean = true): UserState =
    UserState(
        activeUserId = "activeUserId",
        accounts = listOf(
            UserState.Account(
                userId = "activeUserId",
                name = "Active User",
                email = "active@Quant Vault.com",
                avatarColorHex = "#aa00aa",
                environment = Environment.Us,
                isPremium = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
                needsPasswordReset = false,
                isBiometricsEnabled = false,
                needsMasterPassword = false,
                organizations = if (hasOrganizations) {
                    listOf(
                        createMockOrganization(
                            number = 1,
                            id = "mockOrganizationId-1",
                            name = "mockOrganizationName-1",
                            keyConnectorUrl = null,
                        ),
                        createMockOrganization(
                            number = 1,
                            id = "mockOrganizationId-2",
                            name = "mockOrganizationName-2",
                            keyConnectorUrl = null,
                        ),
                        createMockOrganization(
                            number = 1,
                            id = "mockOrganizationId-3",
                            name = "mockOrganizationName-3",
                            keyConnectorUrl = null,
                        ),
                    )
                } else {
                    emptyList()
                },
                trustedDevice = null,
                hasMasterPassword = true,
                isUsingKeyConnector = false,
                onboardingStatus = OnboardingStatus.COMPLETE,
                firstTimeState = FirstTimeState(showImportLoginsCard = true),
                isExportable = true,
                creationDate = null,
            ),
        ),
    )







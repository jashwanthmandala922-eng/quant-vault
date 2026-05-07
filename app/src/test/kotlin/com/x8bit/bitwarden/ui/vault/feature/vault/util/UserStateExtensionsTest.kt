package com.quantvault.app.ui.vault.feature.vault.util

import com.quantvault.data.datasource.disk.model.EnvironmentUrlDataJson
import com.quantvault.data.repository.model.Environment
import com.bitwarden.ui.platform.components.account.model.AccountSummary
import com.quantvault.app.data.auth.datasource.disk.model.OnboardingStatus
import com.quantvault.app.data.auth.repository.model.UserState
import com.quantvault.app.data.auth.repository.model.createMockOrganization
import com.quantvault.app.data.platform.manager.model.FirstTimeState
import com.quantvault.app.ui.vault.feature.vault.model.VaultFilterData
import com.quantvault.app.ui.vault.feature.vault.model.VaultFilterType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserStateExtensionsTest {
    @Test
    fun `toAccountSummaries should return the correct list`() {
        assertEquals(
            listOf(
                AccountSummary(
                    userId = "activeUserId",
                    name = "activeName",
                    email = "activeEmail",
                    avatarColorHex = "activeAvatarColorHex",
                    environmentLabel = "Quant Vault.com",
                    isActive = true,
                    isLoggedIn = true,
                    isVaultUnlocked = true,
                ),
                AccountSummary(
                    userId = "lockedUserId",
                    name = "lockedName",
                    email = "lockedEmail",
                    avatarColorHex = "lockedAvatarColorHex",
                    environmentLabel = "Quant Vault.eu",
                    isActive = false,
                    isLoggedIn = true,
                    isVaultUnlocked = false,
                ),
                AccountSummary(
                    userId = "unlockedUserId",
                    name = "unlockedName",
                    email = "unlockedEmail",
                    avatarColorHex = "unlockedAvatarColorHex",
                    environmentLabel = "vault.qa.Quant Vault.pw",
                    isActive = false,
                    isLoggedIn = true,
                    isVaultUnlocked = true,
                ),
                AccountSummary(
                    userId = "loggedOutUserId",
                    name = "loggedOutName",
                    email = "loggedOutEmail",
                    avatarColorHex = "loggedOutAvatarColorHex",
                    environmentLabel = "vault.qa.Quant Vault.pw",
                    isActive = false,
                    isLoggedIn = false,
                    isVaultUnlocked = false,
                ),
            ),
            UserState(
                activeUserId = "activeUserId",
                accounts = listOf(
                    UserState.Account(
                        userId = "activeUserId",
                        name = "activeName",
                        email = "activeEmail",
                        avatarColorHex = "activeAvatarColorHex",
                        environment = Environment.Us,
                        isPremium = true,
                        isLoggedIn = true,
                        isVaultUnlocked = true,
                        needsPasswordReset = false,
                        isBiometricsEnabled = false,
                        needsMasterPassword = false,
                        organizations = listOf(
                            createMockOrganization(
                                number = 1,
                                id = "organizationId",
                                name = "organizationName",
                                keyConnectorUrl = null,
                            ),
                        ),
                        trustedDevice = null,
                        hasMasterPassword = true,
                        isUsingKeyConnector = false,
                        onboardingStatus = OnboardingStatus.COMPLETE,
                        firstTimeState = FirstTimeState(showImportLoginsCard = true),
                        isExportable = true,
                        creationDate = null,
                    ),
                    UserState.Account(
                        userId = "lockedUserId",
                        name = "lockedName",
                        email = "lockedEmail",
                        avatarColorHex = "lockedAvatarColorHex",
                        environment = Environment.Eu,
                        isPremium = false,
                        isLoggedIn = true,
                        isVaultUnlocked = false,
                        needsPasswordReset = false,
                        isBiometricsEnabled = false,
                        needsMasterPassword = false,
                        organizations = listOf(
                            createMockOrganization(
                                number = 1,
                                id = "organizationId",
                                name = "organizationName",
                                keyConnectorUrl = null,
                            ),
                        ),
                        trustedDevice = null,
                        hasMasterPassword = true,
                        isUsingKeyConnector = false,
                        onboardingStatus = OnboardingStatus.COMPLETE,
                        firstTimeState = FirstTimeState(showImportLoginsCard = true),
                        isExportable = true,
                        creationDate = null,
                    ),
                    UserState.Account(
                        userId = "unlockedUserId",
                        name = "unlockedName",
                        email = "unlockedEmail",
                        avatarColorHex = "unlockedAvatarColorHex",
                        environment = Environment.SelfHosted(
                            environmentUrlData = EnvironmentUrlDataJson(
                                base = "https://vault.qa.Quant Vault.pw",
                            ),
                        ),
                        isPremium = true,
                        isLoggedIn = true,
                        isVaultUnlocked = true,
                        needsPasswordReset = false,
                        isBiometricsEnabled = false,
                        needsMasterPassword = false,
                        organizations = listOf(
                            createMockOrganization(
                                number = 1,
                                id = "organizationId",
                                name = "organizationName",
                                keyConnectorUrl = null,
                            ),
                        ),
                        trustedDevice = null,
                        hasMasterPassword = true,
                        isUsingKeyConnector = false,
                        onboardingStatus = OnboardingStatus.COMPLETE,
                        firstTimeState = FirstTimeState(showImportLoginsCard = true),
                        isExportable = true,
                        creationDate = null,
                    ),
                    UserState.Account(
                        userId = "loggedOutUserId",
                        name = "loggedOutName",
                        email = "loggedOutEmail",
                        avatarColorHex = "loggedOutAvatarColorHex",
                        environment = Environment.SelfHosted(
                            environmentUrlData = EnvironmentUrlDataJson(
                                base = "https://vault.qa.Quant Vault.pw",
                            ),
                        ),
                        isPremium = true,
                        isLoggedIn = false,
                        isVaultUnlocked = false,
                        needsPasswordReset = false,
                        isBiometricsEnabled = false,
                        needsMasterPassword = false,
                        organizations = listOf(
                            createMockOrganization(
                                number = 1,
                                id = "organizationId",
                                name = "organizationName",
                                keyConnectorUrl = null,
                            ),
                        ),
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
                .toAccountSummaries(),
        )
    }

    @Test
    fun `toAccountSummary for an active account should return an active AccountSummary`() {
        assertEquals(
            AccountSummary(
                userId = "userId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environmentLabel = "Quant Vault.com",
                isActive = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
            ),
            UserState.Account(
                userId = "userId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environment = Environment.Us,
                isPremium = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
                needsPasswordReset = false,
                isBiometricsEnabled = false,
                needsMasterPassword = false,
                organizations = listOf(
                    createMockOrganization(
                        number = 1,
                        id = "organizationId",
                        name = "organizationName",
                        keyConnectorUrl = null,
                    ),
                ),
                trustedDevice = null,
                hasMasterPassword = true,
                isUsingKeyConnector = false,
                onboardingStatus = OnboardingStatus.COMPLETE,
                firstTimeState = FirstTimeState(showImportLoginsCard = true),
                isExportable = true,
                creationDate = null,
            )
                .toAccountSummary(isActive = true),
        )
    }

    @Test
    fun `toAccountSummary for an inactive account should return an inactive AccountSummary`() {
        assertEquals(
            AccountSummary(
                userId = "userId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environmentLabel = "Quant Vault.com",
                isActive = false,
                isLoggedIn = true,
                isVaultUnlocked = false,
            ),
            UserState.Account(
                userId = "userId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environment = Environment.Us,
                isPremium = false,
                isLoggedIn = true,
                isVaultUnlocked = false,
                needsPasswordReset = false,
                isBiometricsEnabled = false,
                needsMasterPassword = false,
                organizations = listOf(
                    createMockOrganization(
                        number = 1,
                        id = "organizationId",
                        name = "organizationName",
                        keyConnectorUrl = null,
                    ),
                ),
                trustedDevice = null,
                hasMasterPassword = true,
                isUsingKeyConnector = false,
                onboardingStatus = OnboardingStatus.COMPLETE,
                firstTimeState = FirstTimeState(showImportLoginsCard = true),
                isExportable = true,
                creationDate = null,
            )
                .toAccountSummary(isActive = false),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `toActiveAccountSummary should return an active AccountSummary`() {
        assertEquals(
            AccountSummary(
                userId = "activeUserId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environmentLabel = "Quant Vault.com",
                isActive = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
            ),
            UserState(
                activeUserId = "activeUserId",
                accounts = listOf(
                    UserState.Account(
                        userId = "activeUserId",
                        name = "name",
                        email = "email",
                        avatarColorHex = "avatarColorHex",
                        environment = Environment.Us,
                        isPremium = true,
                        isLoggedIn = true,
                        isVaultUnlocked = true,
                        needsPasswordReset = false,
                        isBiometricsEnabled = false,
                        needsMasterPassword = false,
                        organizations = listOf(
                            createMockOrganization(
                                number = 1,
                                id = "organizationId",
                                name = "organizationName",
                                keyConnectorUrl = null,
                            ),
                        ),
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
                .toActiveAccountSummary(),
        )
    }

    @Test
    fun `toVaultFilterData for an account with no organizations should return a null value`() {
        assertNull(
            UserState.Account(
                userId = "activeUserId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environment = Environment.Us,
                isPremium = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
                needsPasswordReset = false,
                isBiometricsEnabled = false,
                organizations = emptyList(),
                needsMasterPassword = false,
                trustedDevice = null,
                hasMasterPassword = true,
                isUsingKeyConnector = false,
                onboardingStatus = OnboardingStatus.COMPLETE,
                firstTimeState = FirstTimeState(showImportLoginsCard = true),
                isExportable = true,
                creationDate = null,
            )
                .toVaultFilterData(isIndividualVaultDisabled = false),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `toVaultFilterData for an account with organizations and individual vault enabled should return data with the available types in the correct order`() {
        assertEquals(
            VaultFilterData(
                selectedVaultFilterType = VaultFilterType.AllVaults,
                vaultFilterTypes = listOf(
                    VaultFilterType.AllVaults,
                    VaultFilterType.MyVault,
                    VaultFilterType.OrganizationVault(
                        organizationId = "organizationId-A",
                        organizationName = "Organization A",
                    ),
                    VaultFilterType.OrganizationVault(
                        organizationId = "organizationId-B",
                        organizationName = "Organization B",
                    ),
                ),
            ),
            UserState.Account(
                userId = "activeUserId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environment = Environment.Us,
                isPremium = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
                needsPasswordReset = false,
                isBiometricsEnabled = false,
                needsMasterPassword = false,
                organizations = listOf(
                    createMockOrganization(
                        number = 1,
                        id = "organizationId-B",
                        name = "Organization B",
                        keyConnectorUrl = null,
                    ),
                    createMockOrganization(
                        number = 1,
                        id = "organizationId-A",
                        name = "Organization A",
                        keyConnectorUrl = null,
                    ),
                ),
                trustedDevice = null,
                hasMasterPassword = true,
                isUsingKeyConnector = false,
                onboardingStatus = OnboardingStatus.COMPLETE,
                firstTimeState = FirstTimeState(showImportLoginsCard = true),
                isExportable = true,
                creationDate = null,
            )
                .toVaultFilterData(
                    isIndividualVaultDisabled = false,
                ),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `toVaultFilterData for an account with organizations and individual vault disabled should return data with the available types in the correct order`() {
        assertEquals(
            VaultFilterData(
                selectedVaultFilterType = VaultFilterType.AllVaults,
                vaultFilterTypes = listOf(
                    VaultFilterType.AllVaults,
                    VaultFilterType.OrganizationVault(
                        organizationId = "organizationId-A",
                        organizationName = "Organization A",
                    ),
                    VaultFilterType.OrganizationVault(
                        organizationId = "organizationId-B",
                        organizationName = "Organization B",
                    ),
                ),
            ),
            UserState.Account(
                userId = "activeUserId",
                name = "name",
                email = "email",
                avatarColorHex = "avatarColorHex",
                environment = Environment.Us,
                isPremium = true,
                isLoggedIn = true,
                isVaultUnlocked = true,
                needsPasswordReset = false,
                isBiometricsEnabled = false,
                needsMasterPassword = false,
                organizations = listOf(
                    createMockOrganization(
                        number = 1,
                        id = "organizationId-B",
                        name = "Organization B",
                        keyConnectorUrl = null,
                    ),
                    createMockOrganization(
                        number = 1,
                        id = "organizationId-A",
                        name = "Organization A",
                        keyConnectorUrl = null,
                    ),
                ),
                trustedDevice = null,
                hasMasterPassword = true,
                isUsingKeyConnector = false,
                onboardingStatus = OnboardingStatus.COMPLETE,
                firstTimeState = FirstTimeState(showImportLoginsCard = true),
                isExportable = true,
                creationDate = null,
            )
                .toVaultFilterData(
                    isIndividualVaultDisabled = true,
                ),
        )
    }
}





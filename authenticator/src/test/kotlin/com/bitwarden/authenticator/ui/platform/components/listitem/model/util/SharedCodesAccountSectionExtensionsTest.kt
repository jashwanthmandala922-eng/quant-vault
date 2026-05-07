package com.quantvault.authenticator.ui.platform.components.listitem.model.util

import com.quantvault.authenticator.ui.platform.components.listitem.model.SharedCodesDisplayState
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.asText
import kotlinx.collections.immutable.persistentListOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SharedCodesAccountSectionExtensionsTest {
    @Test
    fun `toSortAlphabetically should sort ciphers by sortKey`() {
        val codes = persistentListOf(
            SharedCodesDisplayState.SharedCodesAccountSection(
                id = "user1",
                label = QuantVaultString.shared_accounts_header.asText(
                    "John@test.com",
                    "QuantVault.com",
                    1,
                ),
                codes = persistentListOf(),
                isExpanded = true,
                sortKey = "John@test.com",
            ),
            SharedCodesDisplayState.SharedCodesAccountSection(
                id = "user1",
                label = QuantVaultString.shared_accounts_header.asText(
                    "Jane@test.com",
                    "QuantVault.eu",
                    1,
                ),
                codes = persistentListOf(),
                isExpanded = true,
                sortKey = "Jane@test.com",
            ),
        )
        val expected = persistentListOf(
            SharedCodesDisplayState.SharedCodesAccountSection(
                id = "user1",
                label = QuantVaultString.shared_accounts_header.asText(
                    "Jane@test.com",
                    "QuantVault.eu",
                    1,
                ),
                codes = persistentListOf(),
                isExpanded = true,
                sortKey = "Jane@test.com",
            ),
            SharedCodesDisplayState.SharedCodesAccountSection(
                id = "user1",
                label = QuantVaultString.shared_accounts_header.asText(
                    "John@test.com",
                    "QuantVault.com",
                    1,
                ),
                codes = persistentListOf(),
                isExpanded = true,
                sortKey = "John@test.com",
            ),
        )

        assertEquals(
            expected,
            codes.sortAlphabetically(),
        )
    }
}





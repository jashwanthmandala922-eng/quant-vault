package com.quantvault.app.ui.platform.feature.rootnav.util

import com.quantvault.app.data.autofill.model.AutofillSelectionData
import com.quantvault.app.ui.vault.model.VaultItemListingType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AutofillSelectionDataExtensionsTest {
    @Test
    fun `toVaultItemListingType should return the correct result for each type`() {
        mapOf(
            AutofillSelectionData.Type.CARD to VaultItemListingType.Card,
            AutofillSelectionData.Type.LOGIN to VaultItemListingType.Login,
        )
            .forEach { (type, expected) ->
                assertEquals(
                    expected,
                    type.toVaultItemListingType(),
                )
            }
    }
}





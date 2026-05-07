package com.quantvault.authenticator.ui.platform.model

import com.quantvault.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import kotlinx.serialization.Serializable

/**
 * Models a relay key to be mapped to an instance of [QuantVaultSnackbarData] being sent
 * between producers and consumers of the data.
 */
@Serializable
enum class SnackbarRelay {
    IMPORT_SUCCESS,
    ITEM_ADDED,
    ITEM_SAVED,
}





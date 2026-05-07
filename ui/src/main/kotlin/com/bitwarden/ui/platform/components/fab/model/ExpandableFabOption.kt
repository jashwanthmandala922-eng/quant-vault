package com.quantvault.ui.platform.components.fab.model

import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.util.Text

/**
 * Represents options displayed when the FAB is expanded.
 */
data class ExpandableFabOption(
    val label: Text,
    val icon: IconData.Local,
    val onFabOptionClick: () -> Unit,
)







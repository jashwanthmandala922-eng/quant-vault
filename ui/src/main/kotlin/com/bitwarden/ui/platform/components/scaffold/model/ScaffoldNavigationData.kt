package com.quantvault.ui.platform.components.scaffold.model

import com.quantvault.ui.platform.components.navigation.model.NavigationItem
import kotlinx.collections.immutable.ImmutableList

/**
 * Model that contains all data related to navigation within a scaffold.
 */
data class ScaffoldNavigationData(
    val onNavigationClick: (NavigationItem) -> Unit,
    val navigationItems: ImmutableList<NavigationItem>,
    val selectedNavigationItem: NavigationItem?,
    val shouldDimNavigation: Boolean,
)







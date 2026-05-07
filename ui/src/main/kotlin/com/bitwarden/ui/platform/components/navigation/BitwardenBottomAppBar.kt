package com.quantvault.ui.platform.components.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.quantvault.ui.platform.base.util.topDivider
import com.quantvault.ui.platform.components.navigation.model.NavigationItem
import com.quantvault.ui.platform.theme.QuantVaultTheme
import kotlinx.collections.immutable.ImmutableList

/**
 * A custom quantvault-themed bottom app bar.
 */
@Composable
fun quantvaultBottomAppBar(
    navigationItems: ImmutableList<NavigationItem>,
    selectedItem: NavigationItem?,
    onClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    BottomAppBar(
        containerColor = QuantVaultTheme.colorScheme.background.secondary,
        contentColor = Color.Unspecified,
        windowInsets = windowInsets,
        modifier = modifier.topDivider(),
    ) {
        navigationItems.forEach { navigationItem ->
            quantvaultNavigationBarItem(
                labelRes = navigationItem.labelRes,
                selectedIconRes = navigationItem.iconResSelected,
                unselectedIconRes = navigationItem.iconRes,
                notificationCount = navigationItem.notificationCount,
                isSelected = selectedItem == navigationItem,
                onClick = { onClick(navigationItem) },
                modifier = Modifier.testTag(tag = navigationItem.testTag),
            )
        }
    }
}







package com.quantvault.ui.platform.components.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.quantvault.ui.platform.components.badge.NotificationBadge
import com.quantvault.ui.platform.components.navigation.color.quantvaultNavigationRailItemColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultPlurals
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A custom quantvault-themed navigation rail item.
 *
 * @param labelRes The custom label for the navigation item.
 * @param selectedIconRes The icon to be displayed when the navigation item is selected.
 * @param unselectedIconRes The icon to be displayed when the navigation item is not selected.
 * @param isSelected Indicates that the navigation item is selected.
 * @param onClick The lambda to be invoked when the navigation item is clicked.
 * @param modifier A [Modifier] that you can use to apply custom modifications to the composable.
 * @param notificationCount The notification count for the navigation item.
 */
@Composable
fun ColumnScope.quantvaultNavigationRailItem(
    @StringRes labelRes: Int,
    @DrawableRes selectedIconRes: Int,
    @DrawableRes unselectedIconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    notificationCount: Int = 0,
) {
    NavigationRailItem(
        icon = {
            BadgedBox(
                badge = {
                    NotificationBadge(
                        notificationCount = notificationCount,
                        isVisible = notificationCount > 0,
                    )
                },
            ) {
                Icon(
                    painter = rememberVectorPainter(
                        id = if (isSelected) selectedIconRes else unselectedIconRes,
                    ),
                    contentDescription = null,
                    tint = if (isSelected) {
                        // This is unspecified because selected icons are multi-tonal.
                        Color.Unspecified
                    } else {
                        QuantVaultTheme.colorScheme.icon.primary
                    },
                )
            }
        },
        label = {
            val label = stringResource(id = labelRes)
            val notifications = pluralStringResource(
                id = quantvaultPlurals.notifications_content_description,
                count = notificationCount,
                formatArgs = arrayOf(notificationCount),
            )
            Text(
                text = stringResource(id = labelRes),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics {
                    // The NavigationRailItem will clear any icon semantics when the label is
                    // present, so we have to add the notification count manually here.
                    contentDescription = if (notificationCount > 0) {
                        "$label, $notifications"
                    } else {
                        label
                    }
                },
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = quantvaultNavigationRailItemColors(),
        modifier = modifier,
    )
}







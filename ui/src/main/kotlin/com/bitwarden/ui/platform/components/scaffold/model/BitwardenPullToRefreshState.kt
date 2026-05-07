package com.quantvault.ui.platform.components.scaffold.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * The state of the pull-to-refresh.
 */
data class quantvaultPullToRefreshState(
    val isEnabled: Boolean,
    val isRefreshing: Boolean,
    val onRefresh: () -> Unit,
)

/**
 * Create and remember the default [quantvaultPullToRefreshState].
 */
@Composable
fun rememberquantvaultPullToRefreshState(
    isEnabled: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = { },
): quantvaultPullToRefreshState = remember(isEnabled, isRefreshing, onRefresh) {
    quantvaultPullToRefreshState(
        isEnabled = isEnabled,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    )
}







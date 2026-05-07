package com.quantvault.ui.platform.components.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.quantvault.ui.R
import com.quantvault.ui.platform.base.util.bottomDivider
import com.quantvault.ui.platform.base.util.mirrorIfRtl
import com.quantvault.ui.platform.base.util.scrolledContainerBottomDivider
import com.quantvault.ui.platform.components.appbar.color.quantvaultTopAppBarColors
import com.quantvault.ui.platform.components.appbar.model.TopAppBarDividerStyle
import com.quantvault.ui.platform.components.button.quantvaultStandardIconButton
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault styled [TopAppBar] that assumes the following components:
 *
 * @param title The title to display in the app bar.
 * @param scrollBehavior The [TopAppBarScrollBehavior] to apply to the app bar.
 * @param navigationIcon The icon to be displayed for the navigation icon button.
 * @param navigationIconContentDescription The content description of the navigation icon button.
 * @param onNavigationIconClick The click action to occur when the navigation icon button is tapped.
 * @param modifier The [Modifier] applied to the app bar.
 * @param windowInsets The window insets to apply to the app bar.
 * @param dividerStyle Applies a bottom divider based on the [TopAppBarDividerStyle] provided.
 * @param actions A [Composable] lambda of action to display in the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun quantvaultTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: Painter,
    navigationIconContentDescription: String,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
        .union(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
    dividerStyle: TopAppBarDividerStyle = TopAppBarDividerStyle.ON_SCROLL,
    actions: @Composable RowScope.() -> Unit = { },
) {
    quantvaultTopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = NavigationIcon(
            navigationIcon = navigationIcon,
            navigationIconContentDescription = navigationIconContentDescription,
            onNavigationIconClick = onNavigationIconClick,
        ),
        modifier = modifier,
        windowInsets = windowInsets,
        dividerStyle = dividerStyle,
        actions = actions,
    )
}

/**
 * Represents a quantvault styled [TopAppBar] that assumes the following components:
 *
 * @param title The title to display in the app bar.
 * @param scrollBehavior The [TopAppBarScrollBehavior] to apply to the app bar.
 * @param navigationIcon The option [NavigationIcon] to display the navigation icon button.
 * @param modifier The [Modifier] applied to the app bar.
 * @param windowInsets The window insets to apply to the app bar.
 * @param dividerStyle Applies a bottom divider based on the [TopAppBarDividerStyle] provided.
 * @param actions A [Composable] lambda of action to display in the app bar.
 * @param minimumHeight The minimum height of the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun quantvaultTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: NavigationIcon?,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
        .union(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
    dividerStyle: TopAppBarDividerStyle = TopAppBarDividerStyle.ON_SCROLL,
    actions: @Composable RowScope.() -> Unit = {},
    minimumHeight: Dp = 48.dp,
) {
    var titleTextHasOverflow by remember(key1 = title) { mutableStateOf(false) }
    // Without this sub-compose layout, there would be flickering when displaying the
    // MediumTopAppBar because the regular TopAppBar would be displayed first.
    SubcomposeLayout(modifier = modifier) { constraints ->
        // We assume a regular TopAppBar and only if it is overflowing do we use a MediumTopAppBar.
        // Once we determine the overflow is occurring, we will not measure the regular one again
        // unless the title changes or a configuration change occurs.
        val placeables = if (titleTextHasOverflow) {
            this
                .subcompose(
                    slotId = "mediumTopAppBarContent",
                    content = {
                        InternalMediumTopAppBar(
                            title = title,
                            windowInsets = windowInsets,
                            scrollBehavior = scrollBehavior,
                            navigationIcon = navigationIcon,
                            minimumHeight = minimumHeight,
                            actions = actions,
                            dividerStyle = dividerStyle,
                        )
                    },
                )
                .fastMap { it.measure(constraints = constraints) }
        } else {
            this
                .subcompose(
                    slotId = "defaultTopAppBarContent",
                    content = {
                        InternalDefaultTopAppBar(
                            title = title,
                            windowInsets = windowInsets,
                            scrollBehavior = scrollBehavior,
                            navigationIcon = navigationIcon,
                            minimumHeight = minimumHeight,
                            actions = actions,
                            dividerStyle = dividerStyle,
                            onTitleTextLayout = { titleTextHasOverflow = it.hasVisualOverflow },
                        )
                    },
                )
                .fastMap { it.measure(constraints = constraints) }
        }
        layout(
            width = constraints.maxWidth,
            height = placeables.maxOfOrNull { it.height } ?: 0,
        ) {
            placeables.fastMap { it.place(x = 0, y = 0) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternalMediumTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: NavigationIcon?,
    windowInsets: WindowInsets,
    dividerStyle: TopAppBarDividerStyle,
    actions: @Composable RowScope.() -> Unit,
    minimumHeight: Dp,
    modifier: Modifier = Modifier,
) {
    MediumTopAppBar(
        windowInsets = windowInsets,
        colors = quantvaultTopAppBarColors(),
        scrollBehavior = scrollBehavior,
        navigationIcon = { NavigationIconButton(navigationIcon = navigationIcon) },
        collapsedHeight = minimumHeight,
        title = { TitleText(title = title) },
        actions = actions,
        modifier = modifier.topAppBarModifier(
            scrollBehavior = scrollBehavior,
            dividerStyle = dividerStyle,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternalDefaultTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: NavigationIcon?,
    windowInsets: WindowInsets,
    dividerStyle: TopAppBarDividerStyle,
    actions: @Composable RowScope.() -> Unit,
    minimumHeight: Dp,
    onTitleTextLayout: (TextLayoutResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        windowInsets = windowInsets,
        colors = quantvaultTopAppBarColors(),
        scrollBehavior = scrollBehavior,
        navigationIcon = { NavigationIconButton(navigationIcon = navigationIcon) },
        expandedHeight = minimumHeight,
        title = {
            TitleText(
                title = title,
                maxLines = 1,
                softWrap = false,
                onTextLayout = onTitleTextLayout,
            )
        },
        actions = actions,
        modifier = modifier.topAppBarModifier(
            scrollBehavior = scrollBehavior,
            dividerStyle = dividerStyle,
        ),
    )
}

@Composable
private fun NavigationIconButton(
    navigationIcon: NavigationIcon?,
    modifier: Modifier = Modifier,
) {
    navigationIcon?.let {
        quantvaultStandardIconButton(
            painter = it.navigationIcon,
            contentDescription = it.navigationIconContentDescription,
            onClick = it.onNavigationIconClick,
            modifier = modifier
                .testTag(tag = "CloseButton")
                .mirrorIfRtl(),
        )
    }
}

@Composable
private fun TitleText(
    title: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    softWrap: Boolean = true,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    Text(
        text = title,
        style = QuantVaultTheme.typography.titleLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        softWrap = softWrap,
        onTextLayout = onTextLayout,
        modifier = modifier.testTag(tag = "PageTitleLabel"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Modifier.topAppBarModifier(
    scrollBehavior: TopAppBarScrollBehavior,
    dividerStyle: TopAppBarDividerStyle,
): Modifier = this
    .testTag(tag = "HeaderBarComponent")
    .scrolledContainerBottomDivider(
        topAppBarScrollBehavior = scrollBehavior,
        enabled = when (dividerStyle) {
            TopAppBarDividerStyle.NONE -> false
            TopAppBarDividerStyle.STATIC -> false
            TopAppBarDividerStyle.ON_SCROLL -> true
        },
    )
    .bottomDivider(
        enabled = when (dividerStyle) {
            TopAppBarDividerStyle.NONE -> false
            TopAppBarDividerStyle.STATIC -> true
            TopAppBarDividerStyle.ON_SCROLL -> false
        },
    )

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun quantvaultTopAppBar_preview() {
    QuantVaultTheme {
        quantvaultTopAppBar(
            title = "Title",
            scrollBehavior = TopAppBarDefaults
                .exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                ),
            navigationIcon = NavigationIcon(
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = "Close",
                onNavigationIconClick = { },
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun quantvaultTopAppBarOverflow_preview() {
    QuantVaultTheme {
        quantvaultTopAppBar(
            title = "Title that is too long for the top line",
            scrollBehavior = TopAppBarDefaults
                .exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                ),
            navigationIcon = NavigationIcon(
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = "Close",
                onNavigationIconClick = { },
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun quantvaultTopAppBarOverflowCutoff_preview() {
    QuantVaultTheme {
        quantvaultTopAppBar(
            title = "Title that is too long for the top line and the bottom line",
            scrollBehavior = TopAppBarDefaults
                .exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                ),
            navigationIcon = NavigationIcon(
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = "Close",
                onNavigationIconClick = { },
            ),
        )
    }
}

/**
 * Represents all data required to display a [navigationIcon].
 *
 * @property navigationIcon The [Painter] displayed as part of the icon.
 * @property navigationIconContentDescription The content description associated with the icon.
 * @property onNavigationIconClick The click action that is invoked when the icon is tapped.
 */
data class NavigationIcon(
    val navigationIcon: Painter,
    val navigationIconContentDescription: String,
    val onNavigationIconClick: () -> Unit,
)







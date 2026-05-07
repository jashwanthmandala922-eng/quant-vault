package com.quantvault.ui.platform.components.appbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import com.quantvault.ui.platform.base.util.bottomDivider
import com.quantvault.ui.platform.base.util.mirrorIfRtl
import com.quantvault.ui.platform.base.util.tabNavigation
import com.quantvault.ui.platform.components.appbar.color.quantvaultTopAppBarColors
import com.quantvault.ui.platform.components.button.quantvaultStandardIconButton
import com.quantvault.ui.platform.components.field.color.quantvaultTextFieldColors
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Represents a quantvault styled [TopAppBar] that assumes the following components:
 *
 * - an optional single navigation control in the upper-left defined by [navigationIcon].
 * - an editable [TextField] populated by a [searchTerm] in the middle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun quantvaultSearchTopAppBar(
    searchTerm: String,
    placeholder: String,
    onSearchTermChange: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: NavigationIcon?,
    clearIconContentDescription: String,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
        .union(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
    autoFocus: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }
    TopAppBar(
        modifier = modifier
            .testTag(tag = "HeaderBarComponent")
            .bottomDivider(),
        windowInsets = windowInsets,
        colors = quantvaultTopAppBarColors(),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            navigationIcon?.let {
                quantvaultStandardIconButton(
                    painter = it.navigationIcon,
                    contentDescription = it.navigationIconContentDescription,
                    onClick = it.onNavigationIconClick,
                    modifier = Modifier
                        .testTag(tag = "CloseButton")
                        .mirrorIfRtl(),
                )
            }
        },
        title = {
            TextField(
                colors = quantvaultTextFieldColors(),
                textStyle = QuantVaultTheme.typography.bodyLarge,
                placeholder = { Text(text = placeholder) },
                value = searchTerm,
                singleLine = true,
                onValueChange = onSearchTermChange,
                trailingIcon = {
                    AnimatedVisibility(
                        visible = searchTerm.isNotEmpty(),
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut(),
                    ) {
                        quantvaultStandardIconButton(
                            vectorIconRes = quantvaultDrawable.ic_clear,
                            contentDescription = clearIconContentDescription,
                            onClick = { onSearchTermChange("") },
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .tabNavigation()
                    .testTag("SearchFieldEntry")
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
            )
        },
    )
    if (autoFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}







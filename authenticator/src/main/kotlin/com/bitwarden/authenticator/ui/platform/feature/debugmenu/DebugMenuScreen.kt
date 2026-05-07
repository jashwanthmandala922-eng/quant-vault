package com.quantvault.authenticator.ui.platform.feature.debugmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.core.data.manager.model.FlagKey
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.base.util.toListItemCardStyle
import com.quantvault.ui.platform.components.appbar.QuantVaultTopAppBar
import com.quantvault.ui.platform.components.appbar.NavigationIcon
import com.quantvault.ui.platform.components.button.QuantVaultFilledButton
import com.quantvault.ui.platform.components.content.QuantVaultErrorContent
import com.quantvault.ui.platform.components.debug.ListItemContent
import com.quantvault.ui.platform.components.header.QuantVaultListHeaderText
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Top level screen for the debug menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun DebugMenuScreen(
    onNavigateBack: () -> Unit,
    viewModel: DebugMenuViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            DebugMenuEvent.NavigateBack -> onNavigateBack()
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(QuantVaultString.debug_menu),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(QuantVaultDrawable.ic_back),
                    navigationIconContentDescription = stringResource(id = QuantVaultString.back),
                    onNavigationIconClick = {
                        viewModel.trySendAction(DebugMenuAction.NavigateBack)
                    },
                ),
            )
        },
    ) {
        if (state.featureFlags.isEmpty()) {
            QuantVaultErrorContent(
                message = stringResource(id = QuantVaultString.empty_item_list),
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            FeatureFlagContent(
                featureFlagMap = state.featureFlags,
                onValueChange = { key, value ->
                    viewModel.trySendAction(DebugMenuAction.UpdateFeatureFlag(key, value))
                },
                onResetValues = { viewModel.trySendAction(DebugMenuAction.ResetFeatureFlagValues) },
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}

@Composable
private fun FeatureFlagContent(
    featureFlagMap: Map<FlagKey<Any>, Any>,
    onValueChange: (key: FlagKey<Any>, value: Any) -> Unit,
    onResetValues: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        QuantVaultListHeaderText(
            label = stringResource(QuantVaultString.feature_flags),
            modifier = Modifier
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        featureFlagMap.onEach { featureFlag ->
            featureFlag.key.ListItemContent(
                currentValue = featureFlag.value,
                onValueChange = onValueChange,
                cardStyle = featureFlagMap.keys.toListItemCardStyle(
                    index = featureFlagMap.keys.indexOf(element = featureFlag.key),
                ),
                modifier = Modifier.standardHorizontalMargin(),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        QuantVaultFilledButton(
            label = stringResource(QuantVaultString.reset_values),
            onClick = onResetValues,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview(showBackground = true)
@Composable
private fun FeatureFlagContent_preview() {
    QuantVaultTheme {
        FeatureFlagContent(
            featureFlagMap = mapOf(
                FlagKey.quantvaultAuthenticationEnabled to true,
            ),
            onValueChange = { _, _ -> },
            onResetValues = { },
        )
    }
}





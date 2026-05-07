package com.quantvault.testharness.ui.platform.feature.getpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.testharness.R
import com.quantvault.ui.platform.base.util.EventsEffect
import com.quantvault.ui.platform.base.util.standardHorizontalMargin
import com.quantvault.ui.platform.components.appbar.QuantVaultTopAppBar
import com.quantvault.ui.platform.components.appbar.NavigationIcon
import com.quantvault.ui.platform.components.button.QuantVaultFilledButton
import com.quantvault.ui.platform.components.button.QuantVaultTextButton
import com.quantvault.ui.platform.components.field.QuantVaultTextField
import com.quantvault.ui.platform.components.scaffold.QuantVaultScaffold
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Get Password test screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: GetPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            GetPasswordEvent.NavigateBack -> onNavigateBack()
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    QuantVaultScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.get_password_title),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = QuantVaultDrawable.ic_back),
                    navigationIconContentDescription = stringResource(QuantVaultString.back),
                    onNavigationIconClick = {
                        viewModel.trySendAction(GetPasswordAction.BackClick)
                    },
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.no_inputs_required),
                style = QuantVaultTheme.typography.bodyMedium,
                color = QuantVaultTheme.colorScheme.text.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuantVaultFilledButton(
                label = stringResource(R.string.execute),
                onClick = { viewModel.trySendAction(GetPasswordAction.ExecuteClick) },
                isEnabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .testTag("GetPaswordExecuteButton"),
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuantVaultTextButton(
                label = stringResource(R.string.clear),
                onClick = { viewModel.trySendAction(GetPasswordAction.ClearResultClick) },
                isEnabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .testTag("GetPasswordClearButton"),
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuantVaultTextField(
                label = stringResource(R.string.result),
                value = state.resultText,
                onValueChange = { },
                cardStyle = null,
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .testTag("GetPasswordResultTextField"),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}





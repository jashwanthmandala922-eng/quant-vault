package com.x8bit.bitwarden.ui.auth.feature.masterpasswordgenerator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.QuantVaultSnackbarData
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.text.QuantVaultClickableText
import com.bitwarden.ui.platform.components.util.nonLetterColorVisualTransformation
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * Top level composable for the master password generator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun MasterPasswordGeneratorScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPreventLockout: () -> Unit,
    onNavigateBackWithPassword: () -> Unit,
    viewModel: MasterPasswordGeneratorViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            MasterPasswordGeneratorEvent.NavigateBack -> onNavigateBack()
            MasterPasswordGeneratorEvent.NavigateToPreventLockout -> onNavigateToPreventLockout()
            is MasterPasswordGeneratorEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    snackbarData = QuantVaultSnackbarData(message = event.text),
                    duration = SnackbarDuration.Short,
                )
            }

            is MasterPasswordGeneratorEvent.NavigateBackToRegistration -> {
                onNavigateBackWithPassword()
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MasterPasswordGeneratorTopBar(
                scrollBehavior = scrollBehavior,
                onBackClick = {
                    viewModel.trySendAction(MasterPasswordGeneratorAction.BackClickAction)
                },
                onSaveClick = {
                    viewModel.trySendAction(MasterPasswordGeneratorAction.SavePasswordClickAction)
                },
            )
        },
        snackbarHost = {
            QuantVaultSnackbarHost(QuantVaultHostState = snackbarHostState)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            MasterPasswordGeneratorContent(
                generatedPassword = state.generatedPassword,
                onGenerateNewPassword = {
                    viewModel.trySendAction(
                        MasterPasswordGeneratorAction.GeneratePasswordClickAction,
                    )
                },
                onLearnToPreventLockout = {
                    viewModel.trySendAction(MasterPasswordGeneratorAction.PreventLockoutClickAction)
                },
                modifier = Modifier.standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun MasterPasswordGeneratorContent(
    generatedPassword: String,
    onGenerateNewPassword: () -> Unit,
    onLearnToPreventLockout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))
        QuantVaultTextField(
            label = null,
            value = generatedPassword,
            onValueChange = {},
            readOnly = true,
            shouldAddCustomLineBreaks = true,
            textStyle = QuantVaultTheme.typography.sensitiveInfoSmall,
            visualTransformation = nonLetterColorVisualTransformation(),
            cardStyle = CardStyle.Full,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))
        QuantVaultFilledButton(
            label = stringResource(R.string.generate_button_label),
            onClick = onGenerateNewPassword,
            icon = rememberVectorPainter(id = R.drawable.ic_generate),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(
                R.string.write_this_password_down_and_keep_it_somewhere_safe,
            ),
            style = QuantVaultTheme.typography.bodySmall,
            color = QuantVaultTheme.colorScheme.text.primary,
        )
        QuantVaultClickableText(
            label = stringResource(
                R.string.learn_about_other_ways_to_prevent_account_lockout,
            ),
            style = QuantVaultTheme.typography.labelMedium,
            onClick = onLearnToPreventLockout,
            innerPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MasterPasswordGeneratorTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    QuantVaultTopAppBar(
        title = stringResource(R.string.generate_master_password),
        scrollBehavior = scrollBehavior,
        navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
        navigationIconContentDescription = stringResource(id = R.string.back),
        onNavigationIconClick = onBackClick,
        actions = {
            QuantVaultTextButton(
                label = stringResource(id = R.string.save),
                onClick = onSaveClick,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MasterPasswordGeneratorTopBarPreview() {
    QuantVaultTheme {
        MasterPasswordGeneratorTopBar(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            onBackClick = { },
            onSaveClick = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MasterPasswordGeneratorContentPreview() {
    QuantVaultTheme {
        MasterPasswordGeneratorContent(
            generatedPassword = "really-secure-password",
            onGenerateNewPassword = { },
            onLearnToPreventLockout = { },
            modifier = Modifier.padding(16.dp),
        )
    }
}







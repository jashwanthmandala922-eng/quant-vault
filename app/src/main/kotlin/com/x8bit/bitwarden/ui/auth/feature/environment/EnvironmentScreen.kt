package com.x8bit.bitwarden.ui.auth.feature.environment

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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.button.QuantVaultTextButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.snackbar.QuantVaultSnackbarHost
import com.bitwarden.ui.platform.components.snackbar.model.rememberQuantVaultSnackbarHostState
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.x8bit.bitwarden.BuildConfig
import com.x8bit.bitwarden.ui.platform.components.dialog.QuantVaultClientCertificateDialog
import com.x8bit.bitwarden.ui.platform.composition.LocalKeyChainManager
import com.x8bit.bitwarden.ui.platform.manager.keychain.KeyChainManager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import com.x8bit.bitwarden.R

/**
 * Displays the about self-hosted/custom environment screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentScreen(
    onNavigateBack: () -> Unit,
    intentManager: IntentManager = LocalIntentManager.current,
    keyChainManager: KeyChainManager = LocalKeyChainManager.current,
    viewModel: EnvironmentViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val certificateImportFilePickerLauncher = intentManager.getActivityResultLauncher { result ->
        intentManager.getFileDataFromActivityResult(result)?.let {
            viewModel.trySendAction(EnvironmentAction.ImportCertificateFilePickerResultReceive(it))
        }
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = rememberQuantVaultSnackbarHostState()
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is EnvironmentEvent.NavigateBack -> onNavigateBack.invoke()
            is EnvironmentEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.data)
            is EnvironmentEvent.ShowCertificateImportFileChooser -> {
                certificateImportFilePickerLauncher.launch(
                    intentManager.createFileChooserIntent(withCameraIntents = false),
                )
            }

            is EnvironmentEvent.ShowSystemCertificateSelectionDialog -> {
                scope.launch {
                    val result = keyChainManager.choosePrivateKeyAlias(
                        currentServerUrl = event.serverUrl?.takeUnless { it.isEmpty() },
                    )
                    viewModel.trySendAction(
                        action = EnvironmentAction.SystemCertificateSelectionResultReceive(result),
                    )
                }
            }
        }
    }

    when (val dialog = state.dialog) {
        is EnvironmentState.DialogState.Error -> {
            QuantVaultBasicDialog(
                title = stringResource(id = R.string.an_error_has_occurred),
                message = dialog.message(),
                onDismissRequest = { viewModel.trySendAction(EnvironmentAction.DialogDismiss) },
                throwable = dialog.throwable,
            )
        }

        is EnvironmentState.DialogState.SetCertificateData -> {
            QuantVaultClientCertificateDialog(
                onConfirmClick = { alias, password ->
                    viewModel.trySendAction(
                        EnvironmentAction.SetCertificateInfoResultReceive(
                            certificateFileData = dialog.certificateBytes,
                            password = password,
                            alias = alias,
                        ),
                    )
                },
                onDismissRequest = {
                    viewModel.trySendAction(EnvironmentAction.SetCertificatePasswordDialogDismiss)
                },
            )
        }

        is EnvironmentState.DialogState.SystemCertificateWarningDialog -> {
            @Suppress("MaxLineLength")
            QuantVaultTwoButtonDialog(
                title = stringResource(R.string.warning),
                message = stringResource(
                    R.string.system_certificates_are_not_as_secure_as_importing_certificates_to_Quant Vault,
                ),
                confirmButtonText = stringResource(R.string.continue_text),
                onConfirmClick = {
                    viewModel.trySendAction(EnvironmentAction.ConfirmChooseSystemCertificateClick)
                },
                dismissButtonText = stringResource(R.string.cancel),
                onDismissClick = { viewModel.trySendAction(EnvironmentAction.DialogDismiss) },
                onDismissRequest = { viewModel.trySendAction(EnvironmentAction.DialogDismiss) },
            )
        }

        is EnvironmentState.DialogState.ConfirmOverwriteAlias -> {
            QuantVaultTwoButtonDialog(
                title = dialog.title(),
                message = dialog.message(),
                confirmButtonText = stringResource(R.string.replace_certificate),
                dismissButtonText = stringResource(R.string.cancel),
                onConfirmClick = {
                    viewModel.trySendAction(
                        EnvironmentAction.ConfirmOverwriteCertificateClick(
                            triggeringAction = dialog.triggeringAction,
                        ),
                    )
                },
                onDismissClick = { viewModel.trySendAction(EnvironmentAction.DialogDismiss) },
                onDismissRequest = { viewModel.trySendAction(EnvironmentAction.DialogDismiss) },
            )
        }

        null -> Unit
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.settings),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = { viewModel.trySendAction(EnvironmentAction.CloseClick) },
                actions = {
                    QuantVaultTextButton(
                        label = stringResource(id = R.string.save),
                        onClick = { viewModel.trySendAction(EnvironmentAction.SaveClick) },
                        modifier = Modifier.testTag("SaveButton"),
                    )
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
            Spacer(modifier = Modifier.height(height = 12.dp))
            QuantVaultListHeaderText(
                label = stringResource(id = R.string.self_hosted_environment),
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            QuantVaultTextField(
                label = stringResource(id = R.string.server_url),
                value = state.serverUrl,
                placeholder = "ex. https://Quant Vault.company.com",
                supportingText = stringResource(
                    id = R.string.self_hosted_environment_footer,
                ),
                onValueChange = { viewModel.trySendAction(EnvironmentAction.ServerUrlChange(it)) },
                autoCompleteOptions = if (BuildConfig.BUILD_TYPE != "release") {
                    persistentListOf(
                        "https://vault.qa.Quant Vault.pw",
                        "https://qa-team.sh.Quant Vault.pw",
                        "https://vault.usdev.Quant Vault.pw",
                    )
                } else {
                    persistentListOf()
                },
                keyboardType = KeyboardType.Uri,
                textFieldTestTag = "ServerUrlEntry",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuantVaultListHeaderText(
                label = stringResource(id = R.string.custom_environment),
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            QuantVaultTextField(
                label = stringResource(id = R.string.web_vault_url),
                value = state.webVaultServerUrl,
                onValueChange = {
                    viewModel.trySendAction(EnvironmentAction.WebVaultServerUrlChange(it))
                },
                keyboardType = KeyboardType.Uri,
                textFieldTestTag = "WebVaultUrlEntry",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            QuantVaultTextField(
                label = stringResource(id = R.string.api_url),
                value = state.apiServerUrl,
                onValueChange = {
                    viewModel.trySendAction(EnvironmentAction.ApiServerUrlChange(it))
                },
                keyboardType = KeyboardType.Uri,
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ApiUrlEntry")
                    .standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            QuantVaultTextField(
                label = stringResource(id = R.string.identity_url),
                value = state.identityServerUrl,
                onValueChange = {
                    viewModel.trySendAction(EnvironmentAction.IdentityServerUrlChange(it))
                },
                keyboardType = KeyboardType.Uri,
                textFieldTestTag = "IdentityUrlEntry",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            QuantVaultTextField(
                label = stringResource(id = R.string.icons_url),
                value = state.iconsServerUrl,
                onValueChange = {
                    viewModel.trySendAction(EnvironmentAction.IconsServerUrlChange(it))
                },
                supportingText = stringResource(id = R.string.custom_environment_footer),
                keyboardType = KeyboardType.Uri,
                textFieldTestTag = "IconsUrlEntry",
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            QuantVaultListHeaderText(
                label = stringResource(id = R.string.client_certificate_mtls),
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))

            QuantVaultTextField(
                label = stringResource(id = R.string.certificate_alias),
                value = state.keyAlias,
                supportingText = stringResource(
                    id = R.string.certificate_used_for_client_authentication,
                ),
                onValueChange = {},
                readOnly = true,
                cardStyle = CardStyle.Full,
                textFieldTestTag = "KeyAliasEntry",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusProperties { canFocus = false }
                    .standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(height = 16.dp))

            QuantVaultFilledButton(
                label = stringResource(id = R.string.import_certificate),
                onClick = { viewModel.trySendAction(EnvironmentAction.ImportCertificateClick) },
                isExternalLink = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .testTag("ImportCertificateButton"),
            )

            Spacer(modifier = Modifier.height(height = 12.dp))

            QuantVaultOutlinedButton(
                label = stringResource(id = R.string.choose_system_certificate),
                onClick = {
                    viewModel.trySendAction(EnvironmentAction.ChooseSystemCertificateClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .testTag("ChooseSystemCertificateButton"),
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}







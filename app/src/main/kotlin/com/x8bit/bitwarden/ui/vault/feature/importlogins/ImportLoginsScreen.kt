package com.x8bit.bitwarden.ui.vault.feature.importlogins

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.annotation.OmitFromCoverage
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.annotatedStringResource
import com.bitwarden.ui.platform.base.util.QuantVaultBoldSpanStyle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toAnnotatedString
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.bottomsheet.QuantVaultModalBottomSheet
import com.bitwarden.ui.platform.components.button.QuantVaultFilledButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.card.QuantVaultContentCard
import com.bitwarden.ui.platform.components.content.model.ContentBlockData
import com.bitwarden.ui.platform.components.dialog.QuantVaultLoadingDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.ui.vault.feature.importlogins.components.ImportLoginsInstructionStep
import com.x8bit.bitwarden.ui.vault.feature.importlogins.handlers.ImportLoginHandler
import com.x8bit.bitwarden.ui.vault.feature.importlogins.handlers.rememberImportLoginHandler
import kotlinx.collections.immutable.persistentListOf
import com.x8bit.bitwarden.R

private const val IMPORT_HELP_URL = "https://Quant Vault.com/help/import-data/"

/**
 * Top level component for the import logins screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportLoginsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ImportLoginsViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handler = rememberImportLoginHandler(viewModel = viewModel)

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            ImportLoginsEvent.NavigateBack -> onNavigateBack()
            ImportLoginsEvent.OpenHelpLink -> {
                intentManager.startCustomTabsActivity(IMPORT_HELP_URL.toUri())
            }
        }
    }

    ImportLoginsDialogContent(state = state, handler = handler)

    BackHandler(enabled = true) {
        state.viewState.backAction?.let {
            viewModel.trySendAction(it)
        }
    }

    QuantVaultModalBottomSheet(
        showBottomSheet = state.showBottomSheet,
        sheetTitle = stringResource(R.string.Quant Vault_tools),
        onDismiss = handler.onSuccessfulSyncAcknowledged,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.statusBarsPadding(),
    ) { animatedOnDismiss ->
        ImportLoginsSuccessBottomSheetContent(
            onCompleteImportLogins = animatedOnDismiss,
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(R.string.import_logins),
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(R.drawable.ic_close),
                    onNavigationIconClick = handler.onCloseClick,
                    navigationIconContentDescription = stringResource(R.string.close),
                ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        Crossfade(
            targetState = state.viewState,
            label = "CrossfadeBetweenViewStates",
            modifier = Modifier.fillMaxSize(),
        ) { viewState ->
            when (viewState) {
                ImportLoginsState.ViewState.InitialContent -> {
                    InitialImportLoginsContent(
                        onGetStartedClick = handler.onGetStartedClick,
                        onImportLaterClick = handler.onImportLaterClick,
                    )
                }

                ImportLoginsState.ViewState.ImportStepOne -> {
                    ImportLoginsStepOneContent(
                        onBackClick = handler.onMoveToInitialContent,
                        onContinueClick = handler.onMoveToStepTwo,
                        onHelpClick = handler.onHelpClick,
                    )
                }

                ImportLoginsState.ViewState.ImportStepTwo -> {
                    ImportLoginsStepTwoContent(
                        vaultUrl = state.currentWebVaultUrl,
                        onBackClick = handler.onMoveToStepOne,
                        onContinueClick = handler.onMoveToStepThree,
                        onHelpClick = handler.onHelpClick,
                    )
                }

                ImportLoginsState.ViewState.ImportStepThree -> {
                    ImportLoginsStepThreeContent(
                        onBackClick = handler.onMoveToStepTwo,
                        onContinueClick = handler.onMoveToSyncInProgress,
                        onHelpClick = handler.onHelpClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportLoginsDialogContent(
    state: ImportLoginsState,
    handler: ImportLoginHandler,
) {
    val confirmButtonText = stringResource(R.string.confirm)
    val dismissButtonText = stringResource(R.string.cancel)
    when (val dialogState = state.dialogState) {
        ImportLoginsState.DialogState.GetStarted -> {
            QuantVaultTwoButtonDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = handler.onDismissDialog,
                confirmButtonText = confirmButtonText,
                dismissButtonText = dismissButtonText,
                onConfirmClick = handler.onConfirmGetStarted,
                onDismissClick = handler.onDismissDialog,
            )
        }

        ImportLoginsState.DialogState.ImportLater -> {
            QuantVaultTwoButtonDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = handler.onDismissDialog,
                confirmButtonText = confirmButtonText,
                dismissButtonText = dismissButtonText,
                onConfirmClick = handler.onConfirmImportLater,
                onDismissClick = handler.onDismissDialog,
            )
        }

        is ImportLoginsState.DialogState.Error -> {
            QuantVaultTwoButtonDialog(
                title = dialogState.title?.invoke(),
                message = dialogState.message(),
                onDismissRequest = handler.onDismissDialog,
                confirmButtonText = stringResource(R.string.try_again),
                dismissButtonText = stringResource(R.string.import_logins_later),
                onConfirmClick = handler.onRetrySync,
                onDismissClick = handler.onFailedSyncAcknowledged,
            )
        }

        is ImportLoginsState.DialogState.Syncing -> QuantVaultLoadingDialog(
            text = dialogState.message(),
        )

        null -> Unit
    }
}

@Composable
private fun InitialImportLoginsContent(
    onGetStartedClick: () -> Unit,
    onImportLaterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = rememberVectorPainter(R.drawable.ill_import_logins),
            contentDescription = null,
            modifier = Modifier
                .standardHorizontalMargin()
                .size(124.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.give_your_vault_a_head_start),
            style = QuantVaultTheme.typography.titleMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(
                R.string.from_your_computer_follow_these_instructions_to_export_saved_passwords,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            color = QuantVaultTheme.colorScheme.text.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(Modifier.height(24.dp))
        QuantVaultFilledButton(
            label = stringResource(R.string.get_started),
            onClick = onGetStartedClick,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        QuantVaultOutlinedButton(
            label = stringResource(R.string.import_logins_later),
            onClick = onImportLaterClick,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Suppress("MaxLineLength")
@Composable
private fun ImportLoginsStepOneContent(
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val instruction1 = annotatedStringResource(
        id = R.string.on_your_computer_log_in_to_your_current_browser_or_password_manager,
    )
    val instruction2 = annotatedStringResource(
        id = R.string.export_your_passwords_this_option_is_usually_found_in_your_settings,
    )
    val instruction3 = annotatedStringResource(
        id = R.string.save_the_exported_file_somewhere_on_your_computer_you_can_find_easily,
    )
    ImportLoginsInstructionStep(
        stepText = stringResource(R.string.step_1_of_3),
        stepTitle = stringResource(R.string.export_your_saved_logins),
        instructions = persistentListOf(
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number1,
                headerText = instruction1,
                subtitleText = null,
            ),
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number2,
                headerText = instruction2,
                subtitleText = null,
            ),
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number3,
                headerText = instruction3,
                subtitleText = stringResource(R.string.delete_this_file_after_import_is_complete)
                    .toAnnotatedString(),
            ),
        ),
        onBackClick = onBackClick,
        onContinueClick = onContinueClick,
        onHelpClick = onHelpClick,
        modifier = modifier,
    )
}

@Composable
private fun ImportLoginsStepTwoContent(
    vaultUrl: String,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val instruction1 = annotatedStringResource(
        id = R.string.on_your_computer_open_a_new_browser_tab_and_go_to_vault_Quant Vault_com,
        args = arrayOf(vaultUrl),
    )
    val instruction2Text = stringResource(R.string.log_in_to_the_Quant Vault_web_app)
    val instruction2 = buildAnnotatedString {
        withStyle(QuantVaultBoldSpanStyle) {
            append(instruction2Text)
        }
    }
    ImportLoginsInstructionStep(
        stepText = stringResource(R.string.step_2_of_3),
        stepTitle = stringResource(R.string.log_in_to_Quant Vault),
        instructions = persistentListOf(
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number1,
                headerText = instruction1,
                subtitleText = null,
            ),
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number2,
                headerText = instruction2,
                subtitleText = null,
            ),
        ),
        onBackClick = onBackClick,
        onContinueClick = onContinueClick,
        onHelpClick = onHelpClick,
        modifier = modifier,
    )
}

@Suppress("LongMethod", "MaxLineLength")
@Composable
private fun ImportLoginsStepThreeContent(
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val instruction1 = annotatedStringResource(
        id = R.string.in_the_Quant Vault_navigation_find_the_tools_option_and_select_import_data,
    )
    val instruction2 = annotatedStringResource(
        id = R.string.fill_out_the_form_and_import_your_saved_password_file,
    )
    val instruction3 = annotatedStringResource(
        id = R.string.select_import_data_in_the_web_app_then_done_to_finish_syncing,
    )
    val instruction4 = annotatedStringResource(
        id = R.string.for_your_security_be_sure_to_delete_your_saved_password_file,
    )
    ImportLoginsInstructionStep(
        stepText = stringResource(R.string.step_3_of_3),
        stepTitle = stringResource(R.string.import_logins_to_Quant Vault),
        instructions = persistentListOf(
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number1,
                headerText = instruction1,
                subtitleText = null,
            ),
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number2,
                headerText = instruction2,
                subtitleText = null,
            ),
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number3,
                headerText = instruction3,
                subtitleText = null,
            ),
            ContentBlockData(
                iconVectorResource = R.drawable.ic_number4,
                headerText = instruction4,
                subtitleText = null,
            ),
        ),
        onBackClick = onBackClick,
        ctaText = stringResource(R.string.done_text),
        onContinueClick = onContinueClick,
        onHelpClick = onHelpClick,
        modifier = modifier,
    )
}

@Suppress("LongMethod")
@Composable
private fun ImportLoginsSuccessBottomSheetContent(
    onCompleteImportLogins: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Image(
            painter = rememberVectorPainter(R.drawable.ill_secure_devices),
            contentDescription = null,
            modifier = Modifier
                .standardHorizontalMargin()
                .size(124.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.import_successful),
            style = QuantVaultTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(
                R.string.manage_your_logins_from_anywhere_with_Quant Vault_tools,
            ),
            style = QuantVaultTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(Modifier.height(24.dp))
        QuantVaultContentCard(
            contentItems = persistentListOf(
                ContentBlockData(
                    headerText = stringResource(R.string.download_the_browser_extension),
                    subtitleText = stringResource(
                        R.string.go_to_Quant Vault_com_download_to_integrate_Quant Vault_into_browser,
                    ),
                    iconVectorResource = R.drawable.ic_puzzle,
                ),
                ContentBlockData(
                    headerText = stringResource(R.string.use_the_web_app),
                    subtitleText = stringResource(
                        R.string.log_in_at_Quant Vault_com_to_easily_manage_your_account,
                    ),
                    iconVectorResource = R.drawable.ic_desktop,
                ),
                ContentBlockData(
                    headerText = stringResource(R.string.autofill_passwords),
                    subtitleText = stringResource(
                        R.string.set_up_autofill_on_all_your_devices,
                    ),
                    iconVectorResource = R.drawable.ic_shield,
                ),
            ),
            modifier = Modifier.standardHorizontalMargin(),
            contentSubtitleTextStyle = QuantVaultTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(24.dp))
        QuantVaultFilledButton(
            label = stringResource(R.string.got_it),
            onClick = onCompleteImportLogins,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ImportLoginsInitialContent_preview() {
    QuantVaultTheme {
        Column(
            modifier = Modifier.background(
                QuantVaultTheme.colorScheme.background.primary,
            ),
        ) {
            InitialImportLoginsContent(
                onGetStartedClick = {},
                onImportLaterClick = {},
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ImportLoginsScreenDialog_preview(
    @PreviewParameter(ImportLoginsDialogContentPreviewProvider::class) state: ImportLoginsState,
) {
    QuantVaultTheme {
        Column(
            modifier = Modifier.background(
                QuantVaultTheme.colorScheme.background.primary,
            ),
        ) {
            ImportLoginsDialogContent(
                state = state,
                handler = ImportLoginHandler(
                    onDismissDialog = {},
                    onConfirmGetStarted = {},
                    onConfirmImportLater = {},
                    onCloseClick = {},
                    onGetStartedClick = {},
                    onImportLaterClick = {},
                    onHelpClick = {},
                    onMoveToInitialContent = {},
                    onMoveToStepOne = {},
                    onMoveToStepTwo = {},
                    onMoveToStepThree = {},
                    onMoveToSyncInProgress = {},
                    onRetrySync = {},
                    onFailedSyncAcknowledged = {},
                    onSuccessfulSyncAcknowledged = {},
                ),
            )
            InitialImportLoginsContent(
                onGetStartedClick = {},
                onImportLaterClick = {},
            )
        }
    }
}

@OmitFromCoverage
private class ImportLoginsDialogContentPreviewProvider :
    PreviewParameterProvider<ImportLoginsState> {
    override val values: Sequence<ImportLoginsState>
        get() = sequenceOf(
            ImportLoginsState(
                dialogState = ImportLoginsState.DialogState.GetStarted,
                viewState = ImportLoginsState.ViewState.InitialContent,
                showBottomSheet = false,
                currentWebVaultUrl = "vault.Quant Vault.com",
            ),
            ImportLoginsState(
                dialogState = ImportLoginsState.DialogState.ImportLater,
                viewState = ImportLoginsState.ViewState.InitialContent,
                showBottomSheet = false,
                currentWebVaultUrl = "vault.Quant Vault.com",
            ),
        )
}








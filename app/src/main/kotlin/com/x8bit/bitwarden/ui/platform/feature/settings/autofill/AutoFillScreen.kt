@file:Suppress("TooManyFunctions")

package com.x8bit.bitwarden.ui.platform.feature.settings.autofill

import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quantvault.core.util.persistentListOfNotNull
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.annotatedStringResource
import com.bitwarden.ui.platform.base.util.spanStyleOf
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toAnnotatedString
import com.bitwarden.ui.platform.components.appbar.QuantVaultTopAppBar
import com.bitwarden.ui.platform.components.badge.NotificationBadge
import com.bitwarden.ui.platform.components.button.model.QuantVaultHelpButtonData
import com.bitwarden.ui.platform.components.card.QuantVaultActionCard
import com.bitwarden.ui.platform.components.card.QuantVaultActionCardSmall
import com.bitwarden.ui.platform.components.card.actionCardExitAnimation
import com.bitwarden.ui.platform.components.dialog.QuantVaultBasicDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.dropdown.QuantVaultMultiSelectButton
import com.bitwarden.ui.platform.components.dropdown.model.MultiSelectOption
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.row.QuantVaultExternalLinkRow
import com.bitwarden.ui.platform.components.row.QuantVaultPushRow
import com.bitwarden.ui.platform.components.row.QuantVaultTextRow
import com.bitwarden.ui.platform.components.scaffold.QuantVaultScaffold
import com.bitwarden.ui.platform.components.toggle.QuantVaultSwitch
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.composition.LocalIntentManager
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.platform.manager.util.startSystemAccessibilitySettingsActivity
import com.bitwarden.ui.platform.manager.util.startSystemAutofillSettingsActivity
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.data.platform.repository.model.UriMatchType
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.browser.BrowserAutofillSettingsCard
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.handlers.AutoFillHandlers
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.util.displayLabel
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.util.isAdvancedMatching
import com.x8bit.bitwarden.ui.platform.manager.utils.startBrowserAutofillSettingsActivity
import kotlinx.collections.immutable.toImmutableList
import com.x8bit.bitwarden.R

/**
 * Displays the auto-fill screen.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoFillScreen(
    onNavigateBack: () -> Unit,
    viewModel: AutoFillViewModel = hiltViewModel(),
    intentManager: IntentManager = LocalIntentManager.current,
    onNavigateToBlockAutoFillScreen: () -> Unit,
    onNavigateToSetupAutofill: () -> Unit,
    onNavigateToSetupBrowserAutofill: () -> Unit,
    onNavigateToAboutPrivilegedAppsScreen: () -> Unit,
    onNavigateToPrivilegedAppsList: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var shouldShowAutofillFallbackDialog by rememberSaveable { mutableStateOf(false) }
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            AutoFillEvent.NavigateBack -> onNavigateBack.invoke()

            AutoFillEvent.NavigateToAccessibilitySettings -> {
                intentManager.startSystemAccessibilitySettingsActivity()
            }

            AutoFillEvent.NavigateToAutofillSettings -> {
                val isSuccess = intentManager.startSystemAutofillSettingsActivity()
                shouldShowAutofillFallbackDialog = !isSuccess
            }

            AutoFillEvent.NavigateToBlockAutoFill -> {
                onNavigateToBlockAutoFillScreen()
            }

            AutoFillEvent.NavigateToSettings -> {
                intentManager.startCredentialManagerSettings()
            }

            AutoFillEvent.NavigateToSetupAutofill -> onNavigateToSetupAutofill()
            AutoFillEvent.NavigateToSetupBrowserAutofill -> onNavigateToSetupBrowserAutofill()
            is AutoFillEvent.NavigateToBrowserAutofillSettings -> {
                intentManager.startBrowserAutofillSettingsActivity(
                    browserPackage = event.browserPackage,
                )
            }

            AutoFillEvent.NavigateToAboutPrivilegedAppsScreen -> {
                onNavigateToAboutPrivilegedAppsScreen()
            }

            AutoFillEvent.NavigateToPrivilegedAppsListScreen -> {
                onNavigateToPrivilegedAppsList()
            }

            AutoFillEvent.NavigateToLearnMore -> {
                intentManager.launchUri("https://Quant Vault.com/help/uri-match-detection/".toUri())
            }

            AutoFillEvent.NavigateToAutofillHelp -> {
                intentManager.launchUri(
                    uri = "https://Quant Vault.com/help/auto-fill-android-troubleshooting/".toUri(),
                )
            }
        }
    }

    if (shouldShowAutofillFallbackDialog) {
        QuantVaultBasicDialog(
            title = null,
            message = stringResource(id = R.string.Quant Vault_autofill_go_to_settings),
            onDismissRequest = { shouldShowAutofillFallbackDialog = false },
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val autoFillHandlers = remember(viewModel) { AutoFillHandlers.create(viewModel = viewModel) }
    QuantVaultScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuantVaultTopAppBar(
                title = stringResource(id = R.string.autofill_noun),
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = R.drawable.ic_back),
                navigationIconContentDescription = stringResource(id = R.string.back),
                onNavigationIconClick = { viewModel.trySendAction(AutoFillAction.BackClick) },
            )
        },
    ) {
        AutoFillScreenContent(
            state = state,
            autoFillHandlers = autoFillHandlers,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun AutoFillScreenContent(
    state: AutoFillState,
    autoFillHandlers: AutoFillHandlers,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(height = 12.dp))
        AutofillCallToActionCard(
            state = state,
            autoFillHandlers = autoFillHandlers,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        QuantVaultListHeaderText(
            label = stringResource(id = R.string.autofill_noun),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(height = 8.dp))
        val autofillServicesLabel = stringResource(id = R.string.autofill_services)
        QuantVaultSwitch(
            label = autofillServicesLabel,
            supportingText = stringResource(
                id = R.string.autofill_services_explanation_long,
            ),
            contentDescription = if (state.isAutoFillServicesEnabled) {
                autofillServicesLabel
            } else {
                stringResource(
                    id = R.string.external_link_format,
                    formatArgs = arrayOf(autofillServicesLabel),
                )
            },
            isChecked = state.isAutoFillServicesEnabled,
            onCheckedChange = autoFillHandlers.onAutofillServicesClick,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("AutofillServicesSwitch")
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 8.dp))

        AnimatedVisibility(visible = state.showInlineAutofill) {
            Column {
                FillStyleSelector(
                    selectedStyle = state.autofillStyle,
                    onStyleChange = autoFillHandlers.onAutofillStyleChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
            }
        }

        AnimatedVisibility(visible = state.showBrowserSettingOptions) {
            Column {
                BrowserAutofillSettingsCard(
                    options = state.browserAutofillSettingsOptions,
                    onOptionClicked = autoFillHandlers.onBrowserAutofillSelected,
                    supportingText = stringResource(
                        id = R.string.improves_login_filling_for_supported_websites_on_selected_browsers,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (state.showPasskeyManagementRow) {
            QuantVaultExternalLinkRow(
                text = stringResource(id = R.string.passkey_management),
                description = R.string.passkey_management_explanation_long
                    .toAnnotatedString(),
                onConfirmClick = autoFillHandlers.onPasskeyManagementClick,
                dialogTitle = stringResource(id = R.string.continue_to_device_settings),
                dialogMessage = stringResource(
                    id = R.string.set_Quant Vault_as_passkey_manager_description,
                ),
                withDivider = false,
                cardStyle = CardStyle.Top(),
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
            QuantVaultTextRow(
                text = stringResource(R.string.privileged_apps),
                onClick = autoFillHandlers.onPrivilegedAppsClick,
                helpData = QuantVaultHelpButtonData(
                    contentDescription = stringResource(
                        id = R.string.learn_more_about_privileged_apps,
                    ),
                    onClick = autoFillHandlers.onPrivilegedAppsHelpLinkClick,
                    isExternalLink = false,
                ),
                cardStyle = CardStyle.Bottom,
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }
        AccessibilityAutofillSwitch(
            isAccessibilityAutoFillEnabled = state.isAccessibilityAutofillEnabled,
            onCheckedChange = autoFillHandlers.onUseAccessibilityServiceClick,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        QuantVaultListHeaderText(
            label = stringResource(id = R.string.additional_options),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        QuantVaultSwitch(
            label = stringResource(id = R.string.copy_totp_automatically),
            supportingText = stringResource(
                id = R.string.copy_totp_automatically_description,
            ),
            isChecked = state.isCopyTotpAutomaticallyEnabled,
            onCheckedChange = autoFillHandlers.onCopyTotpAutomaticallyClick,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("CopyTotpAutomaticallySwitch")
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        QuantVaultSwitch(
            label = stringResource(id = R.string.ask_to_add_item),
            supportingText = stringResource(id = R.string.ask_to_add_item_description),
            isChecked = state.isAskToAddLoginEnabled,
            onCheckedChange = autoFillHandlers.onAskToAddLoginClick,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("AskToAddLoginSwitch")
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        DefaultUriMatchTypeRow(
            selectedUriMatchType = state.defaultUriMatchType,
            onUriMatchTypeSelect = autoFillHandlers.onDefaultUriMatchTypeSelect,
            onNavigateToLearnMore = autoFillHandlers.onLearnMoreClick,
            modifier = Modifier
                .testTag("DefaultUriMatchDetectionChooser")
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        QuantVaultPushRow(
            text = stringResource(id = R.string.block_auto_fill),
            description = stringResource(
                id = R.string.auto_fill_will_not_be_offered_for_these_ur_is,
            ),
            onClick = autoFillHandlers.onBlockAutoFillClick,
            cardStyle = CardStyle.Full,
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun AutofillCallToActionCard(
    state: AutoFillState,
    autoFillHandlers: AutoFillHandlers,
    modifier: Modifier,
) {
    AnimatedContent(
        targetState = state.ctaState,
        label = "AutofillActionCard",
        transitionSpec = { EnterTransition.None.togetherWith(actionCardExitAnimation()) },
        modifier = modifier,
    ) {
        when (it) {
            CtaState.AUTOFILL -> {
                QuantVaultActionCard(
                    cardTitle = stringResource(id = R.string.turn_on_autofill),
                    actionText = stringResource(id = R.string.get_started),
                    onActionClick = autoFillHandlers.onAutofillActionCardClick,
                    onDismissClick = autoFillHandlers.onAutofillActionCardDismissClick,
                    leadingContent = { NotificationBadge(notificationCount = 1) },
                )
            }

            CtaState.BROWSER_AUTOFILL -> {
                val subTitleRes = if (state.browserCount > 1) {
                    R.string.browser_requires_special_permissions_for_Quant Vault_plural
                } else {
                    R.string.browser_requires_special_permissions_for_Quant Vault_singular
                }
                QuantVaultActionCard(
                    cardTitle = stringResource(
                        id = R.string.turn_on_browser_autofill_integration,
                    ),
                    cardSubtitle = stringResource(id = subTitleRes),
                    actionText = stringResource(id = R.string.get_started),
                    onActionClick = autoFillHandlers.onBrowserAutofillActionCardClick,
                    onDismissClick = autoFillHandlers.onBrowserAutofillActionCardDismissClick,
                    leadingContent = { NotificationBadge(notificationCount = 1) },
                )
            }

            CtaState.DEFAULT -> {
                QuantVaultActionCardSmall(
                    actionIcon = rememberVectorPainter(id = R.drawable.ic_question_circle),
                    actionText = stringResource(id = R.string.having_trouble_with_autofill),
                    callToActionText = stringResource(
                        id = R.string.access_help_and_troubleshooting_documentation_here,
                    ),
                    onCardClicked = autoFillHandlers.onHelpCardClick,
                    trailingContent = {
                        Icon(
                            painter = rememberVectorPainter(R.drawable.ic_chevron_right),
                            contentDescription = stringResource(id = R.string.external_link),
                            tint = QuantVaultTheme.colorScheme.icon.primary,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun FillStyleSelector(
    selectedStyle: AutofillStyle,
    onStyleChange: (AutofillStyle) -> Unit,
    modifier: Modifier = Modifier,
    resources: Resources = LocalResources.current,
) {
    QuantVaultMultiSelectButton(
        label = stringResource(id = R.string.display_autofill_suggestions),
        supportingText = stringResource(id = R.string.use_inline_autofill_explanation_long),
        options = AutofillStyle.entries.map { it.label() }.toImmutableList(),
        selectedOption = selectedStyle.label(),
        onOptionSelected = {
            onStyleChange(AutofillStyle.entries.first { style -> style.label(resources) == it })
        },
        cardStyle = CardStyle.Full,
        modifier = modifier.testTag(tag = "InlineAutofillSelector"),
    )
}

@Composable
private fun AccessibilityAutofillSwitch(
    isAccessibilityAutoFillEnabled: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldShowDialog by rememberSaveable { mutableStateOf(value = false) }
    QuantVaultSwitch(
        label = stringResource(id = R.string.accessibility),
        supportingText = stringResource(id = R.string.accessibility_description5),
        isChecked = isAccessibilityAutoFillEnabled,
        onCheckedChange = {
            if (isAccessibilityAutoFillEnabled) {
                onCheckedChange()
            } else {
                shouldShowDialog = true
            }
        },
        cardStyle = CardStyle.Full,
        modifier = modifier.testTag(tag = "AccessibilityAutofillSwitch"),
    )

    if (shouldShowDialog) {
        QuantVaultTwoButtonDialog(
            title = stringResource(id = R.string.accessibility_service_disclosure),
            message = stringResource(id = R.string.accessibility_disclosure_text),
            confirmButtonText = stringResource(id = R.string.accept),
            dismissButtonText = stringResource(id = R.string.decline),
            onConfirmClick = {
                onCheckedChange()
                shouldShowDialog = false
            },
            onDismissClick = { shouldShowDialog = false },
            onDismissRequest = { shouldShowDialog = false },
        )
    }
}

@Composable
private fun DefaultUriMatchTypeRow(
    selectedUriMatchType: UriMatchType,
    onUriMatchTypeSelect: (UriMatchType) -> Unit,
    onNavigateToLearnMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAdvancedDialog by rememberSaveable { mutableStateOf(false) }
    var optionPendingConfirmation by rememberSaveable { mutableStateOf<UriMatchType?>(null) }
    var shouldShowLearnMoreMatchDetectionDialog by rememberSaveable { mutableStateOf(false) }

    UriMatchSelectionButton(
        selectedUriMatchType = selectedUriMatchType,
        onOptionSelected = { selectedOption ->
            if (selectedOption.isAdvancedMatching()) {
                optionPendingConfirmation = selectedOption
                showAdvancedDialog = true
            } else {
                onUriMatchTypeSelect(selectedOption)
                optionPendingConfirmation = null
                showAdvancedDialog = false
            }
        },
        modifier = modifier,
    )

    val currentOptionToConfirm = optionPendingConfirmation
    if (showAdvancedDialog && currentOptionToConfirm != null) {
        AdvancedMatchDetectionWarningDialog(
            pendingOption = currentOptionToConfirm,
            onDialogConfirm = {
                onUriMatchTypeSelect(currentOptionToConfirm)
                showAdvancedDialog = false
                optionPendingConfirmation = null
                shouldShowLearnMoreMatchDetectionDialog = true
            },
            onDialogDismiss = {
                showAdvancedDialog = false
                optionPendingConfirmation = null
            },
        )
    }

    if (shouldShowLearnMoreMatchDetectionDialog) {
        MatchDetectionLearnMoreDialog(
            uriMatchType = selectedUriMatchType,
            onDialogConfirm = {
                onNavigateToLearnMore()
                shouldShowLearnMoreMatchDetectionDialog = false
            },
            onDialogDismiss = {
                shouldShowLearnMoreMatchDetectionDialog = false
            },
        )
    }
}

@Composable
private fun AdvancedMatchDetectionWarningDialog(
    pendingOption: UriMatchType,
    onDialogConfirm: () -> Unit,
    onDialogDismiss: () -> Unit,
) {
    val descriptionStringResId =
        when (pendingOption) {
            UriMatchType.STARTS_WITH -> {
                R.string.advanced_option_with_increased_risk_of_exposing_credentials
            }

            UriMatchType.REGULAR_EXPRESSION -> {
                R.string.advanced_option_increased_risk_exposing_credentials_used_incorrectly
            }

            UriMatchType.HOST,
            UriMatchType.DOMAIN,
            UriMatchType.EXACT,
            UriMatchType.NEVER,
                -> {
                error("Unexpected value $pendingOption on AdvancedMatchDetectionWarningDialog")
            }
        }

    QuantVaultTwoButtonDialog(
        title = stringResource(
            id = R.string.are_you_sure_you_want_to_use,
            formatArgs = arrayOf(
                pendingOption.displayLabel(),
            ),
        ),
        message = stringResource(
            id = descriptionStringResId,
        ),
        confirmButtonText = stringResource(id = R.string.yes),
        dismissButtonText = stringResource(id = R.string.cancel),
        onConfirmClick = onDialogConfirm,
        onDismissClick = onDialogDismiss,
        onDismissRequest = onDialogDismiss,
    )
}

@Composable
private fun UriMatchSelectionButton(
    selectedUriMatchType: UriMatchType,
    onOptionSelected: (UriMatchType) -> Unit,
    modifier: Modifier = Modifier,
    resources: Resources = LocalResources.current,
) {
    val advancedOptions = UriMatchType.entries.filter { it.isAdvancedMatching() }
    val options = persistentListOfNotNull(
        *UriMatchType
            .entries
            .filter { !it.isAdvancedMatching() }
            .map { MultiSelectOption.Row(it.displayLabel()) }
            .toTypedArray(),
        if (advancedOptions.isNotEmpty()) {
            MultiSelectOption.Header(
                title = stringResource(id = R.string.advanced_options),
                testTag = "AdvancedOptionsSection",
            )
        } else {
            null
        },
        *advancedOptions
            .map { MultiSelectOption.Row(it.displayLabel()) }
            .toTypedArray(),
    )

    QuantVaultMultiSelectButton(
        label = stringResource(id = R.string.default_uri_match_detection),
        options = options,
        selectedOption = MultiSelectOption.Row(selectedUriMatchType.displayLabel()),
        onOptionSelected = { row ->
            val newSelectedType = UriMatchType
                .entries
                .first { it.displayLabel(resources) == row.title }
            onOptionSelected(newSelectedType)
        },
        cardStyle = CardStyle.Full,
        supportingContent = { SupportingTextForMatchDetection(selectedUriMatchType) },
        modifier = modifier,
    )
}

@Composable
private fun MatchDetectionLearnMoreDialog(
    uriMatchType: UriMatchType,
    onDialogConfirm: () -> Unit,
    onDialogDismiss: () -> Unit,
) {
    QuantVaultTwoButtonDialog(
        title = stringResource(id = R.string.keep_your_credential_secure),
        message = stringResource(
            id = R.string.learn_more_about_how_to_keep_credentirals_secure,
            formatArgs = arrayOf(uriMatchType.displayLabel()),
        ),
        confirmButtonText = stringResource(id = R.string.learn_more),
        dismissButtonText = stringResource(id = R.string.close),
        onConfirmClick = onDialogConfirm,
        onDismissClick = onDialogDismiss,
        onDismissRequest = onDialogDismiss,
    )
}

@Composable
private fun SupportingTextForMatchDetection(
    uriMatchType: UriMatchType,
) {
    val stringResId =
        when (uriMatchType) {
            UriMatchType.STARTS_WITH -> {
                R.string.default_uri_match_detection_description_advanced_options
            }

            UriMatchType.REGULAR_EXPRESSION -> {
                R.string.default_uri_match_detection_description_advanced_options_incorrectly
            }

            UriMatchType.HOST,
            UriMatchType.DOMAIN,
            UriMatchType.EXACT,
            UriMatchType.NEVER,
                -> {
                R.string.default_uri_match_detection_description
            }
        }

    val supportingAnnotatedString =
        annotatedStringResource(
            id = stringResId,
            emphasisHighlightStyle = spanStyleOf(
                textStyle = QuantVaultTheme.typography.bodyMediumEmphasis,
                color = QuantVaultTheme.colorScheme.text.secondary,
            ),
            style = spanStyleOf(
                textStyle = QuantVaultTheme.typography.bodySmall,
                color = QuantVaultTheme.colorScheme.text.secondary,
            ),
        )

    Text(
        text = supportingAnnotatedString,
        style = QuantVaultTheme.typography.bodySmall,
        color = QuantVaultTheme.colorScheme.text.secondary,
        modifier = Modifier.fillMaxWidth(),
    )
}








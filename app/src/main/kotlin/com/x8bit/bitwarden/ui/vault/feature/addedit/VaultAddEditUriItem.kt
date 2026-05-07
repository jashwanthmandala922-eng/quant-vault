package com.x8bit.bitwarden.ui.vault.feature.addedit

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.quantvault.core.util.persistentListOfNotNull
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.dialog.QuantVaultSelectionDialog
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.dialog.row.QuantVaultBasicDialogRow
import com.bitwarden.ui.platform.components.dropdown.QuantVaultMultiSelectDialogContent
import com.bitwarden.ui.platform.components.dropdown.model.MultiSelectOption
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.x8bit.bitwarden.data.platform.repository.model.UriMatchType
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.util.displayLabel
import com.x8bit.bitwarden.ui.vault.feature.addedit.model.UriItem
import com.x8bit.bitwarden.ui.vault.feature.addedit.model.UriMatchDisplayType
import com.x8bit.bitwarden.ui.vault.feature.addedit.util.displayLabel
import com.x8bit.bitwarden.ui.vault.feature.addedit.util.isAdvancedMatching
import com.x8bit.bitwarden.ui.vault.feature.addedit.util.toDisplayMatchType
import com.x8bit.bitwarden.ui.vault.feature.addedit.util.toUriMatchType
import kotlinx.collections.immutable.ImmutableList
import com.x8bit.bitwarden.R

/**
 * The URI item displayed to the user.
 */
@Suppress("LongMethod")
@Composable
fun VaultAddEditUriItem(
    uriItem: UriItem,
    onUriItemRemoved: (UriItem) -> Unit,
    onUriValueChange: (UriItem) -> Unit,
    cardStyle: CardStyle,
    defaultUriMatchType: UriMatchType,
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
    resources: Resources = LocalResources.current,
) {
    var shouldShowOptionsDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowMatchDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowAdvancedMatchDialog by rememberSaveable { mutableStateOf(false) }
    var optionPendingConfirmation by rememberSaveable { mutableStateOf<UriMatchDisplayType?>(null) }
    var shouldShowLearnMoreMatchDetectionDialog by rememberSaveable { mutableStateOf(false) }
    val defaultUriOption = remember(defaultUriMatchType) {
        defaultUriMatchType.displayLabel.toString(resources)
    }

    QuantVaultTextField(
        label = stringResource(id = R.string.website_uri),
        value = uriItem.uri.orEmpty(),
        onValueChange = { onUriValueChange(uriItem.copy(uri = it)) },
        singleLine = false,
        keyboardType = KeyboardType.Uri,
        actions = {
            QuantVaultStandardIconButton(
                vectorIconRes = R.drawable.ic_cog,
                contentDescription = stringResource(id = R.string.more_options),
                onClick = { shouldShowOptionsDialog = true },
                modifier = Modifier.testTag(tag = "LoginUriOptionsButton"),
            )
        },
        textFieldTestTag = "LoginUriEntry",
        cardStyle = cardStyle,
        modifier = modifier,
    )

    if (shouldShowOptionsDialog) {
        QuantVaultSelectionDialog(
            title = stringResource(id = R.string.options),
            onDismissRequest = { shouldShowOptionsDialog = false },
        ) {
            QuantVaultBasicDialogRow(
                text = stringResource(id = R.string.match_detection),
                onClick = {
                    shouldShowOptionsDialog = false
                    shouldShowMatchDialog = true
                },
            )
            QuantVaultBasicDialogRow(
                text = stringResource(id = R.string.remove),
                onClick = {
                    shouldShowOptionsDialog = false
                    onUriItemRemoved(uriItem)
                },
            )
        }
    }

    if (shouldShowMatchDialog) {
        QuantVaultSelectionDialog(
            title = stringResource(id = R.string.uri_match_detection),
            onDismissRequest = { shouldShowMatchDialog = false },
        ) {
            QuantVaultMultiSelectDialogContent(
                options = uriMatchingOptions(defaultUriOption = defaultUriOption),
                selectedOption = MultiSelectOption.Row(
                    uriItem
                        .match
                        .toDisplayMatchType()
                        .displayLabel(defaultUriOption = defaultUriOption)
                        .invoke(),
                ),
                onOptionSelected = { selectedOption ->
                    shouldShowMatchDialog = false

                    val newSelectedType =
                        UriMatchDisplayType
                            .entries
                            .first {
                                it.displayLabel(defaultUriOption)
                                    .invoke(resources) == selectedOption.title
                            }

                    if (newSelectedType.isAdvancedMatching()) {
                        optionPendingConfirmation = newSelectedType
                        shouldShowAdvancedMatchDialog = true
                    } else {
                        onUriValueChange(
                            uriItem.copy(match = newSelectedType.toUriMatchType()),
                        )
                        optionPendingConfirmation = null
                    }
                },
            )
        }
    }

    val currentOptionToConfirm = optionPendingConfirmation

    if (shouldShowAdvancedMatchDialog && currentOptionToConfirm != null) {
        AdvancedMatchDetectionWarning(
            pendingOption = currentOptionToConfirm,
            defaultUriMatchType = defaultUriMatchType,
            onDialogConfirm = {
                onUriValueChange(
                    uriItem.copy(match = currentOptionToConfirm.toUriMatchType()),
                )
                shouldShowAdvancedMatchDialog = false
                optionPendingConfirmation = null
                shouldShowLearnMoreMatchDetectionDialog = true
            },
            onDialogDismiss = {
                shouldShowAdvancedMatchDialog = false
                optionPendingConfirmation = null
            },
        )
    }

    if (shouldShowLearnMoreMatchDetectionDialog) {
        LearnMoreAboutMatchDetectionDialog(
            uriMatchDisplayType = uriItem.match.toDisplayMatchType(),
            defaultUriOption = defaultUriOption,
            onDialogConfirm = {
                onLearnMoreClick()
                shouldShowLearnMoreMatchDetectionDialog = false
            },
            onDialogDismiss = {
                shouldShowLearnMoreMatchDetectionDialog = false
            },
        )
    }
}

@Composable
private fun AdvancedMatchDetectionWarning(
    pendingOption: UriMatchDisplayType,
    defaultUriMatchType: UriMatchType,
    onDialogConfirm: () -> Unit,
    onDialogDismiss: () -> Unit,
) {

    val descriptionStringResId = when (pendingOption) {
        UriMatchDisplayType.STARTS_WITH -> {
            R.string.advanced_option_with_increased_risk_of_exposing_credentials
        }

        UriMatchDisplayType.REGULAR_EXPRESSION -> {
            R.string.advanced_option_increased_risk_exposing_credentials_used_incorrectly
        }

        UriMatchDisplayType.DEFAULT,
        UriMatchDisplayType.HOST,
        UriMatchDisplayType.BASE_DOMAIN,
        UriMatchDisplayType.EXACT,
        UriMatchDisplayType.NEVER,
            ->
            error("Unexpected option on AdvancedMatchDetectionWarning")
    }

    val nameOfSelectedMatchDisplayType = pendingOption
        .displayLabel(defaultUriOption = defaultUriMatchType.displayLabel())
        .invoke()

    QuantVaultTwoButtonDialog(
        title = stringResource(
            id = R.string.are_you_sure_you_want_to_use,
            formatArgs = arrayOf(nameOfSelectedMatchDisplayType),
        ),
        message = stringResource(
            id = descriptionStringResId,
            formatArgs = arrayOf(nameOfSelectedMatchDisplayType),
        ),
        confirmButtonText = stringResource(id = R.string.yes),
        dismissButtonText = stringResource(id = R.string.cancel),
        onConfirmClick = onDialogConfirm,
        onDismissClick = onDialogDismiss,
        onDismissRequest = onDialogDismiss,
    )
}

@Composable
private fun LearnMoreAboutMatchDetectionDialog(
    uriMatchDisplayType: UriMatchDisplayType,
    defaultUriOption: String,
    onDialogConfirm: () -> Unit,
    onDialogDismiss: () -> Unit,
) {
    QuantVaultTwoButtonDialog(
        title = stringResource(id = R.string.keep_your_credential_secure),
        message = stringResource(
            id = R.string.learn_more_about_how_to_keep_credentirals_secure,
            formatArgs = arrayOf(
                uriMatchDisplayType
                    .displayLabel(
                        defaultUriOption = defaultUriOption,
                    )
                    .invoke(),
            ),
        ),
        confirmButtonText = stringResource(id = R.string.learn_more),
        dismissButtonText = stringResource(id = R.string.close),
        onConfirmClick = onDialogConfirm,
        onDismissClick = onDialogDismiss,
        onDismissRequest = onDialogDismiss,
    )
}

@Composable
private fun uriMatchingOptions(defaultUriOption: String): ImmutableList<MultiSelectOption> {
    val advancedOptions = UriMatchDisplayType.entries.filter { it.isAdvancedMatching() }
    return persistentListOfNotNull(
        *UriMatchDisplayType
            .entries
            .filter { !it.isAdvancedMatching() }
            .map { MultiSelectOption.Row(it.displayLabel(defaultUriOption).invoke()) }
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
            .map { MultiSelectOption.Row(it.displayLabel(defaultUriOption).invoke()) }
            .toTypedArray(),
    )
}







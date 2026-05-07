package com.x8bit.bitwarden.ui.tools.feature.send.addedit

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.animation.AnimateNullableContentVisibility
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedButton
import com.bitwarden.ui.platform.components.button.QuantVaultOutlinedErrorButton
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.card.QuantVaultInfoCalloutCard
import com.bitwarden.ui.platform.components.dialog.QuantVaultTwoButtonDialog
import com.bitwarden.ui.platform.components.field.QuantVaultPasswordField
import com.bitwarden.ui.platform.components.field.QuantVaultTextField
import com.bitwarden.ui.platform.components.header.QuantVaultExpandingHeader
import com.bitwarden.ui.platform.components.header.QuantVaultListHeaderText
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.stepper.QuantVaultStepper
import com.bitwarden.ui.platform.components.toggle.QuantVaultSwitch
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.ui.platform.manager.permissions.PermissionsManager
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.components.AddEditSendAuthTypeChooser
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.components.AddEditSendCustomDateChooser
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.components.AddEditSendDeletionDateChooser
import com.x8bit.bitwarden.ui.tools.feature.send.addedit.handlers.AddEditSendHandlers
import com.x8bit.bitwarden.R

/**
 * Content view for the [AddEditSendScreen].
 */
@Suppress("LongMethod")
@Composable
fun AddEditSendContent(
    state: AddEditSendState.ViewState.Content,
    policyDisablesSend: Boolean,
    policySendOptionsInEffect: Boolean,
    isAddMode: Boolean,
    isShared: Boolean,
    addSendHandlers: AddEditSendHandlers,
    permissionsManager: PermissionsManager,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))
        if (policyDisablesSend) {
            QuantVaultInfoCalloutCard(
                text = stringResource(id = R.string.send_disabled_warning),
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth()
                    .testTag("SendPolicyInEffectLabel"),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (policySendOptionsInEffect) {
            QuantVaultInfoCalloutCard(
                text = stringResource(id = R.string.send_options_policy_in_effect),
                modifier = Modifier
                    .testTag(tag = "SendPolicyInEffectLabel")
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        QuantVaultListHeaderText(
            label = stringResource(id = R.string.send_details),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(height = 8.dp))
        QuantVaultTextField(
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
            label = stringResource(id = R.string.send_name_required),
            readOnly = policyDisablesSend,
            value = state.common.name,
            onValueChange = addSendHandlers.onNameChange,
            textFieldTestTag = "SendNameEntry",
            cardStyle = CardStyle.Full,
        )

        when (val type = state.selectedType) {
            is AddEditSendState.ViewState.Content.SendType.File -> {
                FileTypeContent(
                    fileType = type,
                    addSendHandlers = addSendHandlers,
                    permissionsManager = permissionsManager,
                    isAddMode = isAddMode,
                    isShared = isShared,
                )
            }

            is AddEditSendState.ViewState.Content.SendType.Text -> {
                TextTypeContent(
                    textType = type,
                    addSendHandlers = addSendHandlers,
                    policyDisablesSend = policyDisablesSend,
                )
            }
        }

        Spacer(modifier = Modifier.height(height = 8.dp))

        if (isAddMode) {
            AddEditSendDeletionDateChooser(
                onDateSelect = addSendHandlers.onDeletionDateChange,
                isEnabled = !policyDisablesSend,
                modifier = Modifier
                    .testTag("SendDeletionOptionsPicker")
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        } else {
            AddEditSendCustomDateChooser(
                originalSelection = state.common.deletionDate,
                isEnabled = !policyDisablesSend,
                onDateSelect = addSendHandlers.onDeletionDateChange,
                modifier = Modifier
                    .testTag("SendCustomDeletionDatePicker")
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        if (state.common.isSendEmailVerificationEnabled) {
            Spacer(modifier = Modifier.height(height = 8.dp))
            AddEditSendAuthTypeChooser(
                sendAuth = state.common.sendAuth,
                onAuthTypeSelect = addSendHandlers.onAuthTypeSelect,
                onPasswordChange = addSendHandlers.onAuthPasswordChange,
                onEmailValueChange = addSendHandlers.onEmailValueChange,
                onRemoveEmailClick = addSendHandlers.onEmailsRemoveClick,
                onAddNewEmailClick = addSendHandlers.onAddNewEmailClick,
                onOpenPasswordGeneratorClick = addSendHandlers.onOpenPasswordGeneratorClick,
                onPasswordCopyClick = addSendHandlers.onPasswordCopyClick,
                password = state.common.passwordInput,
                isEnabled = !policyDisablesSend,
                isSendsRestrictedByPolicy = policyDisablesSend,
                modifier = Modifier
                    .testTag("SendAuthTypeChooser")
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        AddEditSendOptions(
            state = state,
            isSendsRestrictedByPolicy = policyDisablesSend,
            isAddMode = isAddMode,
            addSendHandlers = addSendHandlers,
        )

        if (!isAddMode) {
            DeleteButton(
                onDeleteClick = addSendHandlers.onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
        }

        Spacer(modifier = Modifier.height(height = 12.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun DeleteButton(
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldShowDeleteConfirmationDialog by rememberSaveable { mutableStateOf(value = false) }
    if (shouldShowDeleteConfirmationDialog) {
        QuantVaultTwoButtonDialog(
            title = stringResource(id = R.string.delete),
            message = stringResource(id = R.string.are_you_sure_delete_send),
            confirmButtonText = stringResource(id = R.string.yes),
            dismissButtonText = stringResource(id = R.string.cancel),
            onConfirmClick = {
                onDeleteClick()
                shouldShowDeleteConfirmationDialog = false
            },
            onDismissClick = { shouldShowDeleteConfirmationDialog = false },
            onDismissRequest = { shouldShowDeleteConfirmationDialog = false },
        )
    }
    QuantVaultOutlinedErrorButton(
        label = stringResource(id = R.string.delete_send),
        onClick = { shouldShowDeleteConfirmationDialog = true },
        icon = rememberVectorPainter(id = R.drawable.ic_trash_small),
        modifier = modifier,
    )
}

@Composable
private fun ColumnScope.TextTypeContent(
    textType: AddEditSendState.ViewState.Content.SendType.Text,
    addSendHandlers: AddEditSendHandlers,
    policyDisablesSend: Boolean,
) {
    Spacer(modifier = Modifier.height(height = 8.dp))
    QuantVaultTextField(
        label = stringResource(id = R.string.text_to_share),
        readOnly = policyDisablesSend,
        value = textType.input,
        singleLine = false,
        onValueChange = addSendHandlers.onTextChange,
        textFieldTestTag = "SendTextContentEntry",
        cardStyle = CardStyle.Full,
        modifier = Modifier
            .fillMaxWidth()
            .standardHorizontalMargin(),
    )
    Spacer(modifier = Modifier.height(height = 8.dp))
    QuantVaultSwitch(
        label = stringResource(id = R.string.hide_text_by_default),
        isChecked = textType.isHideByDefaultChecked,
        onCheckedChange = addSendHandlers.onIsHideByDefaultToggle,
        readOnly = policyDisablesSend,
        cardStyle = CardStyle.Full,
        modifier = Modifier
            .testTag(tag = "SendHideTextByDefaultToggle")
            .fillMaxWidth()
            .standardHorizontalMargin(),
    )
}

@Suppress("LongMethod")
@Composable
private fun ColumnScope.FileTypeContent(
    fileType: AddEditSendState.ViewState.Content.SendType.File,
    addSendHandlers: AddEditSendHandlers,
    permissionsManager: PermissionsManager,
    isAddMode: Boolean,
    isShared: Boolean,
) {
    val chooseFileCameraPermissionLauncher = permissionsManager.getLauncher { isGranted ->
        addSendHandlers.onChooseFileClick(isGranted)
    }
    Spacer(modifier = Modifier.height(height = 8.dp))
    if (isShared) {
        Text(
            text = fileType.name.orEmpty(),
            color = QuantVaultTheme.colorScheme.text.primary,
            style = QuantVaultTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(height = 8.dp))
        Text(
            text = stringResource(id = R.string.required_max_file_size),
            color = QuantVaultTheme.colorScheme.text.secondary,
            style = QuantVaultTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
    } else if (isAddMode) {
        fileType.name?.let {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .defaultMinSize(minHeight = 60.dp)
                    .cardStyle(cardStyle = CardStyle.Full, paddingHorizontal = 16.dp),
            ) {
                Text(
                    text = it,
                    color = QuantVaultTheme.colorScheme.text.primary,
                    style = QuantVaultTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(tag = "SendCurrentFileNameLabel"),
                )
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
        }
        QuantVaultOutlinedButton(
            label = stringResource(id = R.string.choose_file),
            onClick = {
                if (permissionsManager.checkPermission(permission = Manifest.permission.CAMERA)) {
                    addSendHandlers.onChooseFileClick(true)
                } else {
                    chooseFileCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            isExternalLink = true,
            modifier = Modifier
                .testTag(tag = "SendChooseFileButton")
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(height = 8.dp))
        Text(
            text = stringResource(id = R.string.required_max_file_size),
            color = QuantVaultTheme.colorScheme.text.secondary,
            style = QuantVaultTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .padding(horizontal = 16.dp),
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin()
                .defaultMinSize(minHeight = 60.dp)
                .cardStyle(cardStyle = CardStyle.Full, paddingHorizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = fileType.name.orEmpty(),
                color = QuantVaultTheme.colorScheme.text.primary,
                style = QuantVaultTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(weight = 1f),
            )
            Spacer(modifier = Modifier.width(width = 8.dp))
            Text(
                text = fileType.displaySize.orEmpty(),
                color = QuantVaultTheme.colorScheme.text.secondary,
                style = QuantVaultTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * Displays a collapsable set of new send options.
 *
 * @param state The content state.
 * @param isSendsRestrictedByPolicy When `true`, indicates that there's a policy preventing the user
 * from editing or creating sends.
 * @param isAddMode When `true`, indicates that we are creating a new send and `false` when editing
 * an existing send.
 * @param addSendHandlers THe handlers various events.
 */
@Suppress("LongMethod")
@Composable
private fun AddEditSendOptions(
    state: AddEditSendState.ViewState.Content,
    isSendsRestrictedByPolicy: Boolean,
    isAddMode: Boolean,
    addSendHandlers: AddEditSendHandlers,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var shouldShowDialog by rememberSaveable { mutableStateOf(false) }
    QuantVaultExpandingHeader(
        isExpanded = isExpanded,
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .testTag(tag = "SendShowHideOptionsButton")
            .standardHorizontalMargin()
            .fillMaxWidth(),
    )
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        modifier = Modifier.clipToBounds(),
    ) {
        Column {
            QuantVaultStepper(
                label = stringResource(id = R.string.maximum_access_count),
                supportingContent = {
                    Text(
                        text = stringResource(id = R.string.maximum_access_count_info),
                        style = QuantVaultTheme.typography.bodySmall,
                        color = QuantVaultTheme.colorScheme.text.secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    AnimateNullableContentVisibility(
                        targetState = state
                            .common
                            .currentAccessCount
                            ?.takeUnless { isAddMode || state.common.maxAccessCount == null },
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(
                                    id = R.string.current_access_count,
                                    formatArgs = arrayOf(it),
                                ),
                                style = QuantVaultTheme.typography.bodySmall,
                                color = QuantVaultTheme.colorScheme.text.secondary,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                },
                value = state.common.maxAccessCount,
                onValueChange = addSendHandlers.onMaxAccessCountChange,
                isDecrementEnabled = state.common.maxAccessCount != null &&
                    !isSendsRestrictedByPolicy,
                isIncrementEnabled = !isSendsRestrictedByPolicy,
                range = 0..Int.MAX_VALUE,
                textFieldReadOnly = isSendsRestrictedByPolicy,
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .testTag("SendMaxAccessCountEntry")
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )

            if (!state.common.isSendEmailVerificationEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                QuantVaultPasswordField(
                    label = stringResource(id = R.string.new_password),
                    supportingText = stringResource(id = R.string.password_info),
                    readOnly = isSendsRestrictedByPolicy,
                    value = state.common.passwordInput,
                    onValueChange = addSendHandlers.onPasswordChange,
                    passwordFieldTestTag = "SendNewPasswordEntry",
                    cardStyle = CardStyle.Full,
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardHorizontalMargin(),
                ) {
                    QuantVaultStandardIconButton(
                        vectorIconRes = R.drawable.ic_generate,
                        contentDescription = stringResource(id = R.string.generate_password),
                        onClick = {
                            if (state.common.passwordInput.isEmpty()) {
                                addSendHandlers.onOpenPasswordGeneratorClick()
                            } else {
                                shouldShowDialog = true
                            }
                        },
                        modifier = Modifier.testTag(tag = "RegeneratePasswordButton"),
                    )
                    QuantVaultStandardIconButton(
                        vectorIconRes = R.drawable.ic_copy,
                        contentDescription = stringResource(id = R.string.copy_password),
                        isEnabled = state.common.passwordInput.isNotEmpty(),
                        onClick = {
                            addSendHandlers.onPasswordCopyClick(state.common.passwordInput)
                        },
                        modifier = Modifier.testTag(tag = "CopyPasswordButton"),
                    )
                }
            }
            if (shouldShowDialog) {
                QuantVaultTwoButtonDialog(
                    title = stringResource(id = R.string.password),
                    message = stringResource(id = R.string.password_override_alert),
                    confirmButtonText = stringResource(id = R.string.yes),
                    dismissButtonText = stringResource(id = R.string.no),
                    onConfirmClick = {
                        shouldShowDialog = false
                        addSendHandlers.onOpenPasswordGeneratorClick()
                    },
                    onDismissClick = {
                        shouldShowDialog = false
                    },
                    onDismissRequest = {
                        shouldShowDialog = false
                    },
                )
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
            QuantVaultSwitch(
                modifier = Modifier
                    .testTag("SendHideEmailSwitch")
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
                label = stringResource(id = R.string.hide_email),
                isChecked = state.common.isHideEmailChecked,
                onCheckedChange = addSendHandlers.onHideEmailToggle,
                readOnly = isSendsRestrictedByPolicy,
                enabled = state.common.isHideEmailChecked || state.common.isHideEmailAddressEnabled,
                cardStyle = CardStyle.Full,
            )
            Spacer(modifier = Modifier.height(8.dp))
            QuantVaultTextField(
                label = stringResource(id = R.string.private_notes),
                readOnly = isSendsRestrictedByPolicy,
                value = state.common.noteInput,
                singleLine = false,
                onValueChange = addSendHandlers.onNoteChange,
                cardStyle = CardStyle.Full,
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
        }
    }
}







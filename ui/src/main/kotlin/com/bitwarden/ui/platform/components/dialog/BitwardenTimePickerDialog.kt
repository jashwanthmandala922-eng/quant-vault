package com.quantvault.ui.platform.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.quantvault.ui.platform.components.button.quantvaultStandardIconButton
import com.quantvault.ui.platform.components.button.quantvaultTextButton
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A custom composable representing a dialog that displays the time picker dialog.
 *
 * @param initialHour The initial hour to display.
 * @param initialMinute The initial minute to display.
 * @param onTimeSelect The callback to be invoked when a new time is selected.
 * @param onDismissRequest The callback to be invoked when a time has been selected.
 * @param is24Hour Indicates if the time selector should use a 24 hour format or a 12 hour format
 * with AM/PM.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun quantvaultTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelect: (hour: Int, minute: Int) -> Unit,
    onDismissRequest: () -> Unit,
    is24Hour: Boolean,
) {
    var showTimeInput by rememberSaveable { mutableStateOf(value = false) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
    TimePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            quantvaultTextButton(
                modifier = Modifier.testTag(tag = "AcceptAlertButton"),
                label = stringResource(id = quantvaultString.okay),
                onClick = { onTimeSelect(timePickerState.hour, timePickerState.minute) },
            )
        },
        dismissButton = {
            quantvaultTextButton(
                modifier = Modifier.testTag(tag = "DismissAlertButton"),
                label = stringResource(id = quantvaultString.cancel),
                onClick = onDismissRequest,
            )
        },
        inputToggleButton = {
            quantvaultStandardIconButton(
                vectorIconRes = quantvaultDrawable.ic_keyboard,
                contentDescription = stringResource(id = quantvaultString.switch_input_mode),
                onClick = { showTimeInput = !showTimeInput },
            )
        },
    ) {
        val modifier = Modifier.weight(1f)
        if (showTimeInput) {
            TimeInput(
                state = timePickerState,
                colors = quantvaultTimePickerColors(),
                modifier = modifier,
            )
        } else {
            TimePicker(
                state = timePickerState,
                colors = quantvaultTimePickerColors(),
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun quantvaultTimePickerColors(): TimePickerColors = TimePickerColors(
    clockDialColor = QuantVaultTheme.colorScheme.filledButton.backgroundReversed,
    selectorColor = QuantVaultTheme.colorScheme.filledButton.background,
    containerColor = QuantVaultTheme.colorScheme.filledButton.foreground,
    clockDialSelectedContentColor = QuantVaultTheme.colorScheme.background.secondary,
    clockDialUnselectedContentColor = QuantVaultTheme.colorScheme.text.primary,
    periodSelectorBorderColor = QuantVaultTheme.colorScheme.stroke.divider,
    periodSelectorSelectedContainerColor = QuantVaultTheme
        .colorScheme
        .filledButton
        .backgroundReversed,
    periodSelectorUnselectedContainerColor = QuantVaultTheme.colorScheme.background.primary,
    periodSelectorSelectedContentColor = QuantVaultTheme.colorScheme.filledButton.foregroundReversed,
    periodSelectorUnselectedContentColor = QuantVaultTheme.colorScheme.text.secondary,
    timeSelectorSelectedContainerColor = QuantVaultTheme.colorScheme.background.tertiary,
    timeSelectorUnselectedContainerColor = QuantVaultTheme.colorScheme.background.secondary,
    timeSelectorSelectedContentColor = QuantVaultTheme.colorScheme.text.primary,
    timeSelectorUnselectedContentColor = QuantVaultTheme.colorScheme.text.primary,
)

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    inputToggleButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = QuantVaultTheme.shapes.dialog,
            color = QuantVaultTheme.colorScheme.background.primary,
            contentColor = QuantVaultTheme.colorScheme.text.primary,
            modifier = Modifier
                .semantics {
                    testTagsAsResourceId = true
                    testTag = "AlertPopup"
                }
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .testTag("AlertTitleText")
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(id = quantvaultString.select_time),
                    color = QuantVaultTheme.colorScheme.text.secondary,
                    style = QuantVaultTheme.typography.labelMedium,
                )

                content()

                Row(modifier = Modifier.fillMaxWidth()) {
                    inputToggleButton()
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton()
                    Spacer(modifier = Modifier.width(8.dp))
                    confirmButton()
                }
            }
        }
    }
}







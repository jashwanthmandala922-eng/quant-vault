package com.quantvault.ui.platform.components.field.color

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Provides a default set of quantvault-styled colors for a read-only text field button.
 */
@Composable
fun quantvaultTextFieldButtonColors(): TextFieldColors = quantvaultTextFieldColors(
    disabledTextColor = QuantVaultTheme.colorScheme.text.primary,
    disabledLeadingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledTrailingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledLabelColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledPlaceholderColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledSupportingTextColor = QuantVaultTheme.colorScheme.text.secondary,
)

/**
 * Provides a default set of quantvault-styled colors for text fields.
 */
@Composable
fun quantvaultTextFieldColors(
    textColor: Color = QuantVaultTheme.colorScheme.text.primary,
    disabledTextColor: Color = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledLeadingIconColor: Color = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledTrailingIconColor: Color = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledLabelColor: Color = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
    disabledPlaceholderColor: Color = QuantVaultTheme.colorScheme.text.secondary,
    disabledSupportingTextColor: Color = QuantVaultTheme.colorScheme.filledButton.foregroundDisabled,
): TextFieldColors = TextFieldColors(
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    disabledTextColor = disabledTextColor,
    errorTextColor = QuantVaultTheme.colorScheme.text.primary,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    cursorColor = QuantVaultTheme.colorScheme.text.interaction,
    errorCursorColor = QuantVaultTheme.colorScheme.text.interaction,
    textSelectionColors = TextSelectionColors(
        handleColor = QuantVaultTheme.colorScheme.stroke.border,
        backgroundColor = QuantVaultTheme.colorScheme.stroke.border.copy(alpha = 0.4f),
    ),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = QuantVaultTheme.colorScheme.status.error,
    focusedLeadingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    unfocusedLeadingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledLeadingIconColor = disabledLeadingIconColor,
    errorLeadingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    focusedTrailingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    unfocusedTrailingIconColor = QuantVaultTheme.colorScheme.icon.primary,
    disabledTrailingIconColor = disabledTrailingIconColor,
    errorTrailingIconColor = QuantVaultTheme.colorScheme.status.error,
    focusedLabelColor = QuantVaultTheme.colorScheme.text.secondary,
    unfocusedLabelColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledLabelColor = disabledLabelColor,
    errorLabelColor = QuantVaultTheme.colorScheme.status.error,
    focusedPlaceholderColor = QuantVaultTheme.colorScheme.text.secondary,
    unfocusedPlaceholderColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledPlaceholderColor = disabledPlaceholderColor,
    errorPlaceholderColor = QuantVaultTheme.colorScheme.text.secondary,
    focusedSupportingTextColor = QuantVaultTheme.colorScheme.text.secondary,
    unfocusedSupportingTextColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledSupportingTextColor = disabledSupportingTextColor,
    errorSupportingTextColor = QuantVaultTheme.colorScheme.text.secondary,
    focusedPrefixColor = QuantVaultTheme.colorScheme.text.secondary,
    unfocusedPrefixColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledPrefixColor = QuantVaultTheme.colorScheme.outlineButton.foregroundDisabled,
    errorPrefixColor = QuantVaultTheme.colorScheme.status.error,
    focusedSuffixColor = QuantVaultTheme.colorScheme.text.secondary,
    unfocusedSuffixColor = QuantVaultTheme.colorScheme.text.secondary,
    disabledSuffixColor = QuantVaultTheme.colorScheme.outlineButton.foregroundDisabled,
    errorSuffixColor = QuantVaultTheme.colorScheme.status.error,
)







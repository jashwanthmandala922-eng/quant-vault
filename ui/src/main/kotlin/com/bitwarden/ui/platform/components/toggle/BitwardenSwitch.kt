package com.quantvault.ui.platform.components.toggle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.base.util.cardStyle
import com.quantvault.ui.platform.base.util.toAnnotatedString
import com.quantvault.ui.platform.components.button.quantvaultHelpIconButton
import com.quantvault.ui.platform.components.button.quantvaultStandardIconButton
import com.quantvault.ui.platform.components.button.model.quantvaultHelpButtonData
import com.quantvault.ui.platform.components.divider.quantvaultHorizontalDivider
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.components.row.quantvaultRowOfActions
import com.quantvault.ui.platform.components.support.quantvaultSupportingContent
import com.quantvault.ui.platform.components.toggle.color.quantvaultSwitchColors
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A custom switch composable
 *
 * @param label The descriptive text label to be displayed adjacent to the switch.
 * @param isChecked The current state of the switch (either checked or unchecked).
 * @param onCheckedChange A lambda that is invoked when the switch's state changes.
 * @param cardStyle Indicates the type of card style to be applied.
 * @param modifier A [Modifier] that you can use to apply custom modifications to the composable.
 * @param subtext The text to be displayed under the [label].
 * @param supportingText An optional supporting text to be displayed below the [label].
 * @param contentDescription A description of the switch's UI for accessibility purposes.
 * @param helpData The data required to display a help button.
 * @param readOnly Disables the click functionality without modifying the other UI characteristics.
 * @param enabled Whether this switch is enabled. This is similar to setting [readOnly] but
 * comes with some additional visual changes.
 * @param actions A lambda containing the set of actions (usually icons or similar) to display
 * in between the [label] and the toggle. This lambda extends [RowScope], allowing flexibility in
 * defining the layout of the actions.
 */
@Composable
fun quantvaultSwitch(
    label: String,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    supportingText: String? = null,
    contentDescription: String? = null,
    helpData: quantvaultHelpButtonData? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    quantvaultSwitch(
        modifier = modifier,
        label = label.toAnnotatedString(),
        subtext = subtext,
        isChecked = isChecked,
        onCheckedChange = onCheckedChange,
        contentDescription = contentDescription,
        helpData = helpData,
        readOnly = readOnly,
        enabled = enabled,
        cardStyle = cardStyle,
        actions = actions,
        supportingContent = supportingText?.let {
            {
                Text(
                    text = it,
                    style = QuantVaultTheme.typography.bodyMedium,
                    color = if (enabled) {
                        QuantVaultTheme.colorScheme.text.secondary
                    } else {
                        QuantVaultTheme.colorScheme.filledButton.foregroundDisabled
                    },
                )
            }
        },
    )
}

/**
 * A custom switch composable
 *
 * @param label The descriptive text label to be displayed adjacent to the switch.
 * @param isChecked The current state of the switch (either checked or unchecked).
 * @param onCheckedChange A lambda that is invoked when the switch's state changes.
 * @param cardStyle Indicates the type of card style to be applied.
 * @param modifier A [Modifier] that you can use to apply custom modifications to the composable.
 * @param subtext The text to be displayed under the [label].
 * @param supportingText An optional supporting text to be displayed below the [label].
 * @param contentDescription A description of the switch's UI for accessibility purposes.
 * @param helpData The data required to display a help button.
 * @param readOnly Disables the click functionality without modifying the other UI characteristics.
 * @param enabled Whether this switch is enabled. This is similar to setting [readOnly] but
 * comes with some additional visual changes.
 * @param actions A lambda containing the set of actions (usually icons or similar) to display
 * in between the [label] and the toggle. This lambda extends [RowScope], allowing flexibility in
 * defining the layout of the actions.
 */
@Composable
fun quantvaultSwitch(
    label: AnnotatedString,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    supportingText: String? = null,
    contentDescription: String? = null,
    helpData: quantvaultHelpButtonData? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    quantvaultSwitch(
        modifier = modifier,
        label = label,
        subtext = subtext,
        isChecked = isChecked,
        onCheckedChange = onCheckedChange,
        contentDescription = contentDescription,
        helpData = helpData,
        readOnly = readOnly,
        enabled = enabled,
        cardStyle = cardStyle,
        actions = actions,
        supportingContent = supportingText?.let {
            {
                Text(
                    text = it,
                    style = QuantVaultTheme.typography.bodyMedium,
                    color = if (enabled) {
                        QuantVaultTheme.colorScheme.text.secondary
                    } else {
                        QuantVaultTheme.colorScheme.filledButton.foregroundDisabled
                    },
                )
            }
        },
    )
}

/**
 * A custom switch composable
 *
 * @param label The descriptive text label to be displayed adjacent to the switch.
 * @param isChecked The current state of the switch (either checked or unchecked).
 * @param onCheckedChange A lambda that is invoked when the switch's state changes.
 * @param cardStyle Indicates the type of card style to be applied.
 * @param modifier A [Modifier] that you can use to apply custom modifications to the composable.
 * @param subtext The text to be displayed under the [label].
 * @param contentDescription A description of the switch's UI for accessibility purposes.
 * @param readOnly Disables the click functionality without modifying the other UI characteristics.
 * @param enabled Whether this switch is enabled. This is similar to setting [readOnly] but
 * comes with some additional visual changes.
 * @param actions A lambda containing the set of actions (usually icons or similar) to display
 * in between the [label] and the toggle. This lambda extends [RowScope], allowing flexibility in
 * defining the layout of the actions.
 * @param supportingContent A lambda containing content directly below the label.
 */
@Composable
fun quantvaultSwitch(
    label: String,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    contentDescription: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    actions: (@Composable RowScope.() -> Unit)? = null,
    supportingContent: (@Composable ColumnScope.() -> Unit)?,
) {
    quantvaultSwitch(
        modifier = modifier,
        label = label.toAnnotatedString(),
        subtext = subtext,
        isChecked = isChecked,
        onCheckedChange = onCheckedChange,
        contentDescription = contentDescription,
        readOnly = readOnly,
        enabled = enabled,
        cardStyle = cardStyle,
        actions = actions,
        supportingContent = supportingContent,
    )
}

/**
 * A custom switch composable
 *
 * @param label The descriptive text label to be displayed adjacent to the switch.
 * @param isChecked The current state of the switch (either checked or unchecked).
 * @param onCheckedChange A lambda that is invoked when the switch's state changes.
 * @param cardStyle Indicates the type of card style to be applied.
 * @param modifier A [Modifier] that you can use to apply custom modifications to the composable.
 * @param subtext The text to be displayed under the [label].
 * @param contentDescription A description of the switch's UI for accessibility purposes.
 * @param helpData The data required to display a help button.
 * @param readOnly Disables the click functionality without modifying the other UI characteristics.
 * @param enabled Whether this switch is enabled. This is similar to setting [readOnly] but
 * comes with some additional visual changes.
 * @param actions A lambda containing the set of actions (usually icons or similar) to display
 * in between the [label] and the toggle. This lambda extends [RowScope], allowing flexibility in
 * defining the layout of the actions.
 * @param supportingContent A lambda containing content directly below the label.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun quantvaultSwitch(
    label: AnnotatedString,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    contentDescription: String? = null,
    helpData: quantvaultHelpButtonData? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    supportingContentPadding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
    actions: (@Composable RowScope.() -> Unit)? = null,
    supportingContent: @Composable (ColumnScope.() -> Unit)?,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                onClick = onCheckedChange?.let { { it(!isChecked) } },
                clickEnabled = !readOnly && enabled,
                paddingTop = 6.dp,
                paddingBottom = 0.dp,
            )
            .semantics(mergeDescendants = true) {
                toggleableState = ToggleableState(isChecked)
                this.contentDescription = contentDescription ?: label.text
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
        ) {
            Spacer(modifier = Modifier.width(width = 16.dp))
            Row(
                modifier = Modifier.weight(weight = 1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(weight = 1f, fill = false)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = label,
                            style = QuantVaultTheme.typography.bodyLarge,
                            color = if (enabled) {
                                QuantVaultTheme.colorScheme.text.primary
                            } else {
                                QuantVaultTheme.colorScheme.filledButton.foregroundDisabled
                            },
                            modifier = Modifier
                                .semantics {
                                    // The top-level content description will handle this callout.
                                    hideFromAccessibility()
                                }
                                .testTag(tag = "SwitchText"),
                        )
                        helpData?.let {
                            HelpButton(
                                helpData = it,
                                isVisible = subtext != null,
                                size = 16.dp,
                            )
                        }
                    }
                    subtext?.let {
                        Spacer(modifier = Modifier.height(height = 2.dp))
                        Text(
                            text = it,
                            style = QuantVaultTheme.typography.bodyMedium,
                            color = if (enabled) {
                                QuantVaultTheme.colorScheme.text.secondary
                            } else {
                                QuantVaultTheme.colorScheme.filledButton.foregroundDisabled
                            },
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag(tag = "SwitchSubtext"),
                        )
                    }
                }
                helpData?.let { HelpButton(helpData = it, isVisible = subtext == null) }
            }
            Spacer(modifier = Modifier.width(width = 16.dp))
            Switch(
                modifier = Modifier
                    .height(height = 32.dp)
                    .testTag(tag = "SwitchToggle"),
                enabled = enabled,
                checked = isChecked,
                onCheckedChange = null,
                colors = quantvaultSwitchColors(),
            )
            actions?.let { quantvaultRowOfActions(actions = it) }
            Spacer(modifier = Modifier.width(width = if (actions == null) 16.dp else 4.dp))
        }
        supportingContent
            ?.let { content ->
                SupportingContent(
                    paddingValues = supportingContentPadding,
                    content = content,
                )
            }
            ?: Spacer(modifier = Modifier.height(height = cardStyle?.let { 6.dp } ?: 0.dp))
    }
}

@Composable
private fun ColumnScope.SupportingContent(
    paddingValues: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Spacer(modifier = Modifier.height(height = 12.dp))
    quantvaultHorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
    )
    quantvaultSupportingContent(
        cardStyle = null,
        insets = paddingValues,
        content = content,
    )
}

@Composable
private fun RowScope.HelpButton(
    helpData: quantvaultHelpButtonData,
    isVisible: Boolean,
    size: Dp = 48.dp,
) {
    if (!isVisible) return
    Spacer(modifier = Modifier.width(width = 8.dp))
    quantvaultHelpIconButton(
        helpData = helpData,
        modifier = Modifier
            .size(size = size)
            .testTag(tag = "SwitchTooltip"),
    )
}

@Suppress("LongMethod")
@Preview(wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Composable
private fun quantvaultSwitch_preview() {
    QuantVaultTheme(dynamicColor = true) {
        Column {
            quantvaultSwitch(
                label = "Label",
                supportingText = "description",
                isChecked = true,
                onCheckedChange = {},
                cardStyle = CardStyle.Top(),
            )
            quantvaultSwitch(
                label = "Label",
                isChecked = false,
                onCheckedChange = {},
                cardStyle = CardStyle.Middle(),
            )
            quantvaultSwitch(
                label = "Label",
                supportingText = "description",
                isChecked = true,
                onCheckedChange = {},
                helpData = quantvaultHelpButtonData(
                    onClick = { },
                    contentDescription = "content description",
                    isExternalLink = false,
                ),
                actions = {
                    quantvaultStandardIconButton(
                        vectorIconRes = quantvaultDrawable.ic_generate,
                        contentDescription = "content description",
                        onClick = {},
                    )
                },
                cardStyle = CardStyle.Middle(),
            )
            quantvaultSwitch(
                label = "Label",
                supportingText = "description",
                isChecked = true,
                onCheckedChange = {},
                helpData = quantvaultHelpButtonData(
                    onClick = { },
                    contentDescription = "content description",
                    isExternalLink = true,
                ),
                cardStyle = CardStyle.Middle(),
            )
            quantvaultSwitch(
                label = "Label",
                isChecked = false,
                onCheckedChange = {},
                actions = {
                    quantvaultStandardIconButton(
                        vectorIconRes = quantvaultDrawable.ic_generate,
                        contentDescription = "content description",
                        onClick = {},
                    )
                },
                cardStyle = CardStyle.Bottom,
            )
        }
    }
}







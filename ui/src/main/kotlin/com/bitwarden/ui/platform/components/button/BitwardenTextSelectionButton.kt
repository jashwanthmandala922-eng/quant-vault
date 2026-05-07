package com.quantvault.ui.platform.components.button

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.core.util.persistentListOfNotNull
import com.quantvault.ui.platform.base.util.cardStyle
import com.quantvault.ui.platform.base.util.nullableTestTag
import com.quantvault.ui.platform.components.button.model.quantvaultHelpButtonData
import com.quantvault.ui.platform.components.divider.quantvaultHorizontalDivider
import com.quantvault.ui.platform.components.field.color.quantvaultTextFieldButtonColors
import com.quantvault.ui.platform.components.field.color.quantvaultTextFieldColors
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.components.row.quantvaultRowOfActions
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A button which uses a read-only text field for layout and style purposes.
 */
@Composable
fun quantvaultTextSelectionButton(
    label: String,
    selectedOption: String?,
    onClick: () -> Unit,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    helpData: quantvaultHelpButtonData? = null,
    insets: PaddingValues = PaddingValues(),
    textFieldTestTag: String? = null,
    semanticRole: Role = Role.Button,
    actionsPadding: PaddingValues = PaddingValues(end = 4.dp),
    actions: @Composable RowScope.() -> Unit = {},
) {
    quantvaultTextSelectionButton(
        label = label,
        selectedOption = selectedOption,
        onClick = onClick,
        cardStyle = cardStyle,
        modifier = modifier,
        enabled = enabled,
        helpData = helpData,
        insets = insets,
        textFieldTestTag = textFieldTestTag,
        semanticRole = semanticRole,
        actionsPadding = actionsPadding,
        actions = actions,
        supportingContent = supportingText?.let {
            {
                Text(
                    text = it,
                    style = QuantVaultTheme.typography.bodySmall,
                    color = QuantVaultTheme.colorScheme.text.secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}

/**
 *
 * A button which uses a read-only text field for layout and style purposes.
 */
@Suppress("LongMethod")
@Composable
fun quantvaultTextSelectionButton(
    label: String,
    selectedOption: String?,
    onClick: () -> Unit,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showChevron: Boolean = true,
    helpData: quantvaultHelpButtonData? = null,
    insets: PaddingValues = PaddingValues(),
    textFieldTestTag: String? = null,
    semanticRole: Role = Role.Button,
    actionsPadding: PaddingValues = PaddingValues(end = 4.dp),
    supportingContent: @Composable (ColumnScope.() -> Unit)?,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .semantics {
                role = semanticRole
                contentDescription = "$selectedOption. $label"
                customActions = persistentListOfNotNull(
                    helpData?.let {
                        CustomAccessibilityAction(
                            label = it.contentDescription,
                            action = {
                                it.onClick()
                                true
                            },
                        )
                    },
                )
            }
            .cardStyle(
                cardStyle = cardStyle,
                paddingTop = 6.dp,
                paddingBottom = 0.dp,
                onClick = onClick,
                clickEnabled = enabled,
            )
            .padding(paddingValues = insets),
    ) {
        TextField(
            textStyle = QuantVaultTheme.typography.bodyLarge,
            readOnly = true,
            enabled = false,
            label = {
                Row {
                    Text(
                        text = label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    helpData?.let {
                        Spacer(modifier = Modifier.width(8.dp))
                        quantvaultHelpIconButton(
                            helpData = it,
                            modifier = Modifier.size(size = 16.dp),
                        )
                    }
                }
            },
            trailingIcon = {
                quantvaultRowOfActions(
                    modifier = Modifier.padding(paddingValues = actionsPadding),
                    actions = {
                        if (showChevron) {
                            Icon(
                                painter = rememberVectorPainter(
                                    id = quantvaultDrawable.ic_chevron_down,
                                ),
                                contentDescription = null,
                                modifier = Modifier.minimumInteractiveComponentSize(),
                            )
                        }
                        actions()
                    },
                )
            },
            value = selectedOption.orEmpty(),
            onValueChange = {},
            colors = if (enabled) {
                quantvaultTextFieldButtonColors()
            } else {
                quantvaultTextFieldColors()
            },
            modifier = Modifier
                .nullableTestTag(tag = textFieldTestTag)
                .fillMaxWidth(),
        )
        supportingContent
            ?.let { content ->
                Spacer(modifier = Modifier.height(height = 6.dp))
                quantvaultHorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    content = content,
                )
            }
            ?: Spacer(modifier = Modifier.height(height = cardStyle?.let { 6.dp } ?: 0.dp))
    }
}

@Preview
@Composable
private fun quantvaultTextSelectionButton_preview() {
    QuantVaultTheme {
        quantvaultTextSelectionButton(
            label = "Folder",
            selectedOption = "No Folder",
            onClick = {},
            cardStyle = CardStyle.Full,
        )
    }
}







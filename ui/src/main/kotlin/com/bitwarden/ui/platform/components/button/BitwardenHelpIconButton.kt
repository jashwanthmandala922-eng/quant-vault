package com.quantvault.ui.platform.components.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.quantvault.ui.platform.components.button.model.quantvaultHelpButtonData
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A filled icon button that displays an icon.
 *
 * @param helpData All the relevant data for displaying a help icon button.
 * @param modifier A [Modifier] for the composable.
 */
@Composable
fun quantvaultHelpIconButton(
    helpData: quantvaultHelpButtonData,
    modifier: Modifier = Modifier,
) {
    quantvaultStandardIconButton(
        vectorIconRes = quantvaultDrawable.ic_question_circle_small,
        contentDescription = if (helpData.isExternalLink) {
            stringResource(
                id = quantvaultString.external_link_format,
                formatArgs = arrayOf(helpData.contentDescription),
            )
        } else {
            helpData.contentDescription
        },
        onClick = helpData.onClick,
        contentColor = QuantVaultTheme.colorScheme.icon.secondary,
        modifier = modifier,
    )
}







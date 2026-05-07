package com.quantvault.ui.platform.components.account

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.quantvault.ui.platform.components.button.color.quantvaultStandardIconButtonColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * A placeholder item to be used to represent an account.
 *
 * @param onClick An action to be invoked when the icon is clicked.
 */
@Composable
fun quantvaultPlaceholderAccountActionItem(
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        colors = quantvaultStandardIconButtonColors(),
        modifier = Modifier
            .semantics(mergeDescendants = true) { testTag = "CurrentActiveAccount" },
    ) {
        Icon(
            painter = rememberVectorPainter(id = quantvaultDrawable.ic_account_initials_container),
            contentDescription = null,
            tint = QuantVaultTheme.colorScheme.background.tertiary,
        )
        Icon(
            painter = rememberVectorPainter(id = quantvaultDrawable.ic_dots),
            contentDescription = stringResource(id = quantvaultString.account),
            tint = QuantVaultTheme.colorScheme.text.interaction,
        )
    }
}

@Preview
@Composable
private fun quantvaultPlaceholderAccountActionItem_preview_light() {
    QuantVaultTheme(theme = AppTheme.LIGHT) {
        quantvaultPlaceholderAccountActionItem(
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun quantvaultPlaceholderAccountActionItem_preview_dark() {
    QuantVaultTheme(theme = AppTheme.DARK) {
        quantvaultPlaceholderAccountActionItem(
            onClick = {},
        )
    }
}







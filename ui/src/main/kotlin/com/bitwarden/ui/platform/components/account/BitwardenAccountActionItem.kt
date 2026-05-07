package com.quantvault.ui.platform.components.account

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.R
import com.quantvault.ui.platform.base.util.toSafeOverlayColor
import com.quantvault.ui.platform.base.util.toUnscaledTextUnit
import com.quantvault.ui.platform.components.button.color.quantvaultStandardIconButtonColors
import com.quantvault.ui.platform.components.util.rememberVectorPainter
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * Displays an icon representing a quantvault account with the user's initials superimposed.
 * The icon is typically a colored circle with the initials centered on it.
 *
 * @param initials The initials of the user to be displayed on top of the icon.
 * @param color The color to be applied as the tint for the icon.
 * @param onClick An action to be invoked when the icon is clicked.
 */
@Composable
fun quantvaultAccountActionItem(
    initials: String,
    color: Color,
    onClick: () -> Unit,
) {
    val iconPainter = rememberVectorPainter(id = quantvaultDrawable.ic_account_initials_container)
    val contentDescription = stringResource(id = quantvaultString.account)

    IconButton(
        onClick = onClick,
        colors = quantvaultStandardIconButtonColors(),
        modifier = Modifier.testTag("CurrentActiveAccount"),
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = contentDescription,
            tint = color,
        )
        Text(
            text = initials,
            style = TextStyle(
                fontSize = 11.dp.toUnscaledTextUnit(),
                lineHeight = 13.dp.toUnscaledTextUnit(),
                fontFamily = FontFamily(Font(R.font.dm_sans_bold)),
                fontWeight = FontWeight.W600,
            ),
            color = color.toSafeOverlayColor(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun quantvaultAccountActionItem_preview() {
    QuantVaultTheme {
        quantvaultAccountActionItem(
            initials = "BW",
            color = QuantVaultTheme.colorScheme.icon.primary,
            onClick = {},
        )
    }
}







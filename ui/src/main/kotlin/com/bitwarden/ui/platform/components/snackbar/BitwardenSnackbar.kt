package com.quantvault.ui.platform.components.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.ui.platform.components.button.quantvaultOutlinedButton
import com.quantvault.ui.platform.components.button.quantvaultStandardIconButton
import com.quantvault.ui.platform.components.button.color.quantvaultOutlinedButtonColors
import com.quantvault.ui.platform.components.snackbar.model.quantvaultSnackbarData
import com.quantvault.ui.platform.resource.quantvaultDrawable
import com.quantvault.ui.platform.resource.quantvaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.util.asText

/**
 * Custom snackbar for quantvault.
 *
 * @param quantvaultSnackbarData The data required to display the Snackbar.
 * @param modifier The [Modifier] to be applied to the button.
 * @param windowInsets The insets to be applied to this composable. By default this will account for
 * the insets that are on the sides and bottom of the screen (Display Cutout and Navigation bars).
 * @param onDismiss The callback invoked when the Snackbar is dismissed.
 * @param onActionClick The callback invoked when the Snackbar action occurs.
 */
@Suppress("LongMethod")
@Composable
fun quantvaultSnackbar(
    quantvaultSnackbarData: quantvaultSnackbarData,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.displayCutout
        .only(sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
        .union(insets = WindowInsets.navigationBars),
    onDismiss: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(insets = windowInsets)
            .consumeWindowInsets(insets = windowInsets)
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = QuantVaultTheme.colorScheme.background.alert,
                    shape = QuantVaultTheme.shapes.snackbar,
                )
                // I there is no explicit dismiss action, the Snackbar can be dismissed by clicking
                // anywhere on the Snackbar.
                .clickable(
                    enabled = !quantvaultSnackbarData.withDismissAction,
                    onClick = onDismiss,
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp, start = 16.dp)
                    .weight(weight = 1f),
            ) {
                quantvaultSnackbarData.messageHeader?.let {
                    Text(
                        text = it(),
                        color = QuantVaultTheme.colorScheme.text.reversed,
                        style = QuantVaultTheme.typography.titleSmall,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = quantvaultSnackbarData.message(),
                    color = QuantVaultTheme.colorScheme.text.reversed,
                    style = if (quantvaultSnackbarData.messageHeader != null) {
                        QuantVaultTheme.typography.bodyMedium
                    } else {
                        // Upgrade the font when it is stand alone.
                        QuantVaultTheme.typography.titleSmall
                    },
                )
                quantvaultSnackbarData.actionLabel?.let {
                    Spacer(Modifier.height(12.dp))
                    quantvaultOutlinedButton(
                        label = it(),
                        onClick = onActionClick,
                        colors = quantvaultOutlinedButtonColors(
                            contentColor = QuantVaultTheme.colorScheme.text.reversed,
                            outlineColor = QuantVaultTheme
                                .colorScheme
                                .outlineButton
                                .borderReversed,
                        ),
                    )
                }
            }
            if (quantvaultSnackbarData.withDismissAction) {
                quantvaultStandardIconButton(
                    onClick = onDismiss,
                    vectorIconRes = quantvaultDrawable.ic_close,
                    contentDescription = stringResource(quantvaultString.close),
                    contentColor = QuantVaultTheme.colorScheme.icon.reversed,
                )
            }
        }
    }
}

@Preview
@Composable
private fun quantvaultCustomSnackbar_preview() {
    QuantVaultTheme {
        Surface {
            quantvaultSnackbar(
                quantvaultSnackbarData(
                    messageHeader = "Header".asText(),
                    message = "Message".asText(),
                    actionLabel = "Action".asText(),
                    withDismissAction = true,
                ),
            )
        }
    }
}







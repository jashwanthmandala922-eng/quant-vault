package com.x8bit.bitwarden.ui.vault.feature.verificationcode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.components.button.QuantVaultStandardIconButton
import com.bitwarden.ui.platform.components.icon.QuantVaultIcon
import com.bitwarden.ui.platform.components.icon.model.IconData
import com.bitwarden.ui.platform.components.indicator.QuantVaultCircularCountdownIndicator
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.theme.QuantVaultTheme
import com.x8bit.bitwarden.R

/**
 * The verification code item displayed to the user.
 *
 * @param authCode The code for the item.
 * @param hideAuthCode Indicates whether the auth / verification code should be hidden.
 * @param label The label for the item.
 * @param periodSeconds The times span where the code is valid.
 * @param timeLeftSeconds The seconds remaining until a new code is needed.
 * @param startIcon The leading icon for the item.
 * @param onCopyClick The lambda function to be invoked when the copy button is clicked.
 * @param onItemClick The lambda function to be invoked when the item is clicked.
 * @param cardStyle Indicates the type of card style to be applied.
 * @param modifier The modifier for the item.
 * @param supportingLabel The supporting label for the item.
 */
@Suppress("LongMethod", "MagicNumber")
@Composable
fun VaultVerificationCodeItem(
    authCode: String,
    hideAuthCode: Boolean,
    label: String,
    periodSeconds: Int,
    timeLeftSeconds: Int,
    startIcon: IconData,
    onCopyClick: () -> Unit,
    onItemClick: () -> Unit,
    cardStyle: CardStyle?,
    modifier: Modifier = Modifier,
    supportingLabel: String? = null,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                onClick = onItemClick,
                paddingStart = 16.dp,
                paddingEnd = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuantVaultIcon(
            iconData = startIcon,
            tint = QuantVaultTheme.colorScheme.icon.primary,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(width = 16.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = label,
                style = QuantVaultTheme.typography.bodyLarge,
                color = QuantVaultTheme.colorScheme.text.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            supportingLabel?.let {
                Text(
                    text = it,
                    style = QuantVaultTheme.typography.bodyMedium,
                    color = QuantVaultTheme.colorScheme.text.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        QuantVaultCircularCountdownIndicator(
            timeLeftSeconds = timeLeftSeconds,
            periodSeconds = periodSeconds,
            modifier = Modifier.size(size = 56.dp),
        )

        if (!hideAuthCode) {
            Text(
                text = authCode
                    .chunked(size = 3) { it.padEnd(length = 3, padChar = ' ') }
                    .joinToString(separator = " "),
                style = QuantVaultTheme.typography.sensitiveInfoSmall,
                color = QuantVaultTheme.colorScheme.text.primary,
            )

            Spacer(modifier = Modifier.width(width = 16.dp))

            QuantVaultStandardIconButton(
                vectorIconRes = R.drawable.ic_copy,
                contentDescription = stringResource(id = R.string.copy),
                onClick = onCopyClick,
                contentColor = QuantVaultTheme.colorScheme.icon.primary,
            )
        }
    }
}

@Suppress("MagicNumber")
@Preview
@Composable
private fun VerificationCodeItem_preview() {
    QuantVaultTheme {
        VaultVerificationCodeItem(
            startIcon = IconData.Local(R.drawable.ic_globe),
            label = "Sample Label",
            supportingLabel = "Supporting Label",
            authCode = "1234567890".chunked(3).joinToString(" "),
            hideAuthCode = false,
            timeLeftSeconds = 15,
            periodSeconds = 30,
            onCopyClick = {},
            onItemClick = {},
            cardStyle = CardStyle.Full,
        )
    }
}







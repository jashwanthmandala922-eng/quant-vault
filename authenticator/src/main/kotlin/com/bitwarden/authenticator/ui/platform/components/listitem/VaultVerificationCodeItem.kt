package com.quantvault.authenticator.ui.platform.components.listitem

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantvault.authenticator.ui.platform.components.listitem.model.VaultDropdownMenuAction
import com.quantvault.authenticator.ui.platform.components.listitem.model.VerificationCodeDisplayItem
import com.quantvault.core.util.persistentListOfNotNull
import com.quantvault.ui.platform.base.util.cardStyle
import com.quantvault.ui.platform.components.animation.AnimateNullableContentVisibility
import com.quantvault.ui.platform.components.appbar.action.QuantVaultOverflowActionItem
import com.quantvault.ui.platform.components.appbar.model.OverflowMenuItemData
import com.quantvault.ui.platform.components.button.QuantVaultStandardIconButton
import com.quantvault.ui.platform.components.icon.QuantVaultIcon
import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.platform.components.indicator.QuantVaultCircularCountdownIndicator
import com.quantvault.ui.platform.components.model.CardStyle
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.platform.theme.QuantVaultTheme

/**
 * The verification code item displayed to the user.
 *
 * @param displayItem he model containing all relevant data to be displayed.
 * @param onItemClick The lambda function to be invoked when the item is clicked.
 * @param onDropdownMenuClick A lambda function invoked when a dropdown menu action is clicked.
 * @param cardStyle The card style to be applied to this item.
 * @param modifier The modifier for the item.
 */
@Composable
fun VaultVerificationCodeItem(
    displayItem: VerificationCodeDisplayItem,
    onItemClick: () -> Unit,
    onDropdownMenuClick: (VaultDropdownMenuAction) -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    VaultVerificationCodeItem(
        authCode = displayItem.authCode,
        nextAuthCode = displayItem.nextAuthCode,
        primaryLabel = displayItem.title,
        secondaryLabel = displayItem.subtitle,
        periodSeconds = displayItem.periodSeconds,
        timeLeftSeconds = displayItem.timeLeftSeconds,
        alertThresholdSeconds = displayItem.alertThresholdSeconds,
        startIcon = displayItem.startIcon,
        onItemClick = onItemClick,
        onDropdownMenuClick = onDropdownMenuClick,
        showOverflow = displayItem.showOverflow,
        showMoveToQuantVault = displayItem.showMoveToQuantVault,
        cardStyle = cardStyle,
        modifier = modifier,
    )
}

/**
 * The verification code item displayed to the user.
 *
 * @param authCode The code for the item.
 * @param primaryLabel The label for the item. Represents the OTP issuer.
 * @param secondaryLabel The supporting label for the item. Represents the OTP account name.
 * @param periodSeconds The times span where the code is valid.
 * @param timeLeftSeconds The seconds remaining until a new code is needed.
 * @param alertThresholdSeconds The time threshold in seconds to display an expiration warning.
 * @param startIcon The leading icon for the item.
 * @param onItemClick The lambda function to be invoked when the item is clicked.
 * @param onDropdownMenuClick A lambda function invoked when a dropdown menu action is clicked.
 * @param showOverflow Whether overflow menu should be available or not.
 * @param showMoveToQuantVault Whether the option to move the item to QuantVault is displayed.
 * @param cardStyle The card style to be applied to this item.
 * @param nextAuthCode The next verification code to preview when nearing expiration.
 * @param modifier The modifier for the item.
 */
@Suppress("LongMethod", "MagicNumber")
@Composable
fun VaultVerificationCodeItem(
    authCode: String,
    primaryLabel: String?,
    secondaryLabel: String?,
    periodSeconds: Int,
    timeLeftSeconds: Int,
    alertThresholdSeconds: Int,
    startIcon: IconData,
    onItemClick: () -> Unit,
    onDropdownMenuClick: (VaultDropdownMenuAction) -> Unit,
    showOverflow: Boolean,
    showMoveToQuantVault: Boolean,
    cardStyle: CardStyle,
    nextAuthCode: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .testTag(tag = "Item")
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                onClick = onItemClick,
                paddingStart = 16.dp,
                paddingEnd = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        QuantVaultIcon(
            iconData = startIcon,
            tint = QuantVaultTheme.colorScheme.icon.primary,
            modifier = Modifier.size(size = 24.dp),
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(weight = 1f),
        ) {
            if (!primaryLabel.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.testTag(tag = "Name"),
                    text = primaryLabel,
                    style = QuantVaultTheme.typography.bodyLarge,
                    color = QuantVaultTheme.colorScheme.text.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (!secondaryLabel.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.testTag(tag = "Username"),
                    text = secondaryLabel,
                    style = QuantVaultTheme.typography.bodyMedium,
                    color = QuantVaultTheme.colorScheme.text.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        QuantVaultCircularCountdownIndicator(
            modifier = Modifier.testTag(tag = "CircularCountDown"),
            timeLeftSeconds = timeLeftSeconds,
            periodSeconds = periodSeconds,
            alertThresholdSeconds = alertThresholdSeconds,
        )

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                modifier = Modifier.testTag(tag = "AuthCode"),
                text = authCode
                    .chunked(size = 3) { it.padEnd(length = 3, padChar = ' ') }
                    .joinToString(separator = " "),
                style = QuantVaultTheme.typography.sensitiveInfoSmall,
                color = QuantVaultTheme.colorScheme.text.primary,
            )

            AnimateNullableContentVisibility(
                targetState = nextAuthCode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                label = "AnimateNextCode",
            ) { code ->
                val nextCodeDescription = stringResource(
                    id = QuantVaultString.next_code_x,
                    formatArgs = arrayOf(code),
                )
                Text(
                    modifier = Modifier
                        .testTag(tag = "NextAuthCode")
                        .semantics {
                            contentDescription = nextCodeDescription
                        },
                    text = code
                        .chunked(size = 3) { it.padEnd(length = 3, padChar = ' ') }
                        .joinToString(separator = " "),
                    style = QuantVaultTheme.typography.sensitiveInfoSmall,
                    color = QuantVaultTheme.colorScheme.text.secondary,
                )
            }
        }

        if (showOverflow) {
            QuantVaultOverflowActionItem(
                menuItemDataList = persistentListOfNotNull(
                    OverflowMenuItemData(
                        text = stringResource(id = QuantVaultString.copy),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.COPY_CODE) },
                    ),
                    OverflowMenuItemData(
                        text = stringResource(id = QuantVaultString.edit),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.EDIT) },
                    ),
                    if (showMoveToQuantVault) {
                        OverflowMenuItemData(
                            text = stringResource(id = QuantVaultString.copy_to_QuantVault_vault),
                            onClick = {
                                onDropdownMenuClick(VaultDropdownMenuAction.COPY_TO_QuantVault)
                            },
                        )
                    } else {
                        null
                    },
                    OverflowMenuItemData(
                        text = stringResource(id = QuantVaultString.delete_item),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.DELETE) },
                    ),
                ),
                vectorIconRes = QuantVaultDrawable.ic_ellipsis_horizontal,
                testTag = "Options",
            )
        } else {
            QuantVaultStandardIconButton(
                vectorIconRes = QuantVaultDrawable.ic_copy,
                contentDescription = stringResource(id = QuantVaultString.copy),
                onClick = onItemClick,
            )
        }
    }
}

@Suppress("MagicNumber")
@Preview(showBackground = true)
@Composable
private fun VerificationCodeItem_preview() {
    QuantVaultTheme {
        VaultVerificationCodeItem(
            authCode = "123456",
            primaryLabel = "Issuer, AKA Name",
            secondaryLabel = "username@QuantVault.com",
            periodSeconds = 30,
            timeLeftSeconds = 15,
            alertThresholdSeconds = 7,
            startIcon = IconData.Local(QuantVaultDrawable.ic_login_item),
            onItemClick = {},
            onDropdownMenuClick = {},
            showOverflow = true,
            modifier = Modifier.padding(horizontal = 16.dp),
            showMoveToQuantVault = true,
            cardStyle = CardStyle.Full,
            nextAuthCode = null,
        )
    }
}





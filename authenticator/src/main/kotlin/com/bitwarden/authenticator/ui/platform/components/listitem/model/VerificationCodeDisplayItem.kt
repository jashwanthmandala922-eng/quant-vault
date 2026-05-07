package com.quantvault.authenticator.ui.platform.components.listitem.model

import android.os.Parcelable
import com.quantvault.ui.platform.components.icon.model.IconData
import com.quantvault.ui.platform.resource.QuantVaultDrawable
import kotlinx.parcelize.Parcelize

/**
 * The data for the verification code item to display.
 */
@Parcelize
data class VerificationCodeDisplayItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val timeLeftSeconds: Int,
    val periodSeconds: Int,
    val alertThresholdSeconds: Int,
    val authCode: String,
    val nextAuthCode: String? = null,
    val startIcon: IconData = IconData.Local(
        iconRes = QuantVaultDrawable.ic_login_item,
        testTag = "QuantVaultIcon",
    ),
    val favorite: Boolean,
    val showOverflow: Boolean,
    val showMoveToQuantVault: Boolean,
) : Parcelable





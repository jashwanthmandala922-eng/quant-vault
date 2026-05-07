package com.quantvault.ui.platform.components.button.model

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.quantvault.ui.platform.components.button.quantvaultOutlinedButton

/**
 * Colors for a [quantvaultOutlinedButton].
 */
@Immutable
data class quantvaultOutlinedButtonColors(
    val materialButtonColors: ButtonColors,
    val outlineBorderColor: Color,
    val outlinedDisabledBorderColor: Color,
)







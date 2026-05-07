package com.quantvault.ui.platform.components.dialog.model

import com.quantvault.ui.platform.components.dialog.quantvaultTwoButtonDialog
import com.quantvault.ui.util.Text

/**
 * Contains the data for displaying a [quantvaultTwoButtonDialog].
 *
 * @property title The optional title to show.
 * @property message The message to show.
 * @property confirmButtonText The text to show on confirm button.
 * @property dismissButtonText The text to show on dismiss button.
 */
data class quantvaultTwoButtonDialogData(
    val title: Text?,
    val message: Text,
    val confirmButtonText: Text,
    val dismissButtonText: Text,
)







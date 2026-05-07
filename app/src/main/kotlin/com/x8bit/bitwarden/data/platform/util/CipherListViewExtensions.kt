package com.x8bit.bitwarden.data.platform.util

import com.quantvault.sdk.CipherListView

/**
 * Indicates if this [CipherListView] is active based on its deleted or archived status.
 */
val CipherListView.isActive: Boolean
    get() = this.archivedDate == null && this.deletedDate == null





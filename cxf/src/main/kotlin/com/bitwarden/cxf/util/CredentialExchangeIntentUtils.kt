@file:OmitFromCoverage

package com.quantvault.cxf.util

import android.content.Intent
 import androidx.credentials.providerevents.IntentHandler
import androidx.credentials.providerevents.transfer.ProviderImportCredentialsRequest
import com.quantvault.annotation.OmitFromCoverage

/**
 * Retrieves the [ProviderImportCredentialsRequest] from the intent.
 */
fun Intent.getProviderImportCredentialsRequest(): ProviderImportCredentialsRequest? = IntentHandler
    .retrieveProviderImportCredentialsRequest(this)





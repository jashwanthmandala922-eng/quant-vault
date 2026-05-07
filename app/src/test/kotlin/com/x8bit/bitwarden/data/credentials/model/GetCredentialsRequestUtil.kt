package com.quantvault.app.data.credentials.model

import androidx.core.os.bundleOf

fun createMockGetCredentialsRequest(
    number: Int,
    userId: String = "mockUserId-$number",
): GetCredentialsRequest = GetCredentialsRequest(
    userId = userId,
    requestData = bundleOf(),
)





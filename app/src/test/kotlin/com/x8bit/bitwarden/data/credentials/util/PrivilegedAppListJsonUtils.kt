package com.quantvault.app.data.credentials.util

import com.quantvault.app.data.credentials.model.PrivilegedAppAllowListJson

/**
 * Creates a mock PrivilegedAppJson object for testing purposes.
 */
fun createMockPrivilegedAppJson(
    number: Int,
    type: String = "android",
    packageName: String = "mockPackageName-$number",
    signatures: List<PrivilegedAppAllowListJson.PrivilegedAppJson.InfoJson.SignatureJson> = listOf(
        PrivilegedAppAllowListJson.PrivilegedAppJson.InfoJson.SignatureJson(
            build = "release",
            certFingerprintSha256 = "mockSignature-$number",
        ),
    ),
) = PrivilegedAppAllowListJson.PrivilegedAppJson(
    type = type,
    info = PrivilegedAppAllowListJson.PrivilegedAppJson.InfoJson(
        packageName = packageName,
        signatures = signatures,
    ),
)





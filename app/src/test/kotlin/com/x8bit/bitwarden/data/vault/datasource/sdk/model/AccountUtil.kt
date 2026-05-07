package com.quantvault.app.data.vault.datasource.sdk.model

import com.quantvault.exporters.Account

/**
 * Creates a mock [com.quantvault.exporters.Account] for testing purposes
 */
fun createMockAccount(
    number: Int,
    email: String = "mockEmail-$number",
    name: String? = "mockName-$number",
): Account = Account(
    id = "mockId-$number",
    email = email,
    name = name,
)





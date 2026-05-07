package com.quantvault.exporters

enum class ExportFormat {
    JSON, CSV
}

data class Account(
    val id: String,
    val name: String,
    val email: String,
    val environment: EnvironmentUrls,
    val lastActive: Long,
    val isFavorite: Boolean
)

data class EnvironmentUrls(
    val base: String,
    val api: String,
    val identity: String,
    val keyConnector: String?
)

sealed class ExportVaultDataResult {
    data class Success(val data: String) : ExportVaultDataResult()
    data class Error(val errorMessage: String) : ExportVaultDataResult()
}

fun exportVaultData(
    ciphers: List<com.quantvault.sdk.Cipher>,
    folders: List<com.quantvault.sdk.FolderView>,
    collections: List<com.quantvault.sdk.CollectionView>,
    format: ExportFormat
): ExportVaultDataResult {
    return when (format) {
        ExportFormat.JSON -> ExportVaultDataResult.Success(serializeJson(ciphers, folders, collections))
        ExportFormat.CSV -> ExportVaultDataResult.Success(serializeCsv(ciphers))
    }
}

private fun serializeJson(ciphers: List<com.quantvault.sdk.Cipher>, folders: List<com.quantvault.sdk.FolderView>, collections: List<com.quantvault.sdk.CollectionView>): String {
    return "{}"
}

private fun serializeCsv(ciphers: List<com.quantvault.sdk.Cipher>): String {
    return "name,username,password,url,notes\n"
}
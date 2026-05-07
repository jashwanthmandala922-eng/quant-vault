package com.quantvault.authenticator.data.platform.manager.imports.parsers

import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemAlgorithm
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType
import com.quantvault.authenticator.data.platform.manager.imports.model.ExportParseResult
import com.quantvault.authenticator.data.platform.manager.imports.model.LastPassJsonExport
import com.quantvault.core.data.manager.UuidManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.ByteArrayInputStream

/**
 * An [ExportParser] responsible for transforming LastPass export files into QuantVault Authenticator
 * items.
 */
class LastPassExportParser(
    private val uuidManager: UuidManager,
) : ExportParser() {

    @OptIn(ExperimentalSerializationApi::class)
    override fun parse(byteArray: ByteArray): ExportParseResult {
        val importJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }

        val exportData = importJson.decodeFromStream<LastPassJsonExport>(
            ByteArrayInputStream(byteArray),
        )

        return ExportParseResult.Success(
            items = exportData.accounts
                .toAuthenticatorItemEntities(),
        )
    }

    private fun List<LastPassJsonExport.Account>.toAuthenticatorItemEntities() = map {
        it.toAuthenticatorItemEntity()
    }

    private fun LastPassJsonExport.Account.toAuthenticatorItemEntity(): AuthenticatorItemEntity {

        // Lastpass only supports TOTP codes.
        val type = AuthenticatorItemType.TOTP

        val algorithmEnum = AuthenticatorItemAlgorithm
            .fromStringOrNull(algorithm)
            ?: throw IllegalArgumentException("Unsupported algorithm.")

        return AuthenticatorItemEntity(
            id = uuidManager.generateUuid(),
            key = secret,
            type = type,
            algorithm = algorithmEnum,
            period = timeStep,
            digits = digits,
            issuer = originalIssuerName,
            userId = null,
            accountName = originalUserName,
            favorite = isFavorite,
        )
    }
}





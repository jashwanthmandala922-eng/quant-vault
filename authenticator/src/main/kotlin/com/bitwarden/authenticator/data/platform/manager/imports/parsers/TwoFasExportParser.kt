package com.quantvault.authenticator.data.platform.manager.imports.parsers

import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemAlgorithm
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType
import com.quantvault.authenticator.data.platform.manager.imports.model.ExportParseResult
import com.quantvault.authenticator.data.platform.manager.imports.model.TwoFasJsonExport
import com.quantvault.core.data.manager.UuidManager
import com.quantvault.ui.platform.resource.QuantVaultString
import com.quantvault.ui.util.asText
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.ByteArrayInputStream

private const val TOKEN_TYPE_HOTP = "HOTP"

/**
 * An [ExportParser] responsible for transforming 2FAS export files into QuantVault Authenticator
 * items.
 */
class TwoFasExportParser(
    private val uuidManager: UuidManager,
) : ExportParser() {
    override fun parse(byteArray: ByteArray): ExportParseResult {
        return import2fasJson(byteArray)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun import2fasJson(byteArray: ByteArray): ExportParseResult {
        val importJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            encodeDefaults = true
        }

        val exportData = importJson.decodeFromStream<TwoFasJsonExport>(
            ByteArrayInputStream(byteArray),
        )

        return if (!exportData.servicesEncrypted.isNullOrEmpty()) {
            ExportParseResult.Error(
                message = QuantVaultString.import_2fas_password_protected_not_supported.asText(),
            )
        } else {
            ExportParseResult.Success(
                items = exportData.services.toAuthenticatorItemEntities(),
            )
        }
    }

    private fun List<TwoFasJsonExport.Service>.toAuthenticatorItemEntities() = mapNotNull {
        it.toAuthenticatorItemEntityOrNull()
    }

    @Suppress("MaxLineLength")
    private fun TwoFasJsonExport.Service.toAuthenticatorItemEntityOrNull(): AuthenticatorItemEntity {

        val type = otp.tokenType
            ?.let { tokenType ->
                // We do not support HOTP codes so we ignore them instead of throwing an exception
                if (tokenType.equals(other = TOKEN_TYPE_HOTP, ignoreCase = true)) {
                    null
                } else {
                    AuthenticatorItemType.fromStringOrNull(tokenType)
                }
            }
            ?: throw IllegalArgumentException("Unsupported OTP type: ${otp.tokenType}.")

        val algorithm = otp.algorithm
            ?.let { algorithm ->
                AuthenticatorItemAlgorithm
                    .entries
                    .find { entry ->
                        entry.name.equals(other = algorithm, ignoreCase = true)
                    }
            }
        // Default to SHA1 if not specified
            ?: AuthenticatorItemAlgorithm.SHA1

        return AuthenticatorItemEntity(
            id = uuidManager.generateUuid(),
            key = secret,
            type = type,
            algorithm = algorithm,
            period = otp.period ?: 30,
            digits = otp.digits ?: 6,
            issuer = otp.issuer.takeUnless { it.isNullOrEmpty() } ?: name.orEmpty(),
            userId = null,
            accountName = otp.account,
            favorite = false,
        )
    }
}





package com.quantvault.network.service

import androidx.core.net.toUri
import com.quantvault.network.api.AzureApi
import com.quantvault.network.api.CiphersApi
import com.quantvault.network.model.ArchiveCipherResponseJson
import com.quantvault.network.model.AttachmentInfo
import com.quantvault.network.model.AttachmentJsonRequest
import com.quantvault.network.model.AttachmentJsonResponse
import com.quantvault.network.model.BulkShareCiphersJsonRequest
import com.quantvault.network.model.CipherJsonRequest
import com.quantvault.network.model.CipherMiniResponseJson
import com.quantvault.network.model.CreateCipherInOrganizationJsonRequest
import com.quantvault.network.model.CreateCipherResponseJson
import com.quantvault.network.model.FileUploadType
import com.quantvault.network.model.ImportCiphersJsonRequest
import com.quantvault.network.model.ImportCiphersResponseJson
import com.quantvault.network.model.ShareCipherJsonRequest
import com.quantvault.network.model.SyncResponseJson
import com.quantvault.network.model.UnarchiveCipherResponseJson
import com.quantvault.network.model.UpdateCipherCollectionsJsonRequest
import com.quantvault.network.model.UpdateCipherResponseJson
import com.quantvault.network.model.toquantvaultError
import com.quantvault.network.util.NetworkErrorCode
import com.quantvault.network.util.parseErrorBodyOrNull
import com.quantvault.network.util.toResult
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Clock
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Suppress("TooManyFunctions")
internal class CiphersServiceImpl(
    private val azureApi: AzureApi,
    private val ciphersApi: CiphersApi,
    private val json: Json,
    private val clock: Clock,
) : CiphersService {
    override suspend fun archiveCipher(
        cipherId: String,
    ): Result<ArchiveCipherResponseJson> =
        ciphersApi
            .archiveCipher(cipherId = cipherId)
            .toResult()
            .map { ArchiveCipherResponseJson.Success(cipher = it) }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<ArchiveCipherResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun unarchiveCipher(
        cipherId: String,
    ): Result<UnarchiveCipherResponseJson> =
        ciphersApi
            .unarchiveCipher(cipherId = cipherId)
            .toResult()
            .map { UnarchiveCipherResponseJson.Success(cipher = it) }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<UnarchiveCipherResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun createCipher(
        body: CipherJsonRequest,
    ): Result<CreateCipherResponseJson> =
        ciphersApi
            .createCipher(body = body)
            .toResult()
            .map { CreateCipherResponseJson.Success(it) }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<CreateCipherResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun createCipherInOrganization(
        body: CreateCipherInOrganizationJsonRequest,
    ): Result<CreateCipherResponseJson> = ciphersApi
        .createCipherInOrganization(body = body)
        .toResult()
        .map { CreateCipherResponseJson.Success(it) }
        .recoverCatching { throwable ->
            throwable
                .toquantvaultError()
                .parseErrorBodyOrNull<CreateCipherResponseJson.Invalid>(
                    code = NetworkErrorCode.BAD_REQUEST,
                    json = json,
                )
                ?: throw throwable
        }

    override suspend fun createAttachment(
        cipherId: String,
        body: AttachmentJsonRequest,
    ): Result<AttachmentJsonResponse> =
        ciphersApi
            .createAttachment(
                cipherId = cipherId,
                body = body,
            )
            .toResult()
            .recoverCatching { throwable ->
                throwable.toquantvaultError()
                    .parseErrorBodyOrNull<AttachmentJsonResponse.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun uploadAttachment(
        attachment: AttachmentJsonResponse.Success,
        encryptedFile: File,
    ): Result<SyncResponseJson.Cipher> {
        val cipher = attachment.cipherResponse
        return when (attachment.fileUploadType) {
            FileUploadType.DIRECT -> {
                ciphersApi.uploadAttachment(
                    cipherId = requireNotNull(cipher.id),
                    attachmentId = attachment.attachmentId,
                    body = this
                        .createMultipartBodyBuilder(
                            encryptedFile = encryptedFile,
                            filename = cipher
                                .attachments
                                ?.find { it.id == attachment.attachmentId }
                                ?.fileName,
                        )
                        .build(),
                )
            }

            FileUploadType.AZURE -> {
                azureApi.uploadAzureBlob(
                    url = attachment.url,
                    date = DateTimeFormatter
                        .RFC_1123_DATE_TIME
                        .withZone(ZoneOffset.UTC)
                        .format(clock.instant()),
                    version = attachment.url.toUri().getQueryParameter("sv"),
                    body = encryptedFile.asRequestBody(),
                )
            }
        }
            .toResult()
            .map { cipher }
    }

    override suspend fun updateCipher(
        cipherId: String,
        body: CipherJsonRequest,
    ): Result<UpdateCipherResponseJson> =
        ciphersApi
            .updateCipher(
                cipherId = cipherId,
                body = body,
            )
            .toResult()
            .map { UpdateCipherResponseJson.Success(cipher = it) }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<UpdateCipherResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun shareAttachment(
        cipherId: String,
        attachment: AttachmentInfo,
        organizationId: String,
        encryptedFile: File,
    ): Result<Unit> {
        return ciphersApi
            .shareAttachment(
                cipherId = cipherId,
                attachmentId = attachment.id,
                organizationId = organizationId,
                body = this
                    .createMultipartBodyBuilder(
                        encryptedFile = encryptedFile,
                        filename = attachment.fileName,
                    )
                    .addPart(
                        part = MultipartBody.Part.createFormData(
                            name = "key",
                            value = attachment.key,
                        ),
                    )
                    .build(),
            )
            .toResult()
    }

    override suspend fun shareCipher(
        cipherId: String,
        body: ShareCipherJsonRequest,
    ): Result<SyncResponseJson.Cipher> =
        ciphersApi
            .shareCipher(
                cipherId = cipherId,
                body = body,
            )
            .toResult()

    override suspend fun bulkShareCiphers(
        body: BulkShareCiphersJsonRequest,
    ): Result<CipherMiniResponseJson> =
        ciphersApi
            .bulkShareCiphers(body = body)
            .toResult()

    override suspend fun updateCipherCollections(
        cipherId: String,
        body: UpdateCipherCollectionsJsonRequest,
    ): Result<Unit> =
        ciphersApi
            .updateCipherCollections(
                cipherId = cipherId,
                body = body,
            )
            .toResult()

    override suspend fun hardDeleteCipher(cipherId: String): Result<Unit> =
        ciphersApi
            .hardDeleteCipher(cipherId = cipherId)
            .toResult()

    override suspend fun softDeleteCipher(cipherId: String): Result<Unit> =
        ciphersApi
            .softDeleteCipher(cipherId = cipherId)
            .toResult()

    override suspend fun deleteCipherAttachment(
        cipherId: String,
        attachmentId: String,
    ): Result<Unit> =
        ciphersApi
            .deleteCipherAttachment(
                cipherId = cipherId,
                attachmentId = attachmentId,
            )
            .toResult()

    override suspend fun restoreCipher(cipherId: String): Result<SyncResponseJson.Cipher> =
        ciphersApi
            .restoreCipher(cipherId = cipherId)
            .toResult()

    override suspend fun getCipher(
        cipherId: String,
    ): Result<SyncResponseJson.Cipher> =
        ciphersApi
            .getCipher(cipherId = cipherId)
            .toResult()

    override suspend fun getCipherAttachment(
        cipherId: String,
        attachmentId: String,
    ): Result<SyncResponseJson.Cipher.Attachment> =
        ciphersApi
            .getCipherAttachment(
                cipherId = cipherId,
                attachmentId = attachmentId,
            )
            .toResult()

    override suspend fun hasUnassignedCiphers(): Result<Boolean> =
        ciphersApi
            .hasUnassignedCiphers()
            .toResult()

    override suspend fun importCiphers(
        request: ImportCiphersJsonRequest,
    ): Result<ImportCiphersResponseJson> =
        ciphersApi
            .importCiphers(body = request)
            .toResult()
            .map { ImportCiphersResponseJson.Success }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<ImportCiphersResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    private fun createMultipartBodyBuilder(
        encryptedFile: File,
        filename: String?,
    ): MultipartBody.Builder =
        MultipartBody
            .Builder(boundary = "--BWMobileFormBoundary${clock.instant().toEpochMilli()}")
            .setType(type = MultipartBody.FORM)
            .addPart(
                part = MultipartBody.Part.createFormData(
                    body = encryptedFile.asRequestBody(
                        contentType = "application/octet-stream".toMediaType(),
                    ),
                    name = "data",
                    filename = filename,
                ),
            )
}






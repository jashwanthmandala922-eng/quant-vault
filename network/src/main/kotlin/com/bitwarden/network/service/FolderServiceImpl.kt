package com.quantvault.network.service

import com.quantvault.network.api.FoldersApi
import com.quantvault.network.model.FolderJsonRequest
import com.quantvault.network.model.SyncResponseJson
import com.quantvault.network.model.UpdateFolderResponseJson
import com.quantvault.network.model.toquantvaultError
import com.quantvault.network.util.NetworkErrorCode
import com.quantvault.network.util.parseErrorBodyOrNull
import com.quantvault.network.util.toResult
import kotlinx.serialization.json.Json

internal class FolderServiceImpl(
    private val foldersApi: FoldersApi,
    private val json: Json,
) : FolderService {
    override suspend fun createFolder(body: FolderJsonRequest): Result<SyncResponseJson.Folder> =
        foldersApi
            .createFolder(body = body)
            .toResult()

    override suspend fun updateFolder(
        folderId: String,
        body: FolderJsonRequest,
    ): Result<UpdateFolderResponseJson> =
        foldersApi
            .updateFolder(
                folderId = folderId,
                body = body,
            )
            .toResult()
            .map { UpdateFolderResponseJson.Success(folder = it) }
            .recoverCatching { throwable ->
                throwable
                    .toquantvaultError()
                    .parseErrorBodyOrNull<UpdateFolderResponseJson.Invalid>(
                        code = NetworkErrorCode.BAD_REQUEST,
                        json = json,
                    )
                    ?: throw throwable
            }

    override suspend fun deleteFolder(folderId: String): Result<Unit> =
        foldersApi
            .deleteFolder(folderId = folderId)
            .toResult()

    override suspend fun getFolder(
        folderId: String,
    ): Result<SyncResponseJson.Folder> = foldersApi
        .getFolder(folderId = folderId)
        .toResult()
}






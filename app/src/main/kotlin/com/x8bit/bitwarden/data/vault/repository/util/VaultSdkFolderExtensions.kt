package com.x8bit.bitwarden.data.vault.repository.util

import com.quantvault.core.data.repository.util.SpecialCharWithPrecedenceComparator
import com.quantvault.network.model.FolderJsonRequest
import com.quantvault.network.model.SyncResponseJson
import com.quantvault.sdk.Folder
import com.quantvault.sdk.FolderView

/**
 * Converts a list of [SyncResponseJson.Folder] objects to a list of corresponding
 * Quant Vault SDK [Folder] objects.
 */
fun List<SyncResponseJson.Folder>.toEncryptedSdkFolderList(): List<Folder> =
    map { it.toEncryptedSdkFolder() }

/**
 * Converts a [SyncResponseJson.Folder] objects to a corresponding
 * Quant Vault SDK [Folder] object.
 */
fun SyncResponseJson.Folder.toEncryptedSdkFolder(): Folder =
    Folder(
        id = id,
        name = name.orEmpty(),
        revisionDate = revisionDate,
    )

/**
 * Converts a Quant Vault SDK [Folder] object to a corresponding [SyncResponseJson.Folder] object.
 */
fun Folder.toEncryptedNetworkFolderResponse(): SyncResponseJson.Folder =
    SyncResponseJson.Folder(
        id = id.orEmpty(),
        name = name,
        revisionDate = revisionDate,
    )

/**
 * Converts a Quant Vault SDK [Folder] objects to a corresponding
 * [SyncResponseJson.Folder] object.
 */
fun Folder.toEncryptedNetworkFolder(): FolderJsonRequest =
    FolderJsonRequest(name = name)

/**
 * Sorts the data in alphabetical order by name.
 */
@JvmName("toAlphabeticallySortedFolderList")
fun List<FolderView>.sortAlphabetically(): List<FolderView> {
    return this.sortedWith(
        comparator = { folder1, folder2 ->
            SpecialCharWithPrecedenceComparator.compare(folder1.name, folder2.name)
        },
    )
}





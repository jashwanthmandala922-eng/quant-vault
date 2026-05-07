package com.x8bit.bitwarden.data.vault.repository.model

import com.quantvault.sdk.CollectionView
import com.quantvault.sdk.SendView
import com.quantvault.sdk.DecryptCipherListResult
import com.quantvault.sdk.FolderView

/**
 * Represents decrypted vault data.
 *
 * @param decryptCipherListResult Contains the result of decrypting ciphers for display in a list.
 * @param collectionViewList List of decrypted collections.
 * @param folderViewList List of decrypted folders.
 * @param sendViewList List of decrypted sends.
 */
data class VaultData(
    val decryptCipherListResult: DecryptCipherListResult,
    val collectionViewList: List<CollectionView>,
    val folderViewList: List<FolderView>,
    val sendViewList: List<SendView>,
)





package com.quantvault.send

enum class SendType {
    TEXT, FILE
}

data class SendView(
    val id: String,
    val name: String,
    val notes: String?,
    val type: SendType,
    val file: SendFileView?,
    val text: SendTextView?,
    val visibility: SendVisibility,
    val password: String?,
    val maxAccessCount: Int?,
    val accessCount: Int,
    val expirationDate: Long?,
    val creationDate: Long,
    val revisionDate: Long,
    val disabled: Boolean,
    val hideEmail: Boolean,
    val organizationId: String?
)

data class SendFileView(
    val id: String,
    val fileName: String,
    val size: Long,
    val url: String?
)

data class SendTextView(
    val text: String,
    val hidden: Boolean
)

enum class SendVisibility {
    ALL, ORGANIZATION, PRIVATE
}

data class Send(
    val id: String?,
    val name: String?,
    val notes: String?,
    val type: SendType,
    val file: SendFile?,
    val text: SendText?,
    val visibility: SendVisibility,
    val password: String?,
    val maxAccessCount: Int?,
    val accessCount: Int,
    val expirationDate: Long?,
    val creationDate: Long,
    val revisionDate: Long,
    val disabled: Boolean,
    val hideEmail: Boolean,
    val organizationId: String?
)

data class SendFile(
    val id: String?,
    val fileName: String?,
    val size: Long,
    val url: String?
)

data class SendText(
    val text: String?,
    val hidden: Boolean
)
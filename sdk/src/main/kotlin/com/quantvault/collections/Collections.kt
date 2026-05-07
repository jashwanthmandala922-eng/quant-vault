package com.quantvault.collections

data class CollectionView(
    val id: String,
    val name: String,
    val externalId: String?,
    val organizationId: String
)

enum class CollectionType {
    USER,
    ORGANIZATION
}
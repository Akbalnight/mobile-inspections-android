package ru.madbrains.domain.model

data class EquipmentModel(
    val id: String,
    val code: Int?,
    val name: String?,
    val markId: String?,
    val modelId: String?,
    val parentId: String?,
    val isGroup: Boolean?
)
package ru.madbrains.domain.model

import java.io.Serializable

data class EquipmentModel(
        val id: String,
        val code: Int?,
        val name: String?,
        val parentId: String?,
        val isGroup: Boolean?,
        val techPlace: String?,
        val techPlacePath: String?,
        val sapId: String?,
        val constructionType: String?,
        val material: String?,
        val size: String?,
        val weight: String?,
        val manufacturer: String?,
        val dateFinish: String?,
        val measuringPoints: String?,
        val dateWarrantyStart: String?,
        val dateWarrantyFinish: String?,
        val typeEquipment: String?,
        val deleted: Boolean?,
        val warrantyFiles: List<FileModel>?,
        val attachmentFiles: List<FileModel>?
) : Serializable
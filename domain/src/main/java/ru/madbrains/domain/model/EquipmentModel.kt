package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

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
    val dateFinish: Date?,
    val measuringPoints: List<String>?,
    val dateWarrantyStart: Date?,
    val dateWarrantyFinish: Date?,
    val typeEquipment: String?,
    val warrantyFiles: List<FileModel>?,
    val attachmentFiles: List<FileModel>?,
    val deleted: Boolean?
) : Serializable {
    fun getImageUrls(): List<FileModel> {
        val images = attachmentFiles?.filter { it.extension == "jpg" } ?: emptyList()
        val warrantyImages = warrantyFiles?.filter { it.extension == "jpg" } ?: emptyList()

        return images + warrantyImages
    }

    fun getAllDocs(): List<FileModel> {
        val attach = attachmentFiles ?: emptyList()
        val warranty = warrantyFiles ?: emptyList()

        return attach + warranty
    }
}
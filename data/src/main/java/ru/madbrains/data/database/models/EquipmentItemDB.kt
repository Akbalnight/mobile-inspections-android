package ru.madbrains.data.database.models

import androidx.room.Entity
import androidx.room.TypeConverters
import ru.madbrains.domain.model.FileModel
import java.util.*

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class EquipmentItemDB(
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
)
package ru.madbrains.data.database.models

import androidx.room.Entity
import androidx.room.TypeConverters
import ru.madbrains.domain.model.ExtraDataModel
import ru.madbrains.domain.model.FileModel
import java.util.*

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class DefectItemDB(
    val id: String,
    val equipmentId: String?,
    val staffDetectId: String?,
    val defectTypicalId: String?,
    val description: String?,
    val dateDetectDefect: Date?,
    val detourId: String?,
    val files: List<FileModel>?,
    val defectName: String?,
    val equipmentName: String?,
    val statusProcessId: String?,
    val extraData: List<ExtraDataModel>?,
    val changed: Boolean = false,
    val created: Boolean = false
)
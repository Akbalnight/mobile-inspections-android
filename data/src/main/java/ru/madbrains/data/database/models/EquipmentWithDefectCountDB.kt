package ru.madbrains.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class EquipmentWithDefectCountDB(
    val id: String,
    @ColumnInfo(name = "defectCount", defaultValue = "0")
    val defectCount: Int
)
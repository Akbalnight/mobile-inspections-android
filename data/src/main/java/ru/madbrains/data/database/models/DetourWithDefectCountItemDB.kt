package ru.madbrains.data.database.models

import androidx.room.*

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class DetourWithDefectCountItemDB(
    @Embedded val data: DetourItemDB,
    @ColumnInfo(name = "defectCount", defaultValue = "0")
    val defectCount: Int
)
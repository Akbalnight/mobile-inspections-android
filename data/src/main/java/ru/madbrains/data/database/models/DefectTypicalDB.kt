package ru.madbrains.data.database.models

import androidx.room.Entity
import androidx.room.TypeConverters

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class DefectTypicalDB(
    val id: String,
    val name: String?,
    val code: Int?
)
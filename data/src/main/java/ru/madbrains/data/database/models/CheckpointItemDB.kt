package ru.madbrains.data.database.models

import androidx.room.Entity
import androidx.room.TypeConverters

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class CheckpointItemDB(
    val id: String,
    val code: Int,
    val name: String,
    val rfidCode: String?,
    val changed: Boolean = false
)
package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

data class EquipmentFileModel(
    val id: String,
    val url: String,
    val name: String,
    val extension: String
): Serializable
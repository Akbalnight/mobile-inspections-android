package ru.madbrains.domain.model

import java.io.File
import java.io.Serializable
import java.util.*

data class FileModel(
    val id: String,
    val fileId: String?,
    val url: String,
    val fileName: String?,
    val extension: String,
    val date: Date?,
    val routeMapName: String?,
    val isNew:Boolean = false
) : Serializable
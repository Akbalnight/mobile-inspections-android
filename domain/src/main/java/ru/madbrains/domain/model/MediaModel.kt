package ru.madbrains.domain.model

import java.io.File
import java.io.Serializable

data class MediaModel(
    val extension: String,
    val file: File
) : Serializable
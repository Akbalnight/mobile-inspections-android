package ru.madbrains.domain.model

import java.io.File
import java.io.Serializable

data class FileModel(
        val id: String?,
        val url: String?,
        val name: String?,
        val extension: String?
) : Serializable {
    var shipped: Boolean = true
    var localFile: File? = null
}
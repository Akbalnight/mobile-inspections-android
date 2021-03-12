package ru.madbrains.domain.model

import java.io.Serializable

data class FileModel(
        val id: String?,
        val url: String?,
        val name: String?,
        val extension: String?
) : Serializable
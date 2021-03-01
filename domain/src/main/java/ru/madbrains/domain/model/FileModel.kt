package ru.madbrains.domain.model

import java.io.Serializable

data class FileModel(
        val id: String?,
        val someIdDef: String?,
        val fileId: String?
) : Serializable
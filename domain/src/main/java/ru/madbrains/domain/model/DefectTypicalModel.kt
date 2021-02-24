package ru.madbrains.domain.model

import java.io.Serializable

data class DefectTypicalModel(
        val id: String?,
        val name: String?,
        val code: Int?
) : Serializable
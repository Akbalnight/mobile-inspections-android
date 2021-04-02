package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

data class ExtraDataModel(
        val dateDetectDefect: Date?,
        val staffDetectId: String?,
        val description: String?,
        val detoursId: String?
) : Serializable
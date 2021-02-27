package ru.madbrains.domain.model

import java.io.Serializable

data class ExtraDataModel(
        val dateDetectDefect: String,
        val staffDetectId: String,
        val description: String,
        val detoursId: String
) : Serializable
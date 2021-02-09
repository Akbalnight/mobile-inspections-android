package ru.madbrains.domain.model

import java.io.Serializable

data class RoutePointModel(
    val id: String,
    val rowNumber: String,
    val controlPointId: String?,
    val controlPointName: String?,
    val techMapName: String?,
    val detoursId: String?,
    val position: Int?,
    val duration: Int?
) : Serializable
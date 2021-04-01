package ru.madbrains.domain.model

import java.io.Serializable

data class RouteModel(
    val id: String,
    val name: String,
    val code: Int?,
    val duration: Int?,
    val routesData: List<RouteDataModel>,
    val routeMaps: List<RouteMapModel>?
) : Serializable
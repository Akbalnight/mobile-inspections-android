package ru.madbrains.domain.model

import java.io.Serializable

data class RouteModel(
    val id: String,
    val name: String,
    val code: Int?,
    val duration: Int?,
    val routeData: List<RouteDataModel>
) : Serializable
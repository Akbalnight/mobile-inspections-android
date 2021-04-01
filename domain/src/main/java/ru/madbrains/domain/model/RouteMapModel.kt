package ru.madbrains.domain.model

import java.io.Serializable

data class RouteMapModel(
        val id: String,
        val routeId: String?,
        val fileId: String?,
        val position: Int?
): Serializable

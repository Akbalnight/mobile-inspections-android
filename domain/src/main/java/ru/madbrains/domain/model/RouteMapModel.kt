package ru.madbrains.domain.model

import java.io.Serializable

data class RouteMapModel(
        val id: String,
        val url: String,
        val name: String?,
        val extension: String,
        val position: Int?
): Serializable

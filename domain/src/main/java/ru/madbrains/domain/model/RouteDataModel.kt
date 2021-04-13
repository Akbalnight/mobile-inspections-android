package ru.madbrains.domain.model

import java.io.Serializable

data class RouteDataModel(
    val id: String?,
    val techMapId: String?,
    val controlPointId: String?,
    val rfidCode: String?,
    val routeMapId: String?,
    val routeId: String?,
    val duration: Int?,
    val xLocation: Int?,
    val yLocation: Int?,
    val position: Int?,
    val equipments: List<EquipmentModel>?,
    var techMap: TechMapModel?
): Serializable {
    var completed: Boolean = false
}
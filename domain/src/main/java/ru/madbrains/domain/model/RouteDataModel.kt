package ru.madbrains.domain.model

data class RouteDataModel(
    val id: String?,
    val techMapId: String?,
    val controlPointId: String?,
    val routeMapId: Int?,
    val routeId: String?,
    val duration: Int?,
    val xLocation: Int?,
    val yLocation: Int?,
    val position: Int?,
    val equipments: List<EquipmentModel>?,
    var techMap: TechMapModel?
) {
    var completed: Boolean = false
}
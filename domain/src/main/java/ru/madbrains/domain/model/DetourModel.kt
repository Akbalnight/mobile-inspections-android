package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

data class DetourModel(
    val id: String,
    val code: Int?,
    val routeId: String?,
    val staffId: String?,
    val repeaterId: String?,
    val statusId: String?,
    val statusName: String?,
    val routeName: String?,
    val name: String?,
    val staffName: String?,
    val dateStartPlan: Date?,
    val dateFinishPlan: Date?,
    val dateStartFact: Date?,
    val dateFinishFact: Date?,
    val saveOrderControlPoints: Boolean?,
    val takeIntoAccountTimeLocation: Boolean?,
    val takeIntoAccountDateStart: Boolean?,
    val takeIntoAccountDateFinish: Boolean?,
    val possibleDeviationLocationTime: Int?,
    val possibleDeviationDateStart: Int?,
    val possibleDeviationDateFinish: Int?,
    val isDefectExist: Int?,
    val frozen: Boolean?,
    val route: RouteModel,
    val changed: Boolean,
    val startTime: Date? = null
) : Serializable {
    fun getAllFilesIds(): List<String> {
        val res = arrayListOf<FileModel>()
        route.routeMaps?.let {
            res.addAll(it)
        }
        route.routesData?.let { routes ->
            for (data in routes) {
                data.equipments?.let { equipment ->
                    for (item in equipment) {
                        item.attachmentFiles?.let {
                            res.addAll(it)
                        }
                        item.warrantyFiles?.let {
                            res.addAll(it)
                        }
                    }
                }
            }
        }
        return res.mapNotNull { it.fileId }
    }
}

fun List<DetourModel>.getAllFilesIds(): List<String> {
    val res = arrayListOf<String>()
    for (item in this) {
        res.addAll(item.getAllFilesIds())
    }
    return res.distinct()
}

data class RouteModel(
    val id: String,
    val name: String,
    val code: Int?,
    val duration: Int?,
    val routesData: List<RouteDataModel>?,
    val routeMaps: List<FileModel>?
) : Serializable

data class RouteDataModel(
    val id: String?,
    val techMapId: String?,
    val controlPointId: String?,
    val controlPointName: String?,
    val rfidCode: String?,
    val routeMapId: String?,
    val routeId: String?,
    val duration: Int?,
    val xLocation: Int?,
    val yLocation: Int?,
    val position: Int?,
    val equipments: List<EquipmentModel>?,
    val techMap: TechMapModel?,
    val completed: Boolean
) : Serializable

data class TechMapModel(
    val id: String,
    val name: String?,
    val code: Int?,
    val dateStart: String?,
    val techMapsStatusId: String?,
    val parentId: String?,
    val isGroup: Boolean?,
    val techOperations: List<TechOperationModel>
) : Serializable

data class TechOperationModel(
    val id: String,
    val name: String?,
    val code: Int?,
    val needInputData: Boolean?,
    val labelInputData: String?,
    val valueInputData: String?,
    val equipmentStop: Boolean?,
    val increasedDanger: Boolean?,
    val duration: Int?,
    val techMapId: String?,
    val position: Int?
) : Serializable
package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class GetDetoursResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "routeId") val routeId: String?,
    @field:Json(name = "staffId") val staffId: String?,
    @field:Json(name = "repeaterId") val repeaterId: String?,
    @field:Json(name = "statusId") val statusId: String?,
    @field:Json(name = "statusName") val statusName: String?,
    @field:Json(name = "routeName") val routeName: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "staffName") val staffName: String?,
    @field:Json(name = "dateStartPlan") val dateStartPlan: String?,
    @field:Json(name = "dateFinishPlan") val dateFinishPlan: String?,
    @field:Json(name = "dateStartFact") val dateStartFact: String?,
    @field:Json(name = "dateFinishFact") val dateFinishFact: String?,
    @field:Json(name = "saveOrderControlPoints") val saveOrderControlPoints: Boolean?,
    @field:Json(name = "takeIntoAccountTimeLocation") val takeIntoAccountTimeLocation: Boolean?,
    @field:Json(name = "takeIntoAccountDateStart") val takeIntoAccountDateStart: Boolean?,
    @field:Json(name = "takeIntoAccountDateFinish") val takeIntoAccountDateFinish: Boolean?,
    @field:Json(name = "possibleDeviationLocationTime") val possibleDeviationLocationTime: Int?,
    @field:Json(name = "possibleDeviationDateStart") val possibleDeviationDateStart: Int?,
    @field:Json(name = "possibleDeviationDateFinish") val possibleDeviationDateFinish: Int?,
    @field:Json(name = "isDefectExist") val isDefectExist: Int?,
    @field:Json(name = "frozen") val frozen: Boolean?,
    @field:Json(name = "route") val route: GetRouteResp
)

@JsonClass(generateAdapter = true)
data class GetRouteResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "duration") val duration: Int?,
    @field:Json(name = "routesData") val routesData: List<GetRouteDataResp>
)

@JsonClass(generateAdapter = true)
data class GetRouteDataResp(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "techMapId") val techMapId: String?,
    @field:Json(name = "controlPointId") val controlPointId: String?,
    @field:Json(name = "routeMapId") val routeMapId: String?,
    @field:Json(name = "routeId") val routeId: String?,
    @field:Json(name = "duration") val duration: Int?,
    @field:Json(name = "xLocation") val xLocation: Int?,
    @field:Json(name = "yLocation") val yLocation: Int?,
    @field:Json(name = "position") val position: Int?,
    @field:Json(name = "equipments") val equipments: List<GetEquipmentResp>?,
    @field:Json(name = "techMap") val techMap: GetTechMapResp?
)

@JsonClass(generateAdapter = true)
data class GetEquipmentResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "parentId") val parentId: String?,
    @field:Json(name = "isGroup") val isGroup: Boolean?,
    @field:Json(name = "techPlace") val techPlace: String?,
    @field:Json(name = "techPlacePath") val techPlacePath: String?,
    @field:Json(name = "sapId") val sapId: String?,
    @field:Json(name = "constructionType") val constructionType: String?,
    @field:Json(name = "material") val material: String?,
    @field:Json(name = "size") val size: String?,
    @field:Json(name = "weight") val weight: String?,
    @field:Json(name = "manufacturer") val manufacturer: String?,
    @field:Json(name = "dateFinish") val dateFinish: Date?,
    @field:Json(name = "measuringPoints") val measuringPoints: List<String>?,
    @field:Json(name = "dateWarrantyStart") val dateWarrantyStart: Date?,
    @field:Json(name = "dateWarrantyFinish") val dateWarrantyFinish: Date?,
    @field:Json(name = "typeEquipment") val typeEquipment: String?,
    @field:Json(name = "warrantyFiles") val warrantyFiles: List<GetEquipmentFileResp>?,
    @field:Json(name = "attachmentFiles") val attachmentFiles: List<GetEquipmentFileResp>?,
    @field:Json(name = "deleted") val deleted: Boolean?
)
@JsonClass(generateAdapter = true)
data class GetEquipmentFileResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "extension") val extension: String
)

@JsonClass(generateAdapter = true)
data class GetTechMapResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "dateStart") val dateStart: String?,
    @field:Json(name = "techMapsStatusId") val techMapsStatusId: String?,
    @field:Json(name = "parentId") val parentId: String?,
    @field:Json(name = "isGroup") val isGroup: Boolean?,
    @field:Json(name = "techOperations") val techOperations: List<GetTechOperationResp>
)

@JsonClass(generateAdapter = true)
data class GetTechOperationResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "needInputData") val needInputData: Boolean?,
    @field:Json(name = "labelInputData") val labelInputData: String?,
    @field:Json(name = "valueInputData") val valueInputData: String?,
    @field:Json(name = "equipmentStop") val equipmentStop: Boolean?,
    @field:Json(name = "increasedDanger") val increasedDanger: Boolean?,
    @field:Json(name = "duration") val duration: Int?,
    @field:Json(name = "techMapId") val techMapId: String?,
    @field:Json(name = "position") val position: Int?
)
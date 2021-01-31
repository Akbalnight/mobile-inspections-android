package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetRouteResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "routeId") val routeId: String?,
    @field:Json(name = "staffId") val staffId: String?,
    @field:Json(name = "repeaterId") val repeaterId: String?,
    @field:Json(name = "status_id") val statusId: String?,
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
    @field:Json(name = "isDefectExist") val isDefectExist: Int?
)
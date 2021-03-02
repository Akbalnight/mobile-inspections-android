package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

data class DetourModel(
    val id: String,
    val code: Int?,
    val routeId: String?,
    val staffId: String?,
    val repeaterId: String?,
    var statusId: String?,
    val statusName: String?,
    val routeName: String?,
    val name: String?,
    val staffName: String?,
    val dateStartPlan: String?,
    val dateFinishPlan: String?,
    var dateStartFact: String?,
    var dateFinishFact: String?,
    val saveOrderControlPoints: Boolean?,
    val takeIntoAccountTimeLocation: Boolean?,
    val takeIntoAccountDateStart: Boolean?,
    val takeIntoAccountDateFinish: Boolean?,
    val possibleDeviationLocationTime: Int?,
    val possibleDeviationDateStart: Int?,
    val possibleDeviationDateFinish: Int?,
    val isDefectExist: Int?,
    val frozen: Boolean?,
    var route: RouteModel
) : Serializable {
    @Transient var startTime: Date? = null
}
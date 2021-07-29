package ru.madbrains.data.database.models

import androidx.room.Entity
import androidx.room.TypeConverters
import ru.madbrains.domain.model.RouteModel
import java.util.*

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class DetourItemDB(
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
    val changed: Boolean = false
)
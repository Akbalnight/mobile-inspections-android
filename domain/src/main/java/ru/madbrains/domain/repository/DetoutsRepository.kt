package ru.madbrains.domain.repository

import io.reactivex.Single
import ru.madbrains.domain.model.*

interface DetoutsRepository {
    fun getDetours(): Single<List<DetourModel>>

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>>

    fun getDefectTypical(): Single<List<DefectTypicalModel>>

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentsModel>>

    fun getDefects(id: String?,
                   codes: List<String>?,
                   dateDetectStart: String?,
                   dateDetectEnd: String?,
                   detourIds: List<String>?,
                   defectNames: List<String>?,
                   equipmentNames: List<String>?,
                   statusProcessId: String?): Single<List<DefectModel>>
}
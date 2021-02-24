package ru.madbrains.domain.repository

import io.reactivex.Single
import ru.madbrains.domain.model.*

interface RoutesRepository {
    fun getRoutes(): Single<List<RouteModel>>

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>>

    fun getDefectTypical(): Single<List<DefectTypicalModel>>

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentsModel>>
}
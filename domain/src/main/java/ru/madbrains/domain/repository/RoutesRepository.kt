package ru.madbrains.domain.repository

import io.reactivex.Single
import ru.madbrains.domain.model.DefectTypicalModel
import ru.madbrains.domain.model.PlanTechOperationsModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel

interface RoutesRepository {
    fun getRoutes(): Single<List<RouteModel>>

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>>

    fun getDefectTypical(): Single<List<DefectTypicalModel>>
}
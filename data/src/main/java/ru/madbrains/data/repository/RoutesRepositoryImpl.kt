package ru.madbrains.data.repository

import io.reactivex.Single
import ru.madbrains.data.network.api.InspectionApi
import ru.madbrains.data.network.mappers.mapGetRoutePointsResp
import ru.madbrains.data.network.mappers.mapGetPlanTechOperationsResp
import ru.madbrains.data.network.mappers.mapGetRoutesResp
import ru.madbrains.data.network.request.GetPlanTechOperationsReq
import ru.madbrains.data.network.request.GetRotesReq
import ru.madbrains.data.network.request.GetRoutePointsReq
import ru.madbrains.domain.model.PlanTechOperationsModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.domain.repository.RoutesRepository

class RoutesRepositoryImpl(
    private val inspectionApi: InspectionApi
) : RoutesRepository {
    override fun getRoutes(): Single<List<RouteModel>> {
        val request = GetRotesReq()
        return inspectionApi.getRoutes(request).map { resp ->
            resp.map { mapGetRoutesResp(it) }
        }
    }

    override fun getRoutePoints(routeId: String): Single<List<RoutePointModel>> {
        val request = GetRoutePointsReq(
            routeId = routeId
        )
        return inspectionApi.getRoutePoints(request).map { resp ->
            resp.map { mapGetRoutePointsResp(it) }
        }
    }

    override fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>> {
        val request = GetPlanTechOperationsReq(
            dataId = dataId
        )
        return inspectionApi.getPlanTechOperations(request).map { resp ->
            resp.map { mapGetPlanTechOperationsResp(it) }
        }
    }

}
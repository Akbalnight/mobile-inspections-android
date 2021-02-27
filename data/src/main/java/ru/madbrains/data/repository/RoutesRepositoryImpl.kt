package ru.madbrains.data.repository

import io.reactivex.Single
import ru.madbrains.data.network.api.InspectionApi
import ru.madbrains.data.network.mappers.*
import ru.madbrains.data.network.request.*
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoutsRepository

class RoutesRepositoryImpl(
    private val inspectionApi: InspectionApi
) : DetoutsRepository {
    override fun getDetours(): Single<List<DetourModel>> {
        val request = GetDetoursReq()
        return inspectionApi.getDetours(request).map { resp ->
            resp.map { mapGetDetoursResp(it) }
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

    override fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        val request = GetDefectTypicalReq()
        return inspectionApi.getDefectTypical(request).map { resp ->
            resp.map { mapGetDefectTypicalResp(it) }
        }
    }

    override fun getEquipments(
        names: List<String>,
        uuid: List<String>
    ): Single<List<EquipmentsModel>> {
        val request = GetEquipmentsReq(names = names, controlPointIds = uuid)
        return inspectionApi.getEquipments(request).map { resp ->
            resp.map { mapGetEquipmentsResp(it) }
        }
    }

}
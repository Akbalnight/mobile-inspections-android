package ru.madbrains.data.repository

import com.squareup.moshi.Json
import io.reactivex.Single
import ru.madbrains.data.network.api.InspectionApi
import ru.madbrains.data.network.mappers.*
import ru.madbrains.data.network.request.*
import ru.madbrains.domain.model.*
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

    override fun getDefects(id: String, codes: List<String>, dateDetectStart: String, dateDetectEnd: String, detourIds: List<String>, defectNames: List<String>, equipmentNames: List<String>, statusProcessId: String): Single<List<DefectModel>> {
        val request = GetDefectsReq(
                id = id,
                codes = codes,
                dateDetectStart = dateDetectStart,
                dateDetectEnd = dateDetectEnd,
                detourIds = detourIds,
                defectNames = defectNames,
                equipmentNames = equipmentNames,
                statusProcessId = statusProcessId)
        return inspectionApi.getDefects(request).map { resp ->
            resp.map { mapGetDefectsResp(it) }
        }

    }

}
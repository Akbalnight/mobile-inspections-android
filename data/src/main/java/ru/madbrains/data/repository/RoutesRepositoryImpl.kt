package ru.madbrains.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.data.network.ApiData
import ru.madbrains.data.network.mappers.*
import ru.madbrains.data.network.request.*
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoutsRepository
import java.io.File

class RoutesRepositoryImpl(
    private val preferenceStorage: PreferenceStorage
) : DetoutsRepository {

    override fun getDetours(): Single<List<DetourModel>> {
        val request = GetDetoursReq(
            staffIds = listOf(preferenceStorage.userId.orEmpty())
        )
        return ApiData.inspectionApi.getDetours(request).map { resp ->
            resp.map { mapGetDetoursResp(it) }
        }
    }

    override fun saveDetour(detour: DetourModel): Completable {
        return ApiData.inspectionApi.saveDetour(detour)
    }

    override fun freezeDetours(detourIds: List<String>): Completable {
        val request = FreezeDetoursReq(
            detourIds = detourIds
        )
        return ApiData.inspectionApi.freezeDetours(request)
    }

    override fun getRoutePoints(routeId: String): Single<List<RoutePointModel>> {
        val request = GetRoutePointsReq(
            routeId = routeId
        )
        return ApiData.inspectionApi.getRoutePoints(request).map { resp ->
            resp.map { mapGetRoutePointsResp(it) }
        }
    }

    override fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>> {
        val request = GetPlanTechOperationsReq(
            dataId = dataId
        )
        return ApiData.inspectionApi.getPlanTechOperations(request).map { resp ->
            resp.map { mapGetPlanTechOperationsResp(it) }
        }
    }

    override fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        val request = GetDefectTypicalReq()
        return ApiData.inspectionApi.getDefectTypical(request).map { resp ->
            resp.map { mapGetDefectTypicalResp(it) }
        }
    }

    override fun getEquipments(
        names: List<String>,
        uuid: List<String>
    ): Single<List<EquipmentModel>> {
        val request = GetEquipmentsReq(names = names, controlPointIds = uuid)
        return ApiData.inspectionApi.getEquipments(request).map { resp ->
            resp.map { mapGetEquipmentResp(it) }
        }
    }

    override fun getDefects(
        id: String?,
        codes: List<String>?,
        dateDetectStart: String?,
        dateDetectEnd: String?,
        detourIds: List<String>?,
        defectNames: List<String>?,
        equipmentNames: List<String>?,
        statusProcessId: String?
    ): Single<List<DefectModel>> {
        val request = GetDefectsReq(
            id = id,
            codes = codes,
            dateDetectStart = dateDetectStart,
            dateDetectEnd = dateDetectEnd,
            detourIds = detourIds,
            defectNames = defectNames,
            equipmentNames = equipmentNames,
            statusProcessId = statusProcessId
        )
        return ApiData.inspectionApi.getDefects(request).map { resp ->
            resp.map { mapGetDefectsResp(it) }
        }

    }

    override fun saveDefect(
        files: List<File>?,
        detoursId: String?,
        equipmentId: String?,
        staffDetectId: String?,
        defectTypicalId: String?,
        description: String?,
        dateDetectDefect: String?
    ): Single<String> {
        val request = CreateDefectReq(
            detoursId = detoursId,
            equipmentId = equipmentId,
            staffDetectId = staffDetectId,
            defectTypicalId = defectTypicalId,
            description = description,
            dateDetectDefect = dateDetectDefect
        )

        var multiParts: List<MultipartBody.Part>? = null
        files?.let { list ->
            if (list.isNotEmpty()) {
                val body = MultipartBody.Builder().apply {
                    list.forEach { item ->
                        addFormDataPart(
                            name = "files",
                            filename = item.name,
                            body = item.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    }

                }.build()
                multiParts = body.parts
            }
        }

        return ApiData.inspectionApi.saveDefect(request, multiParts)
    }

    override fun downloadFile(fileUrl: String): Single<Response<ResponseBody>> {
        return ApiData.inspectionApi.downloadFile(fileUrl)
    }

}
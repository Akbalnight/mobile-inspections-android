package ru.madbrains.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.data.extensions.toyyyyMMddTHHmmssXXX
import ru.madbrains.data.network.ApiData
import ru.madbrains.data.network.mappers.*
import ru.madbrains.data.network.request.*
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.RemoteRepository
import java.io.File

class RemoteRepositoryImpl(
    private val preferenceStorage: PreferenceStorage
) : RemoteRepository {

    private val _syncedItemsFinish = BehaviorSubject.create<String>()
    override val syncedItemsFinish: Observable<String>
        get() = _syncedItemsFinish

    override fun signalFinishSyncingItem(id: String) {
        _syncedItemsFinish.onNext(id)
    }

    override fun getDetours(statuses: List<DetourStatus>): Single<List<DetourModel>> {
        val request = GetDetoursReq(
            statusIds = statuses.map { it.id },
            staffIds = listOf(preferenceStorage.userId.orEmpty())
        )
        return ApiData.inspectionApi.getDetours(request).map { resp ->
            resp.map { mapGetDetoursResp(it) }
        }
    }

    override fun getDetoursStatuses(): Single<List<DetourStatus>> {
        return ApiData.inspectionApi.getDetoursStatuses(Object()).map { resp ->
            resp.map { mapGetDefectStatusResp(it) }
        }
    }

    override fun updateDetour(detour: DetourModel): Completable {
        return ApiData.inspectionApi.updateDetour(mapDetoursReq(detour))
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
        return ApiData.inspectionApi.getDefectTypical(Object()).map { resp ->
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

    override fun getCheckpoints(): Single<List<CheckpointModel>> {
        return ApiData.inspectionApi.getCheckpoints(Object(), 0, 1).map { resp ->
            resp.map { mapGetCheckpointResp(it) }
        }
    }

    override fun updateCheckpoint(id: String, rfidCode: String): Single<Any> {
        return ApiData.inspectionApi.updateCheckpoint(CheckpointUpdateReq(id, rfidCode))
    }

    override fun getDefects(detourIds: List<String>): Single<List<DefectModel>> {
        val request = GetDefectsReq(detourIds)
        return ApiData.inspectionApi.getDefects(request).map { resp ->
            resp.map { mapGetDefectsResp(it) }
        }

    }

    override fun saveDefect(model: DefectModel, files: List<File>?): Single<String> {
        val request = CreateDefectReq(
            detourId = model.detourId,
            equipmentId = model.equipmentId,
            staffDetectId = preferenceStorage.userId.orEmpty(),
            defectTypicalId = model.defectTypicalId,
            description = model.description,
            dateDetectDefect = model.dateDetectDefect?.toyyyyMMddTHHmmssXXX(),
            statusProcessId = model.statusProcessId
        )
        var multiParts: List<MultipartBody.Part>? = null
        files?.let { list ->
            if (list.isNotEmpty()) {
                val body = MultipartBody.Builder().apply {
                    list.forEach { item ->
                        addFormDataPart(
                            name = "files",
                            filename = "${item.name}.${item.extension}",
                            body = item.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    }
                }.build()
                multiParts = body.parts
            }
        }
        return ApiData.inspectionApi.saveDefect(request, multiParts)
    }

    override fun updateDefect(model: DefectModel, files: List<File>?): Single<String> {
        val request = UpdateDefectReq(
            id = model.id,
            statusProcessId = model.statusProcessId,
            extraData = UpdateExtraDefectReq(
                dateDetectDefect = model.dateDetectDefect?.toyyyyMMddTHHmmssXXX(),
                staffDetectId = preferenceStorage.userId.orEmpty(),
                description = model.description,
                detoursId = model.detourId
            )
        )
        var multiParts: List<MultipartBody.Part>? = null
        files?.let { list ->
            if (list.isNotEmpty()) {
                val body = MultipartBody.Builder().apply {
                    list.forEach { item ->
                        addFormDataPart(
                            name = "files",
                            filename = "${item}.${item.extension}",
                            body = item.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    }
                }.build()
                multiParts = body.parts
            }

        }
        return ApiData.inspectionApi.updateDefect(request, multiParts)
    }

    override fun downloadFileArchive(ids: List<String>): Single<Response<ResponseBody>> {
        return ApiData.inspectionApi.downloadArchive(ids)
    }

}
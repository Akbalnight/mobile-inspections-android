package ru.madbrains.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import java.io.File

interface RemoteRepository {
    val syncedItemsFinish: Observable<String>

    fun getDetours(statuses: List<DetourStatus>): Single<List<DetourModel>>

    fun updateDetour(detour: DetourModel): Completable

    fun freezeDetours(detourIds: List<String>): Completable

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>>

    fun getDefectTypical(): Single<List<DefectTypicalModel>>

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentModel>>

    fun getCheckpoints(): Single<List<CheckpointModel>>

    fun updateCheckpoint(id: String, rfidCode: String): Single<Any>

    fun getDefects(
        id: String?,
        codes: List<String>?,
        dateDetectStart: String?,
        dateDetectEnd: String?,
        detourIds: List<String>?,
        defectNames: List<String>?,
        equipmentNames: List<String>?,
        equipmentIds: List<String>?,
        statusProcessId: String?
    ): Single<List<DefectModel>>


    fun saveDefect(model: DefectModel, files: List<File>?): Single<String>

    fun updateDefect(model: DefectModel, files: List<File>?): Single<String>

    fun getDetoursStatuses(): Single<List<DetourStatus>>

    fun downloadFileArchive(ids: List<String>): Single<Response<ResponseBody>>
    fun signalFinishSyncingItem(id: String)
}
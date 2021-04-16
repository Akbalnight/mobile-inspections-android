package ru.madbrains.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*

interface DetoursRepository {
    fun getDetours(): Single<List<DetourModel>>

    fun saveDetour(detour: DetourModel): Completable

    fun freezeDetours(detourIds: List<String>): Completable

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>>

    fun getDefectTypical(): Single<List<DefectTypicalModel>>

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentModel>>

    fun getCheckpoints(): Single<List<CheckpointModel>>

    fun updateCheckpoint(id:String, rfidCode: String): Single<Any>

    fun getDefects(id: String?,
                   codes: List<String>?,
                   dateDetectStart: String?,
                   dateDetectEnd: String?,
                   detourIds: List<String>?,
                   defectNames: List<String>?,
                   equipmentNames: List<String>?,
                   equipmentIds: List<String>?,
                   statusProcessId: String?): Single<List<DefectModel>>


    fun saveDefect(files: List<MediaModel>?,
                   detourId: String?,
                   equipmentId: String?,
                   staffDetectId: String?,
                   defectTypicalId: String?,
                   description: String?,
                   dateDetectDefect: String?,
                   statusProcessId: String?
    ): Single<String>

    fun updateDefect(files: List<MediaModel>?,
                     id: String?,
                     statusProcessId: String?,
                     dateDetectDefect: String?,
                     staffDetectId: String?,
                     description: String?,
                     detoursId: String?
    ): Single<String>

    fun downloadFile(fileUrl: String): Single<Response<ResponseBody>>

    fun getDetoursStatuses(): Single<List<DetourStatus>>
}
package ru.madbrains.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import java.io.File

interface DetoutsRepository {
    fun getDetours(): Single<List<DetourModel>>

    fun saveDetour(detour: DetourModel): Completable

    fun freezeDetours(detourIds: List<String>): Completable

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>>

    fun getDefectTypical(): Single<List<DefectTypicalModel>>

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentModel>>

    fun getDefects(id: String?,
                   codes: List<String>?,
                   dateDetectStart: String?,
                   dateDetectEnd: String?,
                   detourIds: List<String>?,
                   defectNames: List<String>?,
                   equipmentNames: List<String>?,
                   statusProcessId: String?): Single<List<DefectModel>>


    fun saveDefect(files: List<File>?,
                   detoursId: String?,
                   equipmentId: String?,
                   staffDetectId: String?,
                   defectTypicalId: String?,
                   description: String?,
                   dateDetectDefect: String?
    ): Single<String>

    fun updateDefect(files: List<File>?,
                     id: String?,
                     statusProcessId: String?,
                     dateDetectDefect: String?,
                     staffDetectId: String?,
                     description: String?,
                     detoursId: String?
    ): Single<String>

    fun downloadFile(fileUrl: String): Single<Response<ResponseBody>>
}
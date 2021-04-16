package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoursRepository

class RoutesInteractor(
        private val routesRepository: DetoursRepository
) {
    fun getDetours(): Single<List<DetourModel>> {
        return routesRepository.getDetours()
                .subscribeOn(Schedulers.io())
    }
    fun getDetoursStatuses(): Single<List<DetourStatus>> {
        return routesRepository.getDetoursStatuses()
                .subscribeOn(Schedulers.io())
    }
    fun getCheckpoints(): Single<List<CheckpointModel>> {
        return routesRepository.getCheckpoints()
                .subscribeOn(Schedulers.io())
    }
    fun updateCheckpoint(id: String, rfidCode: String): Single<Any> {
        return routesRepository.updateCheckpoint(id, rfidCode)
                .subscribeOn(Schedulers.io())
    }

    fun saveDetour(detour: DetourModel): Completable {
        return routesRepository.saveDetour(detour)
                .subscribeOn(Schedulers.io())
    }

    fun freezeDetours(detourIds: List<String>): Completable {
        return if (detourIds.isEmpty()) Completable.complete() else {
            routesRepository.freezeDetours(detourIds)
                    .subscribeOn(Schedulers.io())
        }
    }

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>> {
        return routesRepository.getRoutePoints(routeId)
                .subscribeOn(Schedulers.io())
    }

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>> {
        return routesRepository.getPlanTechOperations(dataId)
                .subscribeOn(Schedulers.io())
    }

    fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        return routesRepository.getDefectTypical()
                .subscribeOn(Schedulers.io())
    }

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentModel>> {
        return routesRepository.getEquipments(names, uuid)
                .subscribeOn(Schedulers.io())
    }

    fun downloadFile(fileUrl: String): Single<Response<ResponseBody>> {
        return routesRepository.downloadFile(fileUrl)
                .subscribeOn(Schedulers.io())
    }

    fun getDefects(id: String? = null,
                   codes: List<String>? = null,
                   dateDetectStart: String? = null,
                   dateDetectEnd: String? = null,
                   detourIds: List<String>? = null,
                   defectNames: List<String>? = null,
                   equipmentNames: List<String>? = null,
                   equipmentIds: List<String>? = null,
                   statusProcessId: String? = null): Single<List<DefectModel>> {
        return routesRepository.getDefects(id, codes, dateDetectStart, dateDetectEnd, detourIds, defectNames, equipmentNames, equipmentIds, statusProcessId)
                .subscribeOn(Schedulers.io())
    }

    fun saveDefect(files: List<MediaModel>? = null,
                   detourId: String? = null,
                   equipmentId: String? = null,
                   staffDetectId: String? = null,
                   defectTypicalId: String? = null,
                   description: String? = null,
                   dateDetectDefect: String? = null,
                   statusProcessId: String? = null): Single<String> {

        return routesRepository.saveDefect(files, detourId, equipmentId, staffDetectId, defectTypicalId, description, dateDetectDefect, statusProcessId).subscribeOn(Schedulers.io())
    }

    fun updateDefect(files: List<MediaModel>? = null,
                     id: String? = null,
                     statusProcessId: String? = null,
                     dateDetectDefect: String? = null,
                     staffDetectId: String? = null,
                     description: String? = null,
                     detoursId: String? = null): Single<String> {

        return routesRepository.updateDefect(files, id, statusProcessId, dateDetectDefect, staffDetectId, description, detoursId).subscribeOn(Schedulers.io())
    }
}

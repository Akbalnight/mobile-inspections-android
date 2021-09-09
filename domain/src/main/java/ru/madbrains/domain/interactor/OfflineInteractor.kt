package ru.madbrains.domain.interactor

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.OfflineRepository
import java.io.File

class OfflineInteractor(
    private val offlineRepository: OfflineRepository
) {

    val detoursSource: Observable<List<DetourModel>>
        get() = offlineRepository.detoursSource.subscribeOn(Schedulers.io())

    val syncInfoSource: Observable<SyncInfo>
        get() = offlineRepository.syncInfoSource.subscribeOn(Schedulers.io())

    fun getFileInFolder(name: String?, folder: AppDirType): File? {
        return offlineRepository.getFileInFolder(name, folder)
    }

    fun getAllDefects(): Single<List<DefectModel>> {
        return offlineRepository.getDefects().subscribeOn(Schedulers.io())
    }

    fun getActiveDefects(detourId: String?, equipmentIds: List<String>): Single<List<DefectModel>> {
        return offlineRepository.getActiveDefects(detourId, equipmentIds)
            .subscribeOn(Schedulers.io())
    }

    private fun RouteDataModel.isRouteHaveDefect(set: Set<String>): Boolean {
        if (this.equipments != null) {
            for (equipment in this.equipments) {
                if (set.contains(equipment.id)) {
                    return true
                }
            }
        }
        return false
    }

    private fun RouteDataModel.countRouteDefect(map: Map<String, Int>): Int {
        var res = 0
        if (this.equipments != null) {
            for (equipment in this.equipments) {
                val count = map.getOrDefault(equipment.id, 0)
                res += count
            }
        }
        return res
    }

    fun getRoutesWithDefects(
        detourId: String,
        routes: List<RouteDataModel>
    ): Single<List<RouteDataWithDefect>> {
        return offlineRepository.getEquipmentIdsWithDefects(detourId, routes.getAllEquipmentIds())
            .map { set ->
                routes.map {
                    RouteDataWithDefect(it, it.isRouteHaveDefect(set))
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun getRoutesWithDefectCount(
        detourId: String,
        routes: List<RouteDataModel>
    ): Single<List<RouteDataWithDefectCount>> {
        return offlineRepository.getEquipmentsWithDefectsCount(
            detourId,
            routes.getAllEquipmentIds()
        )
            .map { map ->
                routes.map {
                    RouteDataWithDefectCount(it, it.countRouteDefect(map))
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun getEquipments(): Single<List<EquipmentModel>> {
        return offlineRepository.getEquipments().subscribeOn(Schedulers.io())
    }

    fun getCheckpoints(): Single<List<CheckpointModel>> {
        return offlineRepository.getCheckpoints().subscribeOn(Schedulers.io())
    }

    fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        return offlineRepository.getDefectsTypical().subscribeOn(Schedulers.io())
    }

    fun getChangedDataForSync(): Single<WrapChangedData> {
        return Single.zip(offlineRepository.getChangedDetours(),
            offlineRepository.getChangedDefects(),
            offlineRepository.getChangedCheckpoints(),
            Function3 { b1: List<DetourModel>, b2: List<DefectModel>, b3: List<CheckpointModel> ->
                WrapChangedData(
                    b1,
                    b2,
                    b3
                )
            })
            .subscribeOn(Schedulers.io())
    }

    fun getDetoursAndRefreshSource(): Single<List<DetourModel>> {
        return offlineRepository.getDetoursAndRefreshSource().subscribeOn(Schedulers.io())
    }
}

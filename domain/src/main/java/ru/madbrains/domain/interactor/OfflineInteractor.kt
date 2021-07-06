package ru.madbrains.domain.interactor

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.OfflineRepository
import java.io.File

class OfflineInteractor(
    private val offlineRepository: OfflineRepository
) {

    fun getFileInFolder(name: String?, folder: AppDirType): File? {
        return offlineRepository.getFileInFolder(name, folder)
    }

    fun getDefectsDb(): Single<List<DefectModel>> {
        return offlineRepository.getDefects().subscribeOn(Schedulers.io())
    }

    fun getActiveDefectsDb(equipmentIds: List<String>): Single<List<DefectModel>> {
        return offlineRepository.getActiveDefects(equipmentIds).subscribeOn(Schedulers.io())
    }

    fun getEquipmentIdsWithDefectsDB(equipmentIds: List<String>): Single<List<String>> {
        return offlineRepository.getEquipmentIdsWithDefects(equipmentIds)
            .subscribeOn(Schedulers.io())
    }

    fun getEquipmentsDb(): Single<List<EquipmentModel>> {
        return offlineRepository.getEquipments().subscribeOn(Schedulers.io())
    }

    fun getDefectTypicalDb(): Single<List<DefectTypicalModel>> {
        return offlineRepository.getDefectsTypical().subscribeOn(Schedulers.io())
    }

    fun getDetoursSourceDb(): Observable<List<DetourModel>> {
        return offlineRepository.getDetoursSource().subscribeOn(Schedulers.io())
    }

    fun getSyncInfoSource(): Observable<SyncInfo> {
        return offlineRepository.getSyncInfoSource()
    }

    fun getChangedDetoursDb(): Single<List<DetourModel>> {
        return offlineRepository.getChangedDetours().subscribeOn(Schedulers.io())
    }

    fun getChangedDefectsDb(): Single<List<DefectModel>> {
        return offlineRepository.getChangedDefects().subscribeOn(Schedulers.io())
    }
}

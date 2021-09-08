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

    val detoursSource: Observable<List<DetourModelWithDefectCount>>
        get() = offlineRepository.detoursSource.subscribeOn(Schedulers.io())

    val syncInfoSource: Observable<SyncInfo>
        get() = offlineRepository.syncInfoSource.subscribeOn(Schedulers.io())

    fun getFileInFolder(name: String?, folder: AppDirType): File? {
        return offlineRepository.getFileInFolder(name, folder)
    }

    fun getDefects(): Single<List<DefectModel>> {
        return offlineRepository.getDefects().subscribeOn(Schedulers.io())
    }

    fun getActiveDefects(equipmentIds: List<String>): Single<List<DefectModel>> {
        return offlineRepository.getActiveDefects(equipmentIds).subscribeOn(Schedulers.io())
    }

    fun getEquipmentIdsWithDefects(equipmentIds: List<String>): Single<List<String>> {
        return offlineRepository.getEquipmentIdsWithDefects(equipmentIds)
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

    fun getDetoursAndRefreshSource(): Single<List<DetourModelWithDefectCount>> {
        return offlineRepository.getDetoursAndRefreshSource().subscribeOn(Schedulers.io())
    }
}

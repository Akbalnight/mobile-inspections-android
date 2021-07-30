package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.OfflineRepository
import ru.madbrains.domain.repository.RemoteRepository
import java.io.File
import java.util.*

class SyncInteractor(
    private val offlineRepository: OfflineRepository,
    private val remoteRepository: RemoteRepository
) {
    fun setDirectories(fileTempDir: File?, fileSaveDir: File?) {
        offlineRepository.setDirectories(fileTempDir, fileSaveDir)
    }

    fun logoutClean(): Completable {
        return offlineRepository.logoutClean().subscribeOn(Schedulers.io())
    }

    fun delDefectDb(id: String): Completable {
        return offlineRepository.deleteDefect(id).subscribeOn(Schedulers.io())
    }

    fun deleteFile(file: File?): Completable {
        return offlineRepository.deleteFile(file).subscribeOn(Schedulers.io())
    }

    fun checkIfNeedsCleaningAndRefreshDetours(): Completable {
        return offlineRepository.checkIfNeedsCleaningAndRefreshDetours()
            .subscribeOn(Schedulers.io())
    }

    fun insertDetour(detour: DetourModel): Completable {
        return offlineRepository.insertDetour(detour).subscribeOn(Schedulers.io())
    }

    fun insertDefect(model: DefectModel): Completable {
        return offlineRepository.insertDefect(model).subscribeOn(Schedulers.io())
    }

    fun syncPendingDataAndRefresh(dataWrap: WrapPendingDataSync): Completable {
        val observables = arrayListOf<Completable>()
        dataWrap.routes?.let {
            observables.add(insertDetours(it))
        }
        dataWrap.defects?.let {
            observables.add(insertDefects(it))
        }
        dataWrap.equipment?.let {
            observables.add(insertEquipments(it))
        }
        dataWrap.defectsTypical?.let {
            observables.add(insertDefectTypical(it))
        }
        dataWrap.checkpoints?.let {
            observables.add(insertCheckpoints(it))
        }
        dataWrap.docArchive?.let {
            observables.add(unzipFiles(it, AppDirType.Docs))
        }
        dataWrap.mediaArchive?.let {
            observables.add(unzipFiles(it, AppDirType.Defects))
        }
        return cleanDbAndFiles()
            .andThen(Completable.merge(observables))
            .andThen(Completable.fromSingle(offlineRepository.getDetoursAndRefreshSource()))
            .doOnComplete { finishGetSync(Date()) }
            .subscribeOn(Schedulers.io())
    }

    private fun finishGetSync(date: Date) {
        offlineRepository.finishGetSync(date)
    }

    private fun unzipFiles(zipFile: File, folder: AppDirType): Completable {
        return offlineRepository.unzipFiles(zipFile, folder).subscribeOn(Schedulers.io())
    }

    private fun insertEquipments(models: List<EquipmentModel>): Completable {
        return offlineRepository.insertEquipments(models).subscribeOn(Schedulers.io())
    }

    private fun insertDetours(models: List<DetourModel>): Completable {
        return offlineRepository.insertDetours(models).subscribeOn(Schedulers.io())
    }

    private fun insertDefectTypical(models: List<DefectTypicalModel>): Completable {
        return offlineRepository.insertDefectsTypical(models).subscribeOn(Schedulers.io())
    }

    private fun insertCheckpoints(models: List<CheckpointModel>): Completable {
        return offlineRepository.insertCheckpoints(models).subscribeOn(Schedulers.io())
    }

    private fun insertDefects(models: List<DefectModel>): Completable {
        return offlineRepository.insertDefects(models).subscribeOn(Schedulers.io())
    }

    private fun cleanDbAndFiles(): Completable {
        return offlineRepository.cleanDbAndFiles().subscribeOn(Schedulers.io())
    }
}

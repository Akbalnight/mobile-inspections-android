package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoursRepository
import ru.madbrains.domain.repository.OfflineRepository
import java.io.File
import java.util.*

class SyncInteractor(
    private val offlineRepository: OfflineRepository
) {
    fun setDirectories(fileTempDir: File?, fileSaveDir: File?) {
        offlineRepository.setDirectories(fileTempDir, fileSaveDir)
    }

    fun cleanDbAndFiles(): Completable {
        return offlineRepository.cleanDbAndFiles().subscribeOn(Schedulers.io())
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

    fun refreshDetoursDb(): Single<List<DetourModel>> {
        return offlineRepository.getDetours().subscribeOn(Schedulers.io())
    }

    fun checkAndRefreshDb(): Completable {
        return offlineRepository.checkAndRefreshDb().subscribeOn(Schedulers.io())
    }

    fun finishGetSync(date: Date) {
        offlineRepository.finishGetSync(date)
    }

    fun unzipFiles(zipFile: File, folder: AppDirType): Completable {
        return offlineRepository.unzipFiles(zipFile, folder).subscribeOn(Schedulers.io())
    }

    fun saveFileFromBody(
        response: Response<ResponseBody>,
        name: String,
        dirTypeRoot: RootDirType
    ): Single<File> {
        return offlineRepository.saveFileFromBody(response, name, dirTypeRoot)
            .subscribeOn(Schedulers.io())
    }

    fun saveEquipmentsDb(models: List<EquipmentModel>): Completable {
        return offlineRepository.saveEquipments(models).subscribeOn(Schedulers.io())
    }

    fun saveDetourDB(detour: DetourModel): Completable {
        return offlineRepository.insertDetour(detour)
            .subscribeOn(Schedulers.io())
    }

    fun saveDetoursDb(models: List<DetourModel>): Completable {
        return offlineRepository.insertDetours(models).subscribeOn(Schedulers.io())
    }

    fun saveDefectsDb(models: List<DefectModel>): Completable {
        return offlineRepository.insertDefects(models).subscribeOn(Schedulers.io())
    }

    fun saveDefectDb(model: DefectModel): Completable {
        return offlineRepository.insertDefect(model).subscribeOn(Schedulers.io())
    }

    fun saveDefectTypicalDb(models: List<DefectTypicalModel>): Completable {
        return offlineRepository.saveDefectsTypical(models).subscribeOn(Schedulers.io())
    }
}

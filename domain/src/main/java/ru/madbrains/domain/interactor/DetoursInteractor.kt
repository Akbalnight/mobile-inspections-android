package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoursRepository
import ru.madbrains.domain.repository.OfflineRepository
import java.io.File
import java.util.*

class DetoursInteractor(
    private val routesRepository: DetoursRepository,
    private val offlineRepository: OfflineRepository
) {
    fun getDetoursRemote(): Single<List<DetourModel>> {
        val models = mutableListOf<DetourModel>()
        return routesRepository.getDetoursStatuses().flatMap { statuses ->
            offlineRepository.saveDetourStatuses(statuses)
            routesRepository.getDetours(
                statuses = statuses.getStatusesByType(
                    arrayOf(
                        DetourStatusType.PAUSED,
                        DetourStatusType.NEW
                    )
                )
            ).flatMapCompletable { items ->
                models.addAll(items)
                val unfrozenIds = models.filter { it.frozen != true }.map { it.id }
                if (unfrozenIds.isNotEmpty()) {
                    routesRepository.freezeDetours(unfrozenIds)
                }
                else{
                    Completable.complete()
                }
            }.toSingle{
                models.filter { it.statusId != null }
                    .filter { it -> it.route.routesData?.all { it.techMap != null } == true }
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getCheckpointsRemote(): Single<List<CheckpointModel>> {
        return routesRepository.getCheckpoints()
            .subscribeOn(Schedulers.io())
    }

    fun updateCheckpointRemote(id: String, rfidCode: String): Single<Any> {
        return routesRepository.updateCheckpoint(id, rfidCode)
            .subscribeOn(Schedulers.io())
    }

    fun updateDetourRemote(detour: DetourModel): Completable {
        return routesRepository.updateDetour(detour)
            .subscribeOn(Schedulers.io())
    }

    fun getDefectTypicalRemote(): Single<List<DefectTypicalModel>> {
        return routesRepository.getDefectTypical()
            .subscribeOn(Schedulers.io())
    }

    fun getEquipmentsRemote(): Single<List<EquipmentModel>> {
        return routesRepository.getEquipments(emptyList(), emptyList())
            .subscribeOn(Schedulers.io())
    }

    fun getFileArchive(fileIds: List<String>): Single<Response<ResponseBody>> {
        return routesRepository.downloadFileArchive(fileIds)
            .subscribeOn(Schedulers.io())
    }

    fun getDefectsRemote(
        id: String? = null,
        codes: List<String>? = null,
        dateDetectStart: String? = null,
        dateDetectEnd: String? = null,
        detourIds: List<String>? = null,
        defectNames: List<String>? = null,
        equipmentNames: List<String>? = null,
        equipmentIds: List<String>? = null,
        statusProcessId: String? = null
    ): Single<List<DefectModel>> {
        return routesRepository.getDefects(
            id,
            codes,
            dateDetectStart,
            dateDetectEnd,
            detourIds,
            defectNames,
            equipmentNames,
            equipmentIds,
            statusProcessId
        )
            .subscribeOn(Schedulers.io())
    }

    fun saveDefectRemote(model: DefectModel): Single<String> {
        val files = model.files?.filter { it.isNew }?.mapNotNull { media -> getFileInFolder(media.fileName, AppDirType.Local) }
        return routesRepository.saveDefect(model, files).subscribeOn(Schedulers.io())
    }

    fun updateDefectRemote(model: DefectModel): Single<String> {
        val files = model.files?.filter { it.isNew }?.mapNotNull { media -> getFileInFolder(media.fileName, AppDirType.Local) }
        return routesRepository.updateDefect(model, files).subscribeOn(Schedulers.io())
    }

    //offline

    fun saveDetourDB(detour: DetourModel): Completable {
        return offlineRepository.insertDetour(detour)
            .subscribeOn(Schedulers.io())
    }

    fun getSyncInfoSource(): Observable<SyncInfo> {
        return offlineRepository.getSyncInfoSource()
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

    fun delDefectDb(id: String): Completable {
        return offlineRepository.deleteDefect(id).subscribeOn(Schedulers.io())
    }

    fun refreshDetoursDb(): Single<List<DetourModel>> {
        return offlineRepository.getDetours().subscribeOn(Schedulers.io())
    }

    fun getDetoursSourceDb(): Observable<List<DetourModel>> {
        return offlineRepository.getDetoursSource().subscribeOn(Schedulers.io())
    }

    fun getChangedDetoursDb(): Single<List<DetourModel>> {
        return offlineRepository.getChangedDetours().subscribeOn(Schedulers.io())
    }

    fun getChangedDefectsDb(): Single<List<DefectModel>> {
        return offlineRepository.getChangedDefects().subscribeOn(Schedulers.io())
    }

    fun getDefectsDb(): Single<List<DefectModel>> {
        return offlineRepository.getDefects().subscribeOn(Schedulers.io())
    }

    fun getActiveDefectsDb(equipmentIds: List<String>): Single<List<DefectModel>> {
        return offlineRepository.getActiveDefects(equipmentIds).subscribeOn(Schedulers.io())
    }

    fun getEquipmentIdsWithDefectsDB(equipmentIds: List<String>): Single<List<String>> {
        return offlineRepository.getEquipmentIdsWithDefects(equipmentIds).subscribeOn(Schedulers.io())
    }

    fun saveEquipmentsDb(models: List<EquipmentModel>): Completable {
        return offlineRepository.saveEquipments(models).subscribeOn(Schedulers.io())
    }

    fun getEquipmentsDb(): Single<List<EquipmentModel>> {
        return offlineRepository.getEquipments().subscribeOn(Schedulers.io())
    }

    fun saveDefectTypicalDb(models: List<DefectTypicalModel>): Completable {
        return offlineRepository.saveDefectsTypical(models).subscribeOn(Schedulers.io())
    }

    fun getDefectTypicalDb(): Single<List<DefectTypicalModel>> {
        return offlineRepository.getDefectsTypical()
            .subscribeOn(Schedulers.io())
    }

    fun saveFileFromBody(
        response: Response<ResponseBody>,
        name: String,
        dirTypeRoot: RootDirType
    ): Single<File> {
        return offlineRepository.saveFileFromBody(response, name, dirTypeRoot)
            .subscribeOn(Schedulers.io())
    }

    fun unzipFiles(zipFile: File, folder: AppDirType): Completable {
        return offlineRepository.unzipFiles(zipFile, folder).subscribeOn(Schedulers.io())
    }

    fun finishGetSync(date: Date) {
        offlineRepository.finishGetSync(date)
    }

    fun finishSendSync(date: Date) {
        offlineRepository.finishSendSync(date)
    }

    fun checkAndRefreshDb(): Completable {
        return offlineRepository.checkAndRefreshDb().subscribeOn(Schedulers.io())
    }

    fun cleanDbAndFiles(): Completable {
        return offlineRepository.cleanDbAndFiles().subscribeOn(Schedulers.io())
    }

    fun cleanEverything(): Completable {
        return offlineRepository.cleanEverything().subscribeOn(Schedulers.io())
    }

    fun deleteFile(file: File?): Completable {
        return offlineRepository.deleteFile(file).subscribeOn(Schedulers.io())
    }

    fun setDirectories(fileTempDir: File?, fileSaveDir: File?) {
        offlineRepository.setDirectories(fileTempDir, fileSaveDir)
    }

    fun getFileInFolder(name: String?, folder: AppDirType): File? {
        return offlineRepository.getFileInFolder(name, folder)
    }
}

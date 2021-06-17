package ru.madbrains.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import java.io.File
import java.util.*

interface OfflineRepository {
    companion object{
        const val ARCHIVE_DOCS = "doc-archive.zip"
        const val ARCHIVE_DEFECTS_MEDIA = "defects-media-archive.zip"
    }

    fun insertDetours(models: List<DetourModel>): Completable
    fun insertDetour(model: DetourModel): Completable
    fun getDetours(): Single<List<DetourModel>>
    fun getDetoursSource(): Observable<List<DetourModel>>
    fun getSyncInfoSource(): Observable<SyncInfo>
    fun insertDefects(models: List<DefectModel>): Completable
    fun getDefects(): Single<List<DefectModel>>
    fun getActiveDefects(equipmentIds: List<String>): Single<List<DefectModel>>
    fun getEquipmentIdsWithDefects(equipmentIds: List<String>): Single<List<String>>
    fun saveDetourStatuses(list: List<DetourStatus>)
    fun saveEquipments(models: List<EquipmentModel>): Completable
    fun getEquipments(): Single<List<EquipmentModel>>
    fun finishGetSync(date: Date)
    fun finishSendSync(date: Date)
    fun getDefectsTypical(): Single<List<DefectTypicalModel>>
    fun saveDefectsTypical(models: List<DefectTypicalModel>): Completable
    fun checkAndRefreshDb(): Completable
    fun cleanDbAndFiles(): Completable
    fun deleteFile(file: File?): Completable
    fun setDirectories(fileTempDir: File?, fileSaveDir: File?)
    fun saveFileFromBody(
        response: Response<ResponseBody>,
        name: String,
        dirTypeRoot: RootDirType
    ): Single<File>

    fun unzipFiles(zipFile: File, folder: AppDirType): Completable
    fun getFileInFolder(name: String?, folder: AppDirType): File?
    fun logoutClean(): Completable
    fun getChangedDetours(): Single<List<DetourModel>>
    fun insertDefect(model: DefectModel): Completable
    fun deleteDefect(id: String): Completable
    fun getChangedDefects(): Single<List<DefectModel>>
}
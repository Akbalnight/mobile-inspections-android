package ru.madbrains.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.data.database.HcbDatabase
import ru.madbrains.data.network.mappers.*
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.OfflineRepository
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

class OfflineRepositoryImpl(
    private val preferenceStorage: PreferenceStorage,
    private val db: HcbDatabase
) : OfflineRepository {
    private val _detoursSource = BehaviorSubject.create<List<DetourModel>>()
    private val _syncInfoSource =
        BehaviorSubject.createDefault<SyncInfo>(preferenceStorage.syncInfo)
    private val _syncedItemsFinish = BehaviorSubject.create<String>()

    override val detoursSource: Observable<List<DetourModel>>
        get() = _detoursSource
    override val syncInfoSource: Observable<SyncInfo>
        get() = _syncInfoSource
    override val syncedItemsFinish: Observable<String>
        get() = _syncedItemsFinish

    private var _tempDirectory: File? = null
    private var _saveDirectory: File? = null

    override fun signalFinishSyncingItem(id: String) {
        _syncedItemsFinish.onNext(id)
    }

    override fun insertDetours(models: List<DetourModel>): Completable {
        return db.detourItemDao().insertItem(models.map { toDetourItemDB(it) })
    }

    override fun insertDetour(model: DetourModel): Completable {
        return db.detourItemDao().insertItem(toDetourItemDB(model))
    }

    override fun getDetoursAndRefreshSource(): Single<List<DetourModel>> {
        return db.detourItemDao().getItems().map { it -> it.map { fromDetourItemDB(it) } }.map {
            _detoursSource.onNext(it)
            it
        }
    }

    override fun getChangedDetours(): Single<List<DetourModel>> {
        return db.detourItemDao().getChangedItems().map { it -> it.map { fromDetourItemDB(it) } }
    }

    override fun getChangedDefects(): Single<List<DefectModel>> {
        return db.defectItemDao().getChangedItems().map { it -> it.map { fromDefectItemDB(it) } }
    }

    override fun insertDefects(models: List<DefectModel>): Completable {
        return db.defectItemDao().insertItem(models.map { toDefectItemDB(it) })
    }

    override fun insertDefect(model: DefectModel): Completable {
        return db.defectItemDao().insertItem(toDefectItemDB(model))
    }

    override fun deleteDefect(id: String): Completable {
        return db.defectItemDao().del(id)
    }

    override fun insertDefectsTypical(models: List<DefectTypicalModel>): Completable {
        return db.defectTypicalDao().insertItem(models.map { toDefectTypicalDB(it) })
    }

    override fun insertCheckpoints(models: List<CheckpointModel>): Completable {
        return db.checkpointItemDao().insertItem(models.map { toCheckpointItemDB(it) })
    }

    override fun getDefects(): Single<List<DefectModel>> {
        return db.defectItemDao().getItems().map { it -> it.map { fromDefectItemDB(it) } }
    }

    override fun getActiveDefects(equipmentIds: List<String>): Single<List<DefectModel>> {
        return db.defectItemDao().getActiveItems(equipmentIds, DefectStatus.ELIMINATED.id)
            .map { it -> it.map { fromDefectItemDB(it) } }
    }

    override fun getEquipmentIdsWithDefects(equipmentIds: List<String>): Single<List<String>> {
        return db.defectItemDao()
            .getEquipmentIdsWithDefects(equipmentIds, DefectStatus.ELIMINATED.id)
    }

    override fun getEquipments(): Single<List<EquipmentModel>> {
        return db.equipmentItemDao().getItems().map { it -> it.map { fromEquipmentItemDB(it) } }
    }

    override fun getDefectsTypical(): Single<List<DefectTypicalModel>> {
        return db.defectTypicalDao().getItems().map { it -> it.map { fromDefectTypicalDB(it) } }
    }

    override fun finishGetSync(date: Date) {
        setSyncInfoAndRefreshSource(preferenceStorage.syncInfo.copy(getDate = date))
    }

    override fun finishSendSync(date: Date) {
        setSyncInfoAndRefreshSource(preferenceStorage.syncInfo.copy(sendDate = date))
    }

    private fun setSyncInfoAndRefreshSource(syncInfo: SyncInfo) {
        preferenceStorage.syncInfo = syncInfo
        _syncInfoSource.onNext(syncInfo)
    }

    override fun insertEquipments(models: List<EquipmentModel>): Completable {
        return db.equipmentItemDao().insertItem(models.map { toEquipmentItemDB(it) })
    }

    override fun insertDetourStatuses(list: List<DetourStatus>) {
        preferenceStorage.detourStatuses = DetourStatusHolder(list)
    }

    override fun checkIfNeedsCleaningAndRefreshDetours(): Completable {
        preferenceStorage.syncInfo.getDate?.let {
            val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(Date().time - it.time)
            if (diffInDays > preferenceStorage.saveInfoDuration) {
                return cleanDbAndFiles()
            }
        }
        return getDetoursAndRefreshSource().flatMapCompletable {
            Completable.complete()
        }
    }

    override fun logoutClean(): Completable {
        return cleanDbAndFiles().doFinally {
            preferenceStorage.clearLogout()
        }
    }

    override fun cleanDbAndFiles(): Completable {
        return Completable.merge(
            arrayListOf(
                db.equipmentItemDao().clean(),
                db.defectItemDao().clean(),
                db.defectTypicalDao().clean(),
                db.detourItemDao().clean(),
                db.checkpointItemDao().clean(),
                deleteFile(File(_saveDirectory, AppDirType.Defects.value)),
                deleteFile(File(_saveDirectory, AppDirType.Docs.value))
            )
        )
    }

    override fun deleteFile(file: File?): Completable {
        return Completable.fromCallable {
            file?.let {
                if (it.exists()) {
                    deleteRecursive(it)
                }
            }
        }
    }

    override fun setDirectories(fileTempDir: File?, fileSaveDir: File?) {
        _tempDirectory = fileTempDir
        _saveDirectory = fileSaveDir?.apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    override fun saveFileFromBody(
        response: Response<ResponseBody>,
        name: String,
        dirTypeRoot: RootDirType
    ): Single<File> {
        val saveDirectory = when (dirTypeRoot) {
            RootDirType.Temp -> _tempDirectory
            RootDirType.Save -> _saveDirectory
        }
        return Single.fromCallable {
            val body = response.body()
            if (body != null && response.isSuccessful && saveDirectory != null) {
                val file = File(saveDirectory, name)
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                val inputStream: InputStream = body.byteStream()
                val outputStream = FileOutputStream(file)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.apply {
                    flush()
                    close()
                }

                inputStream.close()
                file
            } else {
                throw Exception("Unable to save")
            }
        }
    }

    override fun unzipFiles(zipFile: File, folder: AppDirType): Completable {
        val saveDirectory = File(_saveDirectory, folder.value)
        return Completable.fromCallable {
            ZipInputStream(
                BufferedInputStream(FileInputStream(zipFile))
            ).use { zipStream ->
                while (true) {
                    val ze = zipStream.nextEntry ?: break
                    val file = File(saveDirectory, ze.name)
                    val dir = if (ze.isDirectory) file else file.parentFile
                    if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                        "Failed to ensure directory: " + dir.absolutePath
                    )
                    if (file.isDirectory) continue
                    FileOutputStream(file).use { fileStream ->
                        var count: Int
                        val buffer = ByteArray(8192)
                        while (zipStream.read(buffer).also { count = it } != -1) fileStream.write(
                            buffer,
                            0,
                            count
                        )
                    }
                }
            }
        }
    }

    override fun getFileInFolder(name: String?, folder: AppDirType): File? {
        if (name != null) {
            val directory = File(_saveDirectory, folder.value)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            return File(directory, name)
        }
        return null
    }


    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.let { list ->
                for (child in list) {
                    child.delete()
                    deleteRecursive(child)
                }
            }
        }
        fileOrDirectory.delete()
    }
}
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
import timber.log.Timber
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

class OfflineRepositoryImpl(
    private val preferenceStorage: PreferenceStorage,
    private val db: HcbDatabase
) : OfflineRepository {
    private val detourDbSource = BehaviorSubject.create<List<DetourModel>>()
    private val syncInfoSource = BehaviorSubject.createDefault<SyncInfo>(preferenceStorage.syncInfo)

    private var _tempDirectory: File? = null
    private var _saveDirectory: File? = null

    override fun insertDetours(models: List<DetourModel>): Completable {
        return db.detourItemDao().insertItem(models.map { toDetourItemDB(it) })
    }

    override fun insertDetour(model: DetourModel): Completable {
        return db.detourItemDao().insertItem(toDetourItemDB(model))
    }

    override fun getDetours(): Single<List<DetourModel>> {
        return db.detourItemDao().getItems().map { it -> it.map { fromDetourItemDB(it) } }.map {
            detourDbSource.onNext(it)
            it
        }
    }

    override fun getChangedDetours(): Single<List<DetourModel>> {
        return db.detourItemDao().getChangedItems().map { it -> it.map { fromDetourItemDB(it) } }
    }

    override fun getChangedDefects(): Single<List<DefectModel>> {
        return db.defectItemDao().getChangedItems().map { it -> it.map { fromDefectItemDB(it) } }
    }

    override fun getDetoursSource(): Observable<List<DetourModel>> {
        return detourDbSource
    }

    override fun getSyncInfoSource(): Observable<SyncInfo> {
        return syncInfoSource
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

    override fun saveDefectsTypical(models: List<DefectTypicalModel>): Completable {
        return db.defectTypicalDao().insertItem(models.map { toDefectTypicalDB(it) })
    }

    override fun getDefects(equipmentIds: List<String>?, limit: Int): Single<List<DefectModel>> {
        val single = if (equipmentIds != null) {
            db.defectItemDao().getItemsByEquipment(equipmentIds, limit)
        } else {
            db.defectItemDao().getItems(limit)
        }
        return single.map { it -> it.map { fromDefectItemDB(it) } }
    }

    override fun getEquipments(): Single<List<EquipmentModel>> {
        return db.equipmentItemDao().getItems().map { it -> it.map { fromEquipmentItemDB(it) } }
    }

    override fun getDefectsTypical(): Single<List<DefectTypicalModel>> {
        return db.defectTypicalDao().getItems().map { it -> it.map { fromDefectTypicalDB(it) } }
    }

    override fun finishGetSync(date: Date) {
        setSyncInfo(preferenceStorage.syncInfo.copy(getDate = date))
    }

    override fun finishSendSync(date: Date) {
        setSyncInfo(preferenceStorage.syncInfo.copy(sendDate = date))
    }

    private fun setSyncInfo(syncInfo: SyncInfo) {
        preferenceStorage.syncInfo = syncInfo
        syncInfoSource.onNext(syncInfo)
    }

    override fun saveEquipments(models: List<EquipmentModel>): Completable {
        return db.equipmentItemDao().insertItem(models.map { toEquipmentItemDB(it) })
    }

    override fun saveDetourStatuses(list: List<DetourStatus>) {
        preferenceStorage.detourStatuses = DetourStatusHolder(list)
    }

    override fun checkAndRefreshDb(): Completable {
        preferenceStorage.syncInfo.getDate?.let {
            val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(Date().time - it.time)
            Timber.d("debug_dmm diffInDays: $diffInDays")
            if (diffInDays > preferenceStorage.saveInfoDuration) {
                return cleanEverything()
            }
        }
        return getDetours().flatMapCompletable {
            Completable.complete()
        }
    }

    override fun cleanEverything(): Completable {
        setSyncInfo(SyncInfo())
        return cleanDbAndFiles()
    }

    override fun cleanDbAndFiles(): Completable {
        return Completable.merge(
            arrayListOf(
                db.equipmentItemDao().clean(),
                db.defectItemDao().clean(),
                db.defectTypicalDao().clean(),
                db.detourItemDao().clean(),
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
            if(!exists()){
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
                    Timber.d("debug_dmm ze.name: ${ze.name}")
                    val file = File(saveDirectory, ze.name)
                    val dir = if (ze.isDirectory) file else file.parentFile
                    if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                        "Failed to ensure directory: " + dir.absolutePath
                    )
                    if (ze.isDirectory) continue
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
            if(!directory.exists()){
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
        Timber.d("debug_dmm deleted $fileOrDirectory")
    }
}
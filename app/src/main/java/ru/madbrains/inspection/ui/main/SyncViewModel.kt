package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.RemoteInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.OfflineRepository.Companion.ARCHIVE_DEFECTS_MEDIA
import ru.madbrains.domain.repository.OfflineRepository.Companion.ARCHIVE_DOCS
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.ProgressState
import ru.madbrains.inspection.ui.delegates.DetourUiModel
import java.io.File
import java.util.*

class SyncViewModel(
    private val remoteInteractor: RemoteInteractor,
    private val syncInteractor: SyncInteractor,
    private val offlineInteractor: OfflineInteractor,
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _syncInfo = MutableLiveData<SyncInfo>(preferenceStorage.syncInfo)
    val syncInfo: LiveData<SyncInfo> = _syncInfo

    private val _detourSyncStatus = MutableLiveData<Event<ProgressState>>()
    val detourSyncStatus: LiveData<Event<ProgressState>> = _detourSyncStatus

    private val _defectsSyncStatus = MutableLiveData<Event<ProgressState>>()
    val defectsSyncStatus: LiveData<Event<ProgressState>> = _defectsSyncStatus

    private val _mediaSyncStatus = MutableLiveData<Event<ProgressState>>()
    val mediaSyncStatus: LiveData<Event<ProgressState>> = _mediaSyncStatus

    private val _docSyncStatus = MutableLiveData<Event<ProgressState>>()
    val docSyncStatus: LiveData<Event<ProgressState>> = _docSyncStatus

    private val _etcSyncStatus = MutableLiveData<Event<ProgressState>>()
    val etcSyncStatus: LiveData<Event<ProgressState>> = _etcSyncStatus

    private val _allSyncProgress = MutableLiveData<Event<ProgressState>>()
    val allSyncProgress: LiveData<Event<ProgressState>> = _allSyncProgress

    private val _openSyncDialog = MutableLiveData<Event<Unit>>()
    val openSyncDialog: LiveData<Event<Unit>> = _openSyncDialog

    private val _globalProgress = MutableLiveData<Boolean>()
    val globalProgress: LiveData<Boolean> = _globalProgress

    private val _changedItems = MutableLiveData<List<DetourUiModel>>()
    val changedItems: LiveData<List<DetourUiModel>> = _changedItems

    private var _pendingDataDb: PendingDataDb? = null

    private val _showSnackBar = MutableLiveData<Event<Int>>()
    val showSnackBar: LiveData<Event<Int>> = _showSnackBar

    private val observables = CompositeDisposable()

    init {
        offlineInteractor.syncInfoSource
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _syncInfo.postValue(it)
            }, {
                it.printStackTrace()
            }).addTo(observables)

        remoteInteractor.syncedItemsFinish
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _changedItems.postValue(_changedItems.value?.filter { it.id != id })
            }, {
                it.printStackTrace()
            })
            .addTo(observables)

        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
    }

    fun initAction(fileTempDir: File?, fileSaveDir: File?) {
        syncInteractor.setDirectories(fileTempDir, fileSaveDir)
        syncInteractor.checkIfNeedsCleaningAndRefreshDetours()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _globalProgress.postValue(true) }
            .doAfterTerminate { _globalProgress.postValue(false) }
            .subscribe({ }, {
                it.printStackTrace()
                _showSnackBar.postValue(Event(R.string.error))
            })
            .addTo(disposables)
    }

    override fun onCleared() {
        observables.dispose()
        super.onCleared()
    }

    fun startSync() {
        _detourSyncStatus.postValue(Event(ProgressState.PROGRESS))
        _openSyncDialog.value = Event(Unit)
        doSync()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _allSyncProgress.postValue(Event(ProgressState.PROGRESS)) }
            .subscribe({
                _allSyncProgress.postValue(Event(ProgressState.DONE))
                _pendingDataDb = it
            }, {
                _allSyncProgress.postValue(Event(ProgressState.FAILED))
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun doSync(): Single<PendingDataDb> {
        return Single.zip(
            doSyncMain(),
            doSyncEtc(), BiFunction { b1: PendingDataDb, b2: EtcAsyncResponse ->
                b1.equipment = b2.equipment
                b1.defectsTypical = b2.defectsTypical
                b1
            })
    }

    private fun doSyncMain(): Single<PendingDataDb> {
        val pending = PendingDataDb()
        return getDetourRemote()
            .flatMap { routes ->
                pending.routes = routes
                Single.zip(
                    getDefectsRemote(routes).flatMap {
                        pending.defects = it
                        getDefectsMediaFilesArchive(it)
                    },
                    getDocFilesArchive(routes),
                    BiFunction { b1: FileEnvelope, b2: FileEnvelope -> Pair(b1, b2) }
                )
            }
            .map { pair ->
                pending.mediaArchive = pair.first.file
                pending.docArchive = pair.second.file
                pending
            }
    }

    private fun doSyncEtc(): Single<EtcAsyncResponse> {
        return Single.zip(
            remoteInteractor.getEquipments(),
            remoteInteractor.getDefectTypical(),
            remoteInteractor.getCheckpoints(),
            Function3 { b1: List<EquipmentModel>,
                        b2: List<DefectTypicalModel>,
                        b3: List<CheckpointModel>
                ->
                EtcAsyncResponse(b1, b2, b3)
            })
            .doOnSubscribe { _etcSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _etcSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _etcSyncStatus.postValue(Event(ProgressState.DONE)) }
    }


    private fun getDetourRemote(): Single<List<DetourModel>> {
        return remoteInteractor.getDetours()
            .doOnSubscribe { _detourSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _detourSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _detourSyncStatus.postValue(Event(ProgressState.DONE)) }
    }

    private fun getDefectsRemote(routes: List<DetourModel>): Single<List<DefectModel>> {
        return remoteInteractor.getDefects(
            detourIds = routes.map { it.id }
        )
            .doOnSubscribe { _defectsSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _defectsSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _defectsSyncStatus.postValue(Event(ProgressState.DONE)) }
    }

    private fun getDocFilesArchive(routes: List<DetourModel>?): Single<FileEnvelope> {
        val ids = routes?.getAllFilesIds()
        val single: Single<FileEnvelope> = if (ids?.isNotEmpty() == true) {
            remoteInteractor.getFileArchive(
                fileIds = ids
            ).flatMap { it ->
                syncInteractor.saveFileFromBody(it, ARCHIVE_DOCS, RootDirType.Temp).map {
                    FileEnvelope(
                        it
                    )
                }
            }
        } else {
            Single.just(FileEnvelope(null))
        }
        return single
            .doOnSubscribe { _docSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _docSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _docSyncStatus.postValue(Event(ProgressState.DONE)) }
    }

    private fun getDefectsMediaFilesArchive(defects: List<DefectModel>?): Single<FileEnvelope> {
        val ids = defects?.getAllFilesIds()
        val single: Single<FileEnvelope> = if (ids?.isNotEmpty() == true) {
            remoteInteractor.getFileArchive(
                fileIds = ids
            ).flatMap { it ->
                syncInteractor.saveFileFromBody(
                    it,
                    ARCHIVE_DEFECTS_MEDIA,
                    RootDirType.Temp
                ).map {
                    FileEnvelope(
                        it
                    )
                }
            }
        } else {
            Single.just(FileEnvelope(null))
        }
        return single
            .doOnSubscribe { _mediaSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _mediaSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _mediaSyncStatus.postValue(Event(ProgressState.DONE)) }
    }

    fun cancelSync() {
        disposables.clear()

        Completable.merge(
            arrayListOf(
                syncInteractor.deleteFile(_pendingDataDb?.docArchive),
                syncInteractor.deleteFile(_pendingDataDb?.mediaArchive)
            )
        )
            .doOnSubscribe { _globalProgress.postValue(true) }
            .doAfterTerminate { _globalProgress.postValue(false) }
            .subscribe({
                _pendingDataDb = null
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun applySyncToDb() {
        _pendingDataDb?.let { data ->
            val observables = arrayListOf<Completable>()
            data.routes?.let {
                observables.add(syncInteractor.insertDetours(it))
            }
            data.defects?.let {
                observables.add(syncInteractor.insertDefects(it))
            }
            data.equipment?.let {
                observables.add(syncInteractor.insertEquipments(it))
            }
            data.defectsTypical?.let {
                observables.add(syncInteractor.insertDefectTypical(it))
            }
            data.checkpoints?.let {
                observables.add(syncInteractor.insertCheckpoints(it))
            }
            data.docArchive?.let {
                observables.add(syncInteractor.unzipFiles(it, AppDirType.Docs))
            }
            data.mediaArchive?.let {
                observables.add(syncInteractor.unzipFiles(it, AppDirType.Defects))
            }
            syncInteractor.cleanDbAndFiles()
                .andThen(Completable.merge(observables))
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(offlineInteractor.getDetoursAndRefreshSource())
                .doOnSubscribe { _globalProgress.postValue(true) }
                .doAfterTerminate { _globalProgress.postValue(false) }
                .subscribe({
                    syncInteractor.finishGetSync(Date())
                    getChangedDetoursAndDefects()
                }, {
                    _showSnackBar.postValue(Event(R.string.error))
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

    fun getChangedDetoursAndDefects() {
        offlineInteractor.getChangedDetoursAndDefects().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _globalProgress.postValue(true) }
            .doAfterTerminate { _globalProgress.postValue(false) }
            .subscribe({ pair ->
                val detours = pair.first.map { detour ->
                    DetourUiModel(
                        id = detour.id,
                        name = detour.name.orEmpty(),
                        status = preferenceStorage.detourStatuses?.data?.getStatusById(detour.statusId),
                        date = detour.dateStartPlan
                    )
                }
                val defects = pair.second.map { defect ->
                    DetourUiModel(
                        id = defect.id,
                        name = defect.defectName.orEmpty(),
                        status = null,
                        date = null
                    )
                }
                _changedItems.postValue(detours + defects)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }


    fun startSendingData() {
        remoteInteractor.sendSyncDataAndRefreshDb()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _globalProgress.postValue(true) }
            .doAfterTerminate { _globalProgress.postValue(false) }
            .subscribe({}, {
                _showSnackBar.postValue(Event(R.string.fragment_sync_send_data_error))
            })
            .addTo(disposables)
    }
}

data class PendingDataDb(
    var routes: List<DetourModel>? = null,
    var defects: List<DefectModel>? = null,
    var equipment: List<EquipmentModel>? = null,
    var defectsTypical: List<DefectTypicalModel>? = null,
    var checkpoints: List<CheckpointModel>? = null,
    var mediaArchive: File? = null,
    var docArchive: File? = null
)

data class EtcAsyncResponse(
    var equipment: List<EquipmentModel>,
    var defectsTypical: List<DefectTypicalModel>,
    var checkpoints: List<CheckpointModel>
)

data class FileEnvelope(val file: File?)

package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.extensions.toyyyyMMddTHHmmssXXX
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.DetoursInteractor
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
    private val detoursInteractor: DetoursInteractor,
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _syncInfo = MutableLiveData<SyncInfo>(preferenceStorage.syncInfo)
    val syncInfo: LiveData<SyncInfo> = _syncInfo

    private val _sendDataAvailable = MutableLiveData<Boolean>()
    val sendDataAvailable: LiveData<Boolean> = _sendDataAvailable

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
    private var _changedDetourItems: List<DetourModel>? = null
    private var _changedDefectsItems: List<DefectModel>? = null

    private val _showSnackBar = MutableLiveData<Event<Int>>()
    val showSnackBar: LiveData<Event<Int>> = _showSnackBar

    private val observables = CompositeDisposable()

    init {
        detoursInteractor.getSyncInfoSource()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _syncInfo.postValue(it)
            }, {
                it.printStackTrace()
            })
            .addTo(observables)

        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
    }

    fun initAction(fileTempDir: File?, fileSaveDir: File?) {
        detoursInteractor.setDirectories(fileTempDir, fileSaveDir)

        detoursInteractor.checkAndRefreshDb()
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
            detoursInteractor.getEquipmentsRemote(),
            detoursInteractor.getDefectTypicalRemote(),
            BiFunction { b1: List<EquipmentModel>,
                         b2: List<DefectTypicalModel> ->
                EtcAsyncResponse(b1, b2)
            })
            .doOnSubscribe { _etcSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _etcSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _etcSyncStatus.postValue(Event(ProgressState.DONE)) }
    }


    private fun getDetourRemote(): Single<List<DetourModel>> {
        return detoursInteractor.getDetoursRemote()
            .doOnSubscribe { _detourSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _detourSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _detourSyncStatus.postValue(Event(ProgressState.DONE)) }
    }

    private fun getDefectsRemote(routes: List<DetourModel>): Single<List<DefectModel>> {
        return detoursInteractor.getDefectsRemote(
            detourIds = routes.map { it.id }
        )
            .doOnSubscribe { _defectsSyncStatus.postValue(Event(ProgressState.PROGRESS)) }
            .doOnError { _defectsSyncStatus.postValue(Event(ProgressState.FAILED)) }
            .doOnSuccess { _defectsSyncStatus.postValue(Event(ProgressState.DONE)) }
    }

    private fun getDocFilesArchive(routes: List<DetourModel>?): Single<FileEnvelope> {
        val ids = routes?.getAllFilesIds()
        val single: Single<FileEnvelope> = if (ids?.isNotEmpty() == true) {
            detoursInteractor.getFileArchive(
                fileIds = ids
            ).flatMap { it ->
                detoursInteractor.saveFileFromBody(it, ARCHIVE_DOCS, RootDirType.Temp).map {
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
            detoursInteractor.getFileArchive(
                fileIds = ids
            ).flatMap { it ->
                detoursInteractor.saveFileFromBody(
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
                detoursInteractor.deleteFile(_pendingDataDb?.docArchive),
                detoursInteractor.deleteFile(_pendingDataDb?.mediaArchive)
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
                observables.add(detoursInteractor.saveDetoursToDb(it))
            }
            data.defects?.let {
                observables.add(detoursInteractor.saveDefectsToDb(it))
            }
            data.equipment?.let {
                observables.add(detoursInteractor.saveEquipmentsDb(it))
            }
            data.defectsTypical?.let {
                observables.add(detoursInteractor.saveDefectTypicalDb(it))
            }
            data.docArchive?.let {
                observables.add(detoursInteractor.unzipFiles(it, AppDirType.Docs))
            }
            data.mediaArchive?.let {
                observables.add(detoursInteractor.unzipFiles(it, AppDirType.Defects))
            }
            detoursInteractor.cleanDbAndFiles()
                .andThen(Completable.merge(observables))
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(detoursInteractor.refreshDetoursDb())
                .doOnSubscribe { _globalProgress.postValue(true) }
                .doAfterTerminate { _globalProgress.postValue(false) }
                .subscribe({
                    detoursInteractor.finishGetSync(Date())
                    getChangedDetours()
                }, {
                    _showSnackBar.postValue(Event(R.string.error))
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

    fun getChangedDetours(){
        Single.zip(detoursInteractor.getChangedDetoursDb(), detoursInteractor.getChangedDefectsDb(),
            BiFunction { b1: List<DetourModel>, b2: List<DefectModel> ->
            Pair(b1, b2)
        })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _globalProgress.postValue(true) }
            .doAfterTerminate { _globalProgress.postValue(false) }
            .subscribe({ pair->
                _changedDetourItems = pair.first
                _changedDefectsItems = pair.second
                _sendDataAvailable.postValue((pair.first + pair.second).isNotEmpty())
                val detours = pair.first.map { detour ->
                    DetourUiModel(
                        id = detour.id,
                        name = detour.name.orEmpty(),
                        status = preferenceStorage.detourStatuses?.data?.getStatusById(detour.statusId),
                        date = detour.dateStartPlan.orEmpty()
                    )
                }
                val defects = pair.second.map { defect ->
                    DetourUiModel(
                        id = defect.id,
                        name = defect.defectName.orEmpty(),
                        status = null,
                        date = defect.dateDetectDefect?.toyyyyMMddTHHmmssXXX().orEmpty()
                    )
                }
                _changedItems.postValue(detours + defects)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun startSendingData() {
        val tasks = arrayListOf<Completable>()
        _changedDetourItems?.let { list->
            if(list.isNotEmpty()){
                val detourTasks = list.map { item->
                    detoursInteractor.updateDetourRemote(item).andThen(
                        detoursInteractor.updateDetourDB(
                            item.apply { changed = false }
                        ).doFinally {
                            _changedItems.postValue(_changedItems.value?.filter {
                                it.id != item.id
                            })
                        }
                    )
                }
                tasks.addAll(detourTasks)
            }
        }
        _changedDefectsItems?.let { list->
            if(list.isNotEmpty()){
                val defectsTasks = list.map { item->
                    val single = if(item.changed) detoursInteractor.updateDefectRemote(item) else detoursInteractor.saveDefectRemote(item)
                    single.flatMapCompletable { Completable.complete() }
                    .andThen(
                        detoursInteractor.saveDefectToDb(
                            item.apply { changed = false }
                        ).doFinally {
                            _changedItems.postValue(_changedItems.value?.filter {
                                it.id != item.id
                            })
                        }
                    )
                }
                tasks.addAll(defectsTasks)
            }
        }
        Completable.merge(tasks)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _globalProgress.postValue(true) }
            .doAfterTerminate { _globalProgress.postValue(false) }
            .subscribe ({
                _sendDataAvailable.postValue(false)
                detoursInteractor.finishSendSync(Date())
            },{
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
    var mediaArchive: File? = null,
    var docArchive: File? = null
)

data class EtcAsyncResponse(
    var equipment: List<EquipmentModel>,
    var defectsTypical: List<DefectTypicalModel>
)

data class FileEnvelope(val file: File?)

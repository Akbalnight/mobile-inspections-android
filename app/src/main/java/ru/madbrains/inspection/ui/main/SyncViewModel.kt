package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.RemoteInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.domain.model.SyncInfo
import ru.madbrains.domain.model.WrapEtcSync
import ru.madbrains.domain.model.WrapFile
import ru.madbrains.domain.model.WrapPendingDataSync
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.ProgressState
import ru.madbrains.inspection.extensions.changeProgressWith
import ru.madbrains.inspection.extensions.changeProgressWithB
import ru.madbrains.inspection.ui.delegates.ChangedItemUiCheckpoint
import ru.madbrains.inspection.ui.delegates.ChangedItemUiDefect
import ru.madbrains.inspection.ui.delegates.ChangedItemUiDetour
import ru.madbrains.inspection.ui.delegates.ChangedItemUiModel
import java.io.File

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

    private val _changedItems = MutableLiveData<List<ChangedItemUiModel>>()
    val changedItems: LiveData<List<ChangedItemUiModel>> = _changedItems

    private var _pendingDataSync: WrapPendingDataSync? = null

    private val _showSnackBar = MutableLiveData<Event<Int>>()
    val showSnackBar: LiveData<Event<Int>> = _showSnackBar

    private val observables = CompositeDisposable()

    init {
        offlineInteractor.syncInfoSource
            .observeOn(Schedulers.io())
            .subscribe({
                _syncInfo.postValue(it)
            }, {
                it.printStackTrace()
            }).addTo(observables)

        remoteInteractor.syncedItemsFinish
            .observeOn(Schedulers.io())
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
            .observeOn(Schedulers.io())
            .changeProgressWith(_globalProgress)
            .subscribe({ }, {
                it.printStackTrace()
                _showSnackBar.postValue(Event(R.string.error))
            })
            .addTo(disposables)
    }

    fun startSync() {
        _detourSyncStatus.postValue(Event(ProgressState.PROGRESS))
        _openSyncDialog.postValue(Event(Unit))
        getSyncData().changeProgressWith(_allSyncProgress)
            .observeOn(Schedulers.io())
            .subscribe({
                _pendingDataSync = it
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun getSyncData(): Single<WrapPendingDataSync> {
        return Single.zip(
            getDefectsAndMediaFiles(),
            remoteInteractor.getSyncEtcData().changeProgressWith(_etcSyncStatus),
            BiFunction { b1: WrapPendingDataSync, b2: WrapEtcSync ->
                b1.equipment = b2.equipment
                b1.defectsTypical = b2.defectsTypical
                b1.checkpoints = b2.checkpoints
                b1
            })
    }

    private fun getDefectsAndMediaFiles(): Single<WrapPendingDataSync> {
        val wrap = WrapPendingDataSync()
        return remoteInteractor.getDetours().changeProgressWith(_detourSyncStatus)
            .flatMap { routes ->
                wrap.routes = routes
                Single.zip(
                    remoteInteractor.getDefects(detourIds = routes.map { it.id })
                        .changeProgressWith(_defectsSyncStatus)
                        .flatMap {
                            wrap.defects = it
                            remoteInteractor.downloadDefectsMediaFilesArchive(it)
                                .changeProgressWith(_mediaSyncStatus)
                        },
                    remoteInteractor.downloadDocFilesArchive(routes)
                        .changeProgressWith(_docSyncStatus),
                    BiFunction { b1: WrapFile, b2: WrapFile ->
                        wrap.mediaArchive = b1.file
                        wrap.docArchive = b2.file
                        wrap
                    }
                )
            }
    }

    fun cancelSync() {
        disposables.clear()
        Completable.merge(
            arrayListOf(
                syncInteractor.deleteFile(_pendingDataSync?.docArchive),
                syncInteractor.deleteFile(_pendingDataSync?.mediaArchive)
            )
        )
            .changeProgressWith(_globalProgress)
            .subscribe({
                _pendingDataSync = null
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun applySyncToDb() {
        _pendingDataSync?.let { data ->
            syncInteractor.syncPendingDataAndRefresh(data)
                .observeOn(Schedulers.io())
                .changeProgressWith(_globalProgress)
                .subscribe({
                    getChangedDetoursAndDefects()
                }, {
                    _showSnackBar.postValue(Event(R.string.error))
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

    fun getChangedDetoursAndDefects() {
        offlineInteractor.getChangedDataForSync()
            .observeOn(Schedulers.io()).changeProgressWithB(_globalProgress)
            .subscribe({ wrap ->
                val detours = wrap.detours.map { detour ->
                    ChangedItemUiDetour(
                        id = detour.id,
                        name = detour.name.orEmpty(),
                        status = preferenceStorage.detourStatuses.getStatusById(detour.statusId),
                        dateStartPlan = detour.dateStartPlan
                    )
                }
                val defects = wrap.defects.map { defect ->
                    ChangedItemUiDefect(
                        id = defect.id,
                        name = defect.defectName.orEmpty(),
                        dateDetectDefect = defect.dateDetectDefect
                    )
                }
                val checkpoints = wrap.checkpoints.map { checkpoint ->
                    ChangedItemUiCheckpoint(
                        id = checkpoint.id,
                        name = checkpoint.name,
                        rfidCode = checkpoint.rfidCode
                    )
                }
                _changedItems.postValue(detours + defects + checkpoints)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun startSendingData() {
        remoteInteractor.sendSyncDataAndRefreshDb()
            .observeOn(Schedulers.io())
            .changeProgressWith(_globalProgress)
            .subscribe({}, {
                _showSnackBar.postValue(Event(R.string.fragment_sync_send_data_error))
            })
            .addTo(disposables)
    }

    override fun onCleared() {
        observables.dispose()
        super.onCleared()
    }
}
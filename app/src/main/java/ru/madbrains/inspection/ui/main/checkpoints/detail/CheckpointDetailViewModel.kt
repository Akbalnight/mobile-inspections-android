package ru.madbrains.inspection.ui.main.checkpoints.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.RfidInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class CheckpointDetailViewModel(
        private val syncInteractor: SyncInteractor,
        private val rfidInteractor: RfidInteractor,
        private val offlineInteractor: OfflineInteractor
) : BaseViewModel() {

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    private var _checkpointRawData: CheckpointModel? = null

    //Models LiveData
    private val _rfidProgress = MutableLiveData<Boolean>()
    val rfidProgress: LiveData<Boolean> = _rfidProgress

    private val _descriptionObserver = MutableLiveData<String>()
    val descriptionObserver: LiveData<String> = _descriptionObserver

    //Events
    private val _navigateToCamera = MutableLiveData<Event<Unit>>()
    val navigateToCamera: LiveData<Event<Unit>> = _navigateToCamera

    private val _popAndRefresh = MutableLiveData<Event<Unit>>()
    val popAndRefresh: LiveData<Event<Unit>> = _popAndRefresh

    private val _showDialogConfirmChangedFields = MutableLiveData<Event<Unit>>()
    val showDialogConfirmChangedFields: LiveData<Event<Unit>> = _showDialogConfirmChangedFields

    private val _showDialogChangedFields = MutableLiveData<Event<Unit>>()
    val showDialogChangedFields: LiveData<Event<Unit>> = _showDialogChangedFields

    private val _showDialogDuplicateRfidCode = MutableLiveData<Event<String>>()
    val showDialogDuplicateRfidCode: LiveData<Event<String>> = _showDialogDuplicateRfidCode

    private val _popNavigation = MutableLiveData<Event<Unit>>()
    val popNavigation: LiveData<Event<Unit>> = _popNavigation

    private val _showSnackBar = MutableLiveData<Event<Int>>()
    val showSnackBar: LiveData<Event<Int>> = _showSnackBar

    private val _showError = MutableLiveData<Event<Throwable>>()
    val showError: LiveData<Event<Throwable>> = _showError

    private val _rfidDataReceiver = MutableLiveData<Event<String>>()
    val rfidDataReceiver: LiveData<Event<String>> = _rfidDataReceiver

    private var _descriptionText: String? = null
    private var _rfidCode: String? = null

    fun setRawData(data: CheckpointModel?) {
        _checkpointRawData = data
        _rfidDataReceiver.postValue(Event(data?.rfidCode ?: "-"))
        _rfidCode = data?.rfidCode
    }

    fun changeDescription(text: CharSequence?) {
        _descriptionText = text?.toString()
        _isChanged.postValue(true)
    }

    fun sendUpdate() {
        _checkpointRawData?.let { model ->
            _rfidCode?.let { rfid ->
                syncInteractor.insertCheckpoint(model.copy(rfidCode = rfid, changed = true))
                        .observeOn(Schedulers.io())
                        .doOnSubscribe { _rfidProgress.postValue(true) }
                        .doAfterTerminate { _rfidProgress.postValue(false) }
                        .subscribe({
                            _showSnackBar.postValue(Event(R.string.fragment_checkpoint_detail_saved_success))
                            _popAndRefresh.postValue(Event(Unit))
                        }, {
                            it.printStackTrace()
                            _showError.postValue(Event(it))
                        })
                        .addTo(disposables)
            }

        }
    }

    fun checkAndSave() {
        if (_isChanged.value == true) {
            _showDialogConfirmChangedFields.postValue(Event(Unit))
        }
    }

    fun checkPopBack() {
        if (_isChanged.value == true) {
            _showDialogChangedFields.postValue(Event(Unit))
        } else {
            _popNavigation.postValue(Event(Unit))
        }
    }

    fun startRfidScan() {
        rfidInteractor.startScan({
            _rfidProgress.postValue(it)
        }) {
            offlineInteractor.getCheckpointWithRfidCode(it)
                    .observeOn(Schedulers.io())
                    .subscribe({ listWithRfidScan ->
                        if (listWithRfidScan.isEmpty()) {
                            _rfidDataReceiver.postValue(Event(it))
                            _rfidCode = it
                            _isChanged.postValue(true)
                        } else {
                            _showDialogDuplicateRfidCode.postValue(Event(listWithRfidScan.first().name))
                        }
                    }, { throwableListWithRfidCode ->
                        throwableListWithRfidCode.printStackTrace()
                        _showError.postValue(Event(throwableListWithRfidCode))
                    })
                    .addTo(disposables)
        }
    }

    fun stopRfidScan() {
        rfidInteractor.stopScan()
    }

}

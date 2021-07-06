package ru.madbrains.inspection.ui.main.checkpoints.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.domain.interactor.RemoteInteractor
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.MediaUiModel

class CheckpointDetailViewModel(
    private val remoteInteractor: RemoteInteractor,
    private val rfidDevice: RfidDevice
) : BaseViewModel() {

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    private var _checkpointRawData: CheckpointModel? = null
    private val mediaModels = mutableListOf<MediaUiModel>()

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
        _rfidDataReceiver.value = Event(data?.rfidCode ?: "-")
        _rfidCode = data?.rfidCode
    }

    fun changeDescription(text: CharSequence?) {
        _descriptionText = text?.toString()
        _isChanged.value = true
    }

    fun sendUpdate() {
        _checkpointRawData?.let { model ->
            _rfidCode?.let { rfid ->
                remoteInteractor.updateCheckpoint(model.id, rfid)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _rfidProgress.postValue(true) }
                    .doAfterTerminate { _rfidProgress.postValue(false) }
                    .subscribe({
                        _showSnackBar.value =
                            Event(R.string.fragment_checkpoint_detail_saved_success)
                        _popAndRefresh.value = Event(Unit)
                    }, {
                        it.printStackTrace()
                        _showError.value = Event(it)
                    })
                    .addTo(disposables)
            }

        }
    }

    fun checkAndSave() {
        if (_isChanged.value == true) {
            _showDialogConfirmChangedFields.value = Event(Unit)
        }
    }

    fun checkPopBack() {
        if (_isChanged.value == true) {
            _showDialogChangedFields.value = Event(Unit)
        } else {
            _popNavigation.value = Event(Unit)
        }
    }

    fun startRfidScan() {
        rfidDevice.startScan({
            _rfidProgress.value = it
        }) {
            _rfidDataReceiver.value = Event(it)
            _rfidCode = it
            _isChanged.value = true
        }
    }

    fun stopRfidScan() {
        rfidDevice.stopScan()
    }

}

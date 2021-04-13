package ru.madbrains.inspection.ui.main.checkpoints.detail

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MediaUiModel
import java.io.File
import java.util.*

class CheckpointDetailViewModel(
    private val routesInteractor: RoutesInteractor,
    private val fileUtil: FileUtil,
    private val rfidDevice: RfidDevice
    ) : BaseViewModel() {


    private var isChanged: Boolean = false

    private var _checkpointRawData: CheckpointModel? = null
    private val mediaModels = mutableListOf<MediaUiModel>()

    //Models LiveData
    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _descriptionObserver = MutableLiveData<String>()
    val descriptionObserver: LiveData<String> = _descriptionObserver

    private val _mediaList = MutableLiveData<List<DiffItem>>()
    val mediaList: LiveData<List<DiffItem>> = _mediaList

    //Events
    private val _navigateToCamera = MutableLiveData<Event<Unit>>()
    val navigateToCamera: LiveData<Event<Unit>> = _navigateToCamera

    private val _showDialogConfirmChangedFields = MutableLiveData<Event<Unit>>()
    val showDialogConfirmChangedFields: LiveData<Event<Unit>> = _showDialogConfirmChangedFields

    private val _showDialogChangedFields = MutableLiveData<Event<Unit>>()
    val showDialogChangedFields: LiveData<Event<Unit>> = _showDialogChangedFields

    private val _popNavigation = MutableLiveData<Event<Unit>>()
    val popNavigation: LiveData<Event<Unit>> = _popNavigation

    private val _showSnackBar = MutableLiveData<Event<Int>>()
    val showSnackBar: LiveData<Event<Int>> = _showSnackBar

    private val _rfidDataReceiver = MutableLiveData<Event<String>>()
    val rfidDataReceiver: LiveData<Event<String>> = _rfidDataReceiver

    private var _descriptionText: String? = null
    private var _rfidId: String? = null

    fun setRawData(data: CheckpointModel?) {
        _checkpointRawData = data
        _rfidDataReceiver.value = Event(data?.rfidCode?:"-")
        _rfidId = data?.rfidCode
    }

    fun changeDescription(text: CharSequence?) {
        _descriptionText = text?.toString()
        isChanged = true
    }

    private fun updateMediaList() {
        val items = mutableListOf<MediaUiModel>().apply {
            mediaModels.map { item ->
                add(item)
            }
        }
        _mediaList.value = items
    }

    fun addImage(image: Bitmap) {
        mediaModels.add(
                MediaUiModel(
                        id = UUID.randomUUID().toString(),
                        imageBitmap = image,
                        isEditing = true,
                        isNetworkImage = false
                )
        )
        isChanged = true
        updateMediaList()
    }

    fun deleteMedia(deleteItem: MediaUiModel) {
        if (mediaModels.remove(deleteItem)) {
            isChanged = true
            updateMediaList()
        }
    }

    fun photoVideoClick() {
        if (mediaModels.size < 8)
            _navigateToCamera.value = Event(Unit)
    }

    fun sendUpdate() {

    }

    private fun getFilesToSend(): List<File> {
        return mediaModels.filter {
            it.isEditing && (it.imageBitmap != null)
        }.map { media ->
            fileUtil.createFile(media.imageBitmap!!, media.id)
        }
    }


    fun checkAndSave() {
        if (isChanged) {
            _showDialogConfirmChangedFields.value = Event(Unit)
        } else {
            sendUpdate()
        }
    }

    fun checkPopBack() {
        if (isChanged) {
            _showDialogChangedFields.value = Event(Unit)
        } else {
            _popNavigation.value = Event(Unit)
        }
    }

    fun startRfidScan(){
        _progressVisibility.value = true
        rfidDevice.startScan({
            _progressVisibility.value = it
        }) {
            _rfidDataReceiver.value = Event(it)
            _rfidId = it
            isChanged = true
        }
    }

    fun stopRfidScan(){
        rfidDevice.stopScan()
    }

}

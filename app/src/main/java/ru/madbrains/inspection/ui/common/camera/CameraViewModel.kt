package ru.madbrains.inspection.ui.common.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import java.io.File

class CameraViewModel(
    private val detoursInteractor: DetoursInteractor
) : BaseViewModel() {

    private val _cameraState = MutableLiveData<Event<CameraState>>()
    val cameraState: LiveData<Event<CameraState>> = _cameraState

    private val _capturedImage = MutableLiveData<Event<Bitmap>>()
    val capturedImage: LiveData<Event<Bitmap>> = _capturedImage

    private val _capturedVideo = MutableLiveData<Event<File>>()
    val capturedVideo: LiveData<Event<File>> = _capturedVideo

    private val _startRecording = MutableLiveData<Event<File?>>()
    val startRecording: LiveData<Event<File?>> = _startRecording

    fun setImage(bitmap: Bitmap) {
        _capturedImage.postValue(Event(bitmap))
    }

    fun setVideo(file: File) {
        _capturedVideo.postValue(Event(file))
    }

    fun changeCameraState(state: CameraState) {
        _cameraState.postValue(Event(state))
    }

    fun startRecord(name: String) {
        _startRecording.postValue(
            Event(detoursInteractor.getFileInFolder(name, AppDirType.Local))
        )
    }

    enum class CameraState {
        PHOTO,
        VIDEO
    }
}
package ru.madbrains.inspection.ui.common.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import java.io.File

class CameraViewModel : BaseViewModel() {

    private val _cameraState = MutableLiveData<Event<CameraState>>()
    val cameraState: LiveData<Event<CameraState>> = _cameraState

    private val _capturedImage = MutableLiveData<Event<Bitmap>>()
    val capturedImage: LiveData<Event<Bitmap>> = _capturedImage

    private val _capturedVideo = MutableLiveData<Event<File>>()
    val capturedVideo: LiveData<Event<File>> = _capturedVideo

    fun setImage(bitmap: Bitmap) {
        _capturedImage.postValue(Event(bitmap))
    }

    fun setVideo(file: File) {
        _capturedVideo.postValue(Event(file))
    }

    fun changeCameraState(state: CameraState) {
        _cameraState.postValue(Event(state))
    }

    enum class CameraState {
        PHOTO,
        VIDEO
    }
}
package ru.madbrains.inspection.ui.common.camera

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import timber.log.Timber
import java.io.*

class CameraViewModel(
    private val offlineInteractor: OfflineInteractor
) : BaseViewModel() {

    private val _cameraState = MutableLiveData<Event<CameraState>>()
    val cameraState: LiveData<Event<CameraState>> = _cameraState

    private val _resolvedFile = MutableLiveData<Event<File>>()
    val resolvedFile: LiveData<Event<File>> = _resolvedFile

    private val _startRecording = MutableLiveData<Event<File>>()
    val startRecording: LiveData<Event<File>> = _startRecording

    private val _startCapture = MutableLiveData<Event<File>>()
    val startCapture: LiveData<Event<File>> = _startCapture

    private val _toGallery = MutableLiveData<Event<Unit>>()
    val toGallery: LiveData<Event<Unit>> = _toGallery

    private val _popNav = MutableLiveData<Event<Unit>>()
    val popNav: LiveData<Event<Unit>> = _popNav

    fun startCapture() {
        offlineInteractor.getFileInFolder(
            "${System.currentTimeMillis()}.jpg",
            AppDirType.Local
        )?.let{
            _startCapture.postValue(Event(it))
        }


    }

    fun postFile(file: File) {
        _resolvedFile.postValue(Event(file))
        _popNav.postValue(Event(Unit))
    }

    fun changeCameraState(state: CameraState) {
        _cameraState.postValue(Event(state))
    }

    fun galleryClick() {
        _toGallery.postValue(Event(Unit))
    }

    fun startRecord() {
        offlineInteractor.getFileInFolder(
            "${System.currentTimeMillis()}.mp4",
            AppDirType.Local
        )?.let {
            _startRecording.postValue(Event(it))
        }
    }

    fun getDataFromGallery(uri: Uri, contentResolver: ContentResolver) {
        try {
            val type = contentResolver.getType(uri)
            val ext =  MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
            offlineInteractor.getFileInFolder(
                "${System.currentTimeMillis()}.$ext",
                AppDirType.Local
            )?.let{ file->
                contentResolver.openInputStream(uri)?.let { inputStream->
                    val fileOutputStream = FileOutputStream(file)
                    copyStream(inputStream, fileOutputStream)
                    fileOutputStream.close()
                    inputStream.close()
                    postFile(file)
                }
            }
        } catch (e: Throwable) {
            Timber.d("debug_dmm e: $e")
        }
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        do {
            val bytesRead = input.read(buffer)
            if(bytesRead != -1){
                output.write(buffer, 0, bytesRead)
            }
        } while (bytesRead != -1)
    }

    enum class CameraState {
        PHOTO,
        VIDEO
    }
}
package ru.madbrains.inspection.ui.common.camera

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CameraViewModel(
    private val detoursInteractor: DetoursInteractor,
    private val fileUtil: FileUtil
) : BaseViewModel() {

    private val _cameraState = MutableLiveData<Event<CameraState>>()
    val cameraState: LiveData<Event<CameraState>> = _cameraState

    private val _resolvedFile = MutableLiveData<Event<File>>()
    val resolvedFile: LiveData<Event<File>> = _resolvedFile

    private val _startRecording = MutableLiveData<Event<File?>>()
    val startRecording: LiveData<Event<File?>> = _startRecording

    private val _toGallery = MutableLiveData<Event<Unit>>()
    val toGallery: LiveData<Event<Unit>> = _toGallery

    fun setImage(bitmap: Bitmap) {
       fileUtil.createJpgFile(
            bitmap, detoursInteractor.getFileInFolder(
                "${UUID.randomUUID()}.jpg",
                AppDirType.Local
            )
        )?.let{
           postFile(it)
       }
    }

    fun postFile(file: File) {
        _resolvedFile.postValue(Event(file))
    }

    fun changeCameraState(state: CameraState) {
        _cameraState.postValue(Event(state))
    }

    fun galleryClick() {
        _toGallery.postValue(Event(Unit))
    }

    fun startRecord(name: String) {
        _startRecording.postValue(
            Event(detoursInteractor.getFileInFolder(name, AppDirType.Local))
        )
    }

    fun getDataFromGallery(dataData: Uri, dir: File, contentResolver: ContentResolver) {
        try {
            createImageFile(dir)?.let{ file->
                contentResolver.openInputStream(dataData)?.let { inputStream->
                    val fileOutputStream = FileOutputStream(file)
                    copyStream(inputStream, fileOutputStream)
                    fileOutputStream.close()
                    inputStream.close()
                    postFile(file)
                }
            }

        } catch (e: Throwable) {

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(storageDir: File): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile(
            timeStamp + "_",
            null,
            storageDir
        )
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        do {
            val bytesRead = input.read(buffer)
            output.write(buffer, 0, bytesRead)
        } while (bytesRead != -1)
    }

    enum class CameraState {
        PHOTO,
        VIDEO
    }
}
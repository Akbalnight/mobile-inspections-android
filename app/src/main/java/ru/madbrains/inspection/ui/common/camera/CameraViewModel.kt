package ru.madbrains.inspection.ui.common.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class CameraViewModel : BaseViewModel() {

    private val _capturedImage = MutableLiveData<Event<Bitmap>>()
    val capturedImage: LiveData<Event<Bitmap>> = _capturedImage

    fun setImage(bitmap: Bitmap) {
        _capturedImage.value = Event(bitmap)
    }
}
package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.graphics.Bitmap
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectTypicalModel
import ru.madbrains.domain.model.EquipmentsModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*

class DefectDetailViewModel(private val routesInteractor: RoutesInteractor,
                            private val fileUtil: FileUtil) :
        BaseViewModel() {

    private val defectTypicalModels = mutableListOf<DefectTypicalModel>()
    private val mediaModels = mutableListOf<MediaDefectUiModel>()
    private var currentTypical: DefectTypicalUiModel? = null
    private var description: String = ""

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _defectTypicalList = MutableLiveData<List<DefectTypicalUiModel>>()
    val defectTypicalList: LiveData<List<DefectTypicalUiModel>> = _defectTypicalList

    private val _device = MutableLiveData<EquipmentsModel?>()
    val device: LiveData<EquipmentsModel?> = _device

    private val _mediaList = MutableLiveData<List<DiffItem>>()
    val mediaList: LiveData<List<DiffItem>> = _mediaList

    //Events
    private val _navigateToCamera = MutableLiveData<Event<Unit>>()
    val navigateToCamera: LiveData<Event<Unit>> = _navigateToCamera

    private val _showDialogBlankFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankFields: LiveData<Event<Unit>> = _showDialogBlankFields

    private val _showDialogBlankRequiredFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankRequiredFields: LiveData<Event<Unit>> = _showDialogBlankRequiredFields

    private val _popNavigation = MutableLiveData<Event<Unit>>()
    val popNavigation: LiveData<Event<Unit>> = _popNavigation


    fun getDefectTypicalList() {
        routesInteractor.getDefectTypical()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    defectTypicalModels.addAll(items)
                    updateDefectTypicalList()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
    }

    fun changeCurrentDefectTypical(model: DefectTypicalUiModel) {
        currentTypical = model
    }


    fun changeCurrentDefectDevice(model: EquipmentsModel) {
        _device.value = model
    }

    fun clearData() {
        defectTypicalModels.clear()
        mediaModels.clear()
        currentTypical?.let { currentTypical = null }
        description = ""
        _defectTypicalList.value = emptyList()
        _device.value = null
        _mediaList.value = emptyList()
    }

    private fun checkIsNotEmptyFields(): Boolean {

        var isNotEmpty = true

        isNotEmpty = isNotEmpty && (currentTypical != null)

        isNotEmpty = isNotEmpty && (description.isNotBlank())

        isNotEmpty = isNotEmpty && (!mediaList.value.isNullOrEmpty())

        return isNotEmpty
    }

    private fun checkIsNoEmptyRequiredFields(): Boolean {

        var isNotEmpty = true
        device.value?.let {
            isNotEmpty = isNotEmpty && true
        } ?: run {
            isNotEmpty = isNotEmpty && false
        }
        return isNotEmpty
    }

    fun addDescription(text: CharSequence?) {
        text?.let {
            description = it.toString()
        } ?: run {
            description = ""
        }
    }

    private fun updateDefectTypicalList() {
        val items = mutableListOf<DefectTypicalUiModel>().apply {
            defectTypicalModels.map { item ->
                add(
                        DefectTypicalUiModel(
                                id = item.id,
                                name = item.name.orEmpty(),
                                code = item.code
                        )
                )
            }
        }
        _defectTypicalList.value = items
    }

    fun addImage(image: Bitmap) {
        mediaModels.add(
                MediaDefectUiModel(
                        id = UUID.randomUUID().toString(),
                        image = image,
                        isVideo = false
                )
        )
        updateMediaList()
    }

    fun deleteMedia(deleteItem: MediaDefectUiModel) {
        if (mediaModels.remove(deleteItem)) {
            updateMediaList()
        }
    }

    private fun updateMediaList() {
        val items = mutableListOf<MediaDefectUiModel>().apply {
            mediaModels.map { item ->
                add(item)
            }
        }
        _mediaList.value = items
    }

    fun photoVideoClick() {
        _navigateToCamera.value = Event(Unit)
    }

    fun saveDefect() {
        //todo save defect
        _popNavigation.value = Event(Unit)
    }

    fun checkAndSave() {

        val listFiles = mediaModels.map {
            fileUtil.createFile(it.image, it.id)
        }

        routesInteractor.saveDefect(detoursId = "a0af3d69-f68a-4e29-bc9b-37b19f35423c",
                equipmentId = "54211ba6-6f65-4c57-83ce-71ec9f8ff567",
                staffDetectId = "1f627c88-8f43-4105-a679-3a693559debc",
                defectTypicalId = "8ea718c9-b5ef-4f67-b87f-bab4f70c0b61",
                description = "Описание дефекта №1 test 999",
                dateDetectDefect = "2020-12-01T00:00:00+03:00",
                files = listFiles
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({ items ->
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)

        /*   if (checkIsNoEmptyRequiredFields()){
               if(checkIsNotEmptyFields()){
                   saveDefect()
               } else {
                   _showDialogBlankFields.value = Event(Unit)
               }
           } else {
               _showDialogBlankRequiredFields.value = Event(Unit)
           }

         */

    }


}

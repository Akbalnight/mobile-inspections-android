package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.graphics.Bitmap
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectTypicalModel
import ru.madbrains.domain.model.EquipmentsModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel
import java.util.*

class DefectDetailViewModel(private val routesInteractor: RoutesInteractor) :
        BaseViewModel() {

    private val defectTypicalModels = mutableListOf<DefectTypicalModel>()
    private val mediaModels = mutableListOf<MediaDefectUiModel>()
    private lateinit var currentTypical: DefectTypicalUiModel
    private var description: String = ""

    private val _defectTypicalList = MutableLiveData<List<DefectTypicalUiModel>>()
    val defectTypicalList: LiveData<List<DefectTypicalUiModel>> = _defectTypicalList

    private val _device = MutableLiveData<EquipmentsModel>()
    val device: LiveData<EquipmentsModel> = _device

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

    private fun checkIsNotEmptyFields(): Boolean {

        var isNotEmpty = true

        isNotEmpty = isNotEmpty && (this::currentTypical.isInitialized)

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

        if (checkIsNoEmptyRequiredFields()){
            if(checkIsNotEmptyFields()){
                saveDefect()
            } else {
                _showDialogBlankFields.value = Event(Unit)
            }
        } else {
            _showDialogBlankRequiredFields.value = Event(Unit)
        }

    }
}

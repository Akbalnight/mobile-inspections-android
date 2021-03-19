package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectTypicalModel
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel
import java.text.SimpleDateFormat
import java.util.*

class DefectDetailViewModel(private val routesInteractor: RoutesInteractor,
                            private val fileUtil: FileUtil) :
        BaseViewModel() {

    private val defectTypicalModels = mutableListOf<DefectTypicalModel>()
    private val mediaModels = mutableListOf<MediaDefectUiModel>()
    private var currentTypical: DefectTypicalUiModel? = null
    private var description: String = ""


    private var detourId: String? = null



    //Models Input Data
    private var currentTypicalDefect: DefectTypicalModel? = null

    var equipmentList = listOf<EquipmentModel>()

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _defectTypicalList = MutableLiveData<List<DefectTypicalUiModel>>()
    val defectTypicalList: LiveData<List<DefectTypicalUiModel>> = _defectTypicalList

    private val _device = MutableLiveData<EquipmentModel?>()
    val device: LiveData<EquipmentModel?> = _device

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

    fun setDetourId(id:String){
        detourId = id
    }

    fun setEquipments(equipments: List<EquipmentModel>){
        equipmentList = equipments
    }

    fun changeCurrentDefectTypical(model: DefectTypicalUiModel) {
        currentTypical = model
    }


    fun changeCurrentDefectDevice(model: EquipmentModel) {
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
        detourId = null
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
                        imageBitmap = image,
                        url = "https://s1.1zoom.ru/big3/984/Canada_Parks_Lake_Mountains_Forests_Scenery_Rocky_567540_3840x2400.jpg"
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

    @SuppressLint("SimpleDateFormat")
    fun saveDefect() {
        //todo save defect
        val listFiles = mediaModels.map {
        //    fileUtil.createFile(it.image, it.id)
        }

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val timeStamp = format.format(Date())
        routesInteractor.saveDefect(detoursId = null,
                equipmentId = _device.value?.id,
                defectTypicalId = currentTypical?.id,
                description = description,
                dateDetectDefect = timeStamp
                //files = listFiles
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({ items ->
                    _popNavigation.value = Event(Unit)
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
    }

    fun checkAndSave() {
        if (checkIsNoEmptyRequiredFields()) {
            if (checkIsNotEmptyFields()) {
                saveDefect()
            } else {
                _showDialogBlankFields.value = Event(Unit)
            }
        } else {
            _showDialogBlankRequiredFields.value = Event(Unit)
        }

    }


}

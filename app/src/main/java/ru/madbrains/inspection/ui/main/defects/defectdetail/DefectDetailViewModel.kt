package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.network.ApiData
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectModel
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

    private var descriptionDefect: String = ""
    private var defect: DefectModel? = null

    //Add new defect
    var currentDeviceModel: EquipmentModel? = null
    var equipmentModelList: List<EquipmentModel>? = null
    private val defectTypicalModels = mutableListOf<DefectTypicalModel>()
    private val mediaModels = mutableListOf<MediaDefectUiModel>()
    private var currentTypical: DefectTypicalUiModel? = null
    private var detourId: String? = null

    //Models Input Data

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _defectTypicalList = MutableLiveData<List<DefectTypicalUiModel>>()
    val defectTypicalList: LiveData<List<DefectTypicalUiModel>> = _defectTypicalList

    private val _deviceName = MutableLiveData<String?>()
    val deviceName: LiveData<String?> = _deviceName

    private val _mediaList = MutableLiveData<List<DiffItem>>()
    val mediaList: LiveData<List<DiffItem>> = _mediaList
/*
    private val _mediaList = MutableLiveData<List<DiffItem>>()
    val mediaList: LiveData<List<DiffItem>> = _mediaList*/

    //Events
    private val _navigateToCamera = MutableLiveData<Event<Unit>>()
    val navigateToCamera: LiveData<Event<Unit>> = _navigateToCamera

    private val _showDialogBlankFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankFields: LiveData<Event<Unit>> = _showDialogBlankFields

    private val _showDialogBlankRequiredFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankRequiredFields: LiveData<Event<Unit>> = _showDialogBlankRequiredFields

    private val _popNavigation = MutableLiveData<Event<Unit>>()
    val popNavigation: LiveData<Event<Unit>> = _popNavigation

    private val _disableEquipmentField = MutableLiveData<Event<Unit>>()
    val disableEquipmentField: LiveData<Event<Unit>> = _disableEquipmentField

    private val _disableTypicalDefectField = MutableLiveData<Event<String>>()
    val disableTypicalDefectField: LiveData<Event<String>> = _disableTypicalDefectField

    private val _descriptionObserver = MutableLiveData<String>()
    val descriptionObserver: LiveData<String> = _descriptionObserver


    fun getDefectTypicalList() {
        if (defectTypicalModels.isNullOrEmpty()) {
            routesInteractor.getDefectTypical()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ items ->
                        defectTypicalModels.clear()
                        defectTypicalModels.addAll(items)
                        updateDefectTypicalList()
                    }, {
                        it.printStackTrace()
                    })
                    .addTo(disposables)
        }
    }

    fun setDetourId(id: String?) {
        detourId = id
    }

    fun setDefect(item: DefectModel?) {
        if (defect == null) {
            defect = item
            updateDefect()
        }
    }

    private fun updateDefect() {
        defect?.let { defect ->
            defect.apply {
                equipmentName?.let {
                    _deviceName.value = it
                    _disableEquipmentField.value = Event(Unit)
                }
                defectName?.let {
                    _disableTypicalDefectField.value = Event(it)
                }
                description?.let {
                    descriptionDefect = it
                    _descriptionObserver.value = it
                }
                files?.let {
                    it.map { fileModel ->
                        when (fileModel.extension) {
                            "png" -> { // если в файле изображение добавляем в список
                                mediaModels.add(MediaDefectUiModel(
                                        id = fileModel.id.orEmpty(),
                                        isEditing = false,
                                        url = ApiData.apiUrl + fileModel.url.orEmpty()
                                        //todo isImage если видео
                                        //todo image если видео
                                ))
                            }
                            "mpeg" -> {
                                //todo preview video
                            }
                            else -> {
                                //todo delete when extension all files
                                mediaModels.add(MediaDefectUiModel(
                                        id = fileModel.id.orEmpty(),
                                        isEditing = false,
                                        url = ApiData.apiUrl + fileModel.url.orEmpty()
                                        //todo isImage если видео
                                        //todo image если видео
                                ))
                            }
                        }
                    }
                }
                updateMediaList()
            }

        }
    }

    fun setEquipments(equipments: List<EquipmentModel>?) {
        equipmentModelList = equipments
        if (equipmentModelList?.size == 1) {
            changeCurrentDefectDevice(equipmentModelList!!.get(index = 0))
            _disableEquipmentField.value = Event(Unit)
        }
    }

    fun changeCurrentDefectTypical(model: DefectTypicalUiModel) {
        currentTypical = model
    }

    fun changeCurrentDefectDevice(model: EquipmentModel) {
        currentDeviceModel = model
        _deviceName.value = model.name
    }

    private fun checkIsNotEmptyFields(): Boolean {
        var isNotEmpty = true
        isNotEmpty = isNotEmpty && (currentTypical != null)
        isNotEmpty = isNotEmpty && (descriptionDefect.isNotBlank())
        isNotEmpty = isNotEmpty && (!mediaList.value.isNullOrEmpty())
        return isNotEmpty
    }

    private fun checkIsNoEmptyRequiredFields(): Boolean {
        var isNotEmpty = true
        _deviceName.value?.let {
            isNotEmpty = isNotEmpty && true
        } ?: run {
            isNotEmpty = isNotEmpty && false
        }
        return isNotEmpty
    }

    fun addDescription(text: CharSequence?) {
        text?.let {
            descriptionDefect = it.toString()
        } ?: run {
            descriptionDefect = ""
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
                        isEditing = true,
                        isNetworkImage = false
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
        if (mediaModels.size < 9)
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
                equipmentId = currentDeviceModel?.id,
                defectTypicalId = currentTypical?.id,
                description = descriptionDefect,
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

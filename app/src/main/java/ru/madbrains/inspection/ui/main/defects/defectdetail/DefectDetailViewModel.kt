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
import ru.madbrains.domain.model.DefectStatus
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

    private var descriptionDefect: String? = null

    private var defect: DefectModel? = null

    private var targetDefectStatus: DefectStatus? = null

    private var isChangedDefect: Boolean = false

    //Add new defect
    private var currentDeviceModel: EquipmentModel? = null
    private var currentTypical: DefectTypicalUiModel? = null
    private var detourId: String? = null

    private val defectTypicalModels = mutableListOf<DefectTypicalModel>()
    var equipmentModelList: List<EquipmentModel>? = null
    private val mediaModels = mutableListOf<MediaDefectUiModel>()

    //Models LiveData
    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _defectTypicalList = MutableLiveData<List<DefectTypicalUiModel>>()
    val defectTypicalList: LiveData<List<DefectTypicalUiModel>> = _defectTypicalList

    private val _deviceName = MutableLiveData<String?>()
    val deviceName: LiveData<String?> = _deviceName

    private val _descriptionObserver = MutableLiveData<String>()
    val descriptionObserver: LiveData<String> = _descriptionObserver

    private val _mediaList = MutableLiveData<List<DiffItem>>()
    val mediaList: LiveData<List<DiffItem>> = _mediaList

    //Events
    private val _navigateToCamera = MutableLiveData<Event<Unit>>()
    val navigateToCamera: LiveData<Event<Unit>> = _navigateToCamera

    private val _showDialogBlankFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankFields: LiveData<Event<Unit>> = _showDialogBlankFields

    private val _showDialogBlankRequiredFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankRequiredFields: LiveData<Event<Unit>> = _showDialogBlankRequiredFields

    private val _showDialogChangedFields = MutableLiveData<Event<Unit>>()
    val showDialogChangedFields: LiveData<Event<Unit>> = _showDialogChangedFields

    private val _popNavigation = MutableLiveData<Event<Unit>>()
    val popNavigation: LiveData<Event<Unit>> = _popNavigation

    private val _disableEquipmentField = MutableLiveData<Event<Unit>>()
    val disableEquipmentField: LiveData<Event<Unit>> = _disableEquipmentField

    private val _disableTypicalDefectField = MutableLiveData<Event<String>>()
    val disableTypicalDefectField: LiveData<Event<String>> = _disableTypicalDefectField

    fun getCurrentDevice(): EquipmentModel? {
        return currentDeviceModel
    }

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

    fun setDefectStatus(status: DefectStatus?) {
        targetDefectStatus = status
    }



    fun setEquipments(equipments: List<EquipmentModel>?) {
        equipmentModelList = equipments
        if (equipmentModelList?.size == 1) {
            currentDeviceModel = equipmentModelList!!.get(index = 0)
            _deviceName.value = equipmentModelList!!.get(index = 0).name
            _disableEquipmentField.value = Event(Unit)
        }
    }

    fun changeCurrentDefectTypical(model: DefectTypicalUiModel) {
        currentTypical = model
        isChangedDefect = true
    }

    fun changeCurrentDefectDevice(model: EquipmentModel) {
        currentDeviceModel = model
        _deviceName.value = model.name
        isChangedDefect = true
    }

    fun changeDescription(text: CharSequence?) {
        text?.let {
            descriptionDefect = it.toString()
        } ?: run {
            descriptionDefect = null
        }
        isChangedDefect = true
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
                    if (descriptionDefect != null) {
                        descriptionDefect = it
                        _descriptionObserver.value = it
                    }
                }
                files?.let {
                    it.map { fileModel ->
                        when (fileModel.extension) {
                            "jpg" -> { // если в файле изображение добавляем в список
                                mediaModels.add(MediaDefectUiModel(
                                        id = fileModel.id.orEmpty(),
                                        isEditing = !fileModel.shipped,
                                        url = ApiData.apiUrl + fileModel.url.orEmpty()
                                ))
                            }
                            "mpeg" -> {
                                //todo preview video
                            }
                            else -> {
                            }
                        }
                    }
                }
                updateMediaList()
            }

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

    fun addImage(image: Bitmap) {
        mediaModels.add(
                MediaDefectUiModel(
                        id = UUID.randomUUID().toString(),
                        imageBitmap = image,
                        isEditing = true,
                        isNetworkImage = false
                )
        )
        isChangedDefect = true
        updateMediaList()
    }

    fun deleteMedia(deleteItem: MediaDefectUiModel) {
        if (mediaModels.remove(deleteItem)) {
            isChangedDefect = true
            updateMediaList()
        }
    }

    fun photoVideoClick() {
        if (mediaModels.size < 9)
            _navigateToCamera.value = Event(Unit)
    }

    @SuppressLint("SimpleDateFormat")
    fun saveDefect() {
        val listFiles = mediaModels.filter {
            it.isEditing && (it.imageBitmap != null)
        }.map { media ->
            fileUtil.createFile(media.imageBitmap!!, media.id)
        }
        val timeStamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date())
        routesInteractor.saveDefect(detoursId = null,
                equipmentId = currentDeviceModel?.id,
                defectTypicalId = currentTypical?.id,
                description = descriptionDefect.orEmpty(),
                dateDetectDefect = timeStamp,
                files = listFiles
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

    private fun checkIsNotEmptyFields(): Boolean {
        var isNotEmpty = true
        isNotEmpty = isNotEmpty && (currentTypical != null)
        isNotEmpty = isNotEmpty && (!descriptionDefect.isNullOrEmpty())
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

    fun checkPopBack() {
        if(isChangedDefect) {
            _showDialogChangedFields.value = Event(Unit)
        } else {
            _popNavigation.value = Event(Unit)
        }

    }

}

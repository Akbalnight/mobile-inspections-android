package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.*
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MediaUiModel
import java.io.File
import java.util.*

class DefectDetailViewModel(
    private val detoursInteractor: DetoursInteractor,
    private val fileUtil: FileUtil
) :
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
    private val mediaModels = mutableListOf<MediaUiModel>()

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

    private val _showDialogBlankFields = MutableLiveData<Event<Boolean>>()
    val showDialogBlankFields: LiveData<Event<Boolean>> = _showDialogBlankFields

    private val _showDialogSaveNoLinkedDetour = MutableLiveData<Event<Unit>>()
    val showDialogSaveNoLinkedDetour: LiveData<Event<Unit>> = _showDialogSaveNoLinkedDetour

    private val _showDialogBlankRequiredFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankRequiredFields: LiveData<Event<Unit>> = _showDialogBlankRequiredFields

    private val _showDialogConfirmChangedFields = MutableLiveData<Event<Boolean>>()
    val showDialogConfirmChangedFields: LiveData<Event<Boolean>> = _showDialogConfirmChangedFields

    private val _showDialogChangedFields = MutableLiveData<Event<Unit>>()
    val showDialogChangedFields: LiveData<Event<Unit>> = _showDialogChangedFields

    private val _popNavigation = MutableLiveData<Event<Unit>>()
    val popNavigation: LiveData<Event<Unit>> = _popNavigation

    private val _disableEquipmentField = MutableLiveData<Event<Unit>>()
    val disableEquipmentField: LiveData<Event<Unit>> = _disableEquipmentField

    private val _disableTypicalDefectField = MutableLiveData<Event<String>>()
    val disableTypicalDefectField: LiveData<Event<String>> = _disableTypicalDefectField

    private val _showSnackBar = MutableLiveData<Event<Unit>>()
    val showSnackBar: LiveData<Event<Unit>> = _showSnackBar

    fun getCurrentDevice(): EquipmentModel? {
        return currentDeviceModel
    }

    fun getDefectTypicalList() {
        if (defectTypicalModels.isNullOrEmpty()) {
            detoursInteractor.getDefectTypicalDb()
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

        defect?.let { defectModel ->
            if (defectModel.description != descriptionDefect) {
                isChangedDefect = true
            }
        } ?: run {
            isChangedDefect = true
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
                    if (descriptionDefect == null) {
                        descriptionDefect = it
                        _descriptionObserver.value = it
                    }
                }
                files?.let {
                    it.map { fileModel ->
                        mediaModels.add(
                            MediaUiModel(
                                id = fileModel.id,
                                file = detoursInteractor.getFileInFolder(
                                    fileModel.fileName,
                                    if(fileModel.isLocal) AppDirType.Local else AppDirType.Defects
                                ),
                                isLocal = fileModel.isLocal
                            )
                        )
                    }
                }
                updateMediaList()
            }

        }
    }

    private fun updateMediaList() {
        val items = mutableListOf<MediaUiModel>().apply {
            mediaModels.map { item ->
                add(item)
            }
        }
        _mediaList.value = items
    }

    fun addImage(image: Bitmap) {
        val id = UUID.randomUUID().toString()
        mediaModels.add(
            MediaUiModel(
                id = id,
                file = fileUtil.createJpgFile(image, detoursInteractor.getFileInFolder("$id.jpg", AppDirType.Local)),
                isLocal = true
            )
        )
        isChangedDefect = true
        updateMediaList()
    }

    fun addVideo(videoFile: File) {
        mediaModels.add(
            MediaUiModel(
                id = UUID.randomUUID().toString(),
                isLocal = true,
                file = videoFile
            )
        )
        isChangedDefect = true
        updateMediaList()
    }

    fun deleteMedia(deleteItem: MediaUiModel) {
        if (mediaModels.remove(deleteItem)) {
            isChangedDefect = true
            updateMediaList()
        }
    }

    fun photoVideoClick() {
        if (mediaModels.size < 8)
            _navigateToCamera.value = Event(Unit)
    }

    fun saveDefectDb() {
        val model = DefectModel(
            id = UUID.randomUUID().toString(),
            detourId = detourId,
            equipmentId = currentDeviceModel?.id,
            defectTypicalId = currentTypical?.id,
            statusProcessId = DefectStatus.NEW.id,
            description = descriptionDefect.orEmpty(),
            dateDetectDefect = Date(),
            files = getFilesToSend(),
            defectName = currentTypical?.name,
            equipmentName = currentDeviceModel?.name,
            extraData = null,
            staffDetectId = null,
            created = true,
            changed = false
        )
        detoursInteractor.saveDefectDb(model).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({
                _showSnackBar.value = Event(Unit)
                _popNavigation.value = Event(Unit)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun updateDefectDb() {
        defect?.let { defectModel ->
            val model = defectModel.copy(
                statusProcessId = targetDefectStatus?.id.orEmpty(),
                description = descriptionDefect.orEmpty(),
                dateDetectDefect = Date(),
                files = (defectModel.files?: arrayListOf()) + getFilesToSend(),
                changed = true
            )
            detoursInteractor.saveDefectDb(model)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({
                    _popNavigation.value = Event(Unit)
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }

    }

    private fun getFilesToSend(): List<FileModel> {
        return mediaModels.filter { it.isLocal }.mapNotNull { media -> media.file }.map {
            FileModel(
                id = UUID.randomUUID().toString(),
                fileId = UUID.randomUUID().toString(),
                url = "",
                fileName = it.name,
                extension = it.extension,
                date = Date(),
                routeMapName = "",
                isLocal = true
            )
        }
    }


    fun checkAndSave() {
        targetDefectStatus?.let {
            if (isChangedDefect) {
                _showDialogConfirmChangedFields.value = Event(true)
            } else {
                updateDefectDb()
            }
        } ?: run {
            defect?.let {
                _showDialogConfirmChangedFields.value = Event(false)
            } ?: run {
                if (checkIsNoEmptyRequiredFields()) {
                    if (checkIsNotEmptyFields()) {
                        if (detourId.isNullOrEmpty()) {
                            _showDialogSaveNoLinkedDetour.value = Event(Unit)
                        } else {
                            saveDefectDb()
                        }
                    } else {
                        _showDialogBlankFields.value = Event(detourId.isNullOrEmpty())
                    }
                } else {
                    _showDialogBlankRequiredFields.value = Event(Unit)
                }
            }
        }
    }

    private fun checkIsNotEmptyFields(): Boolean {
        return (currentTypical != null) && (!descriptionDefect.isNullOrEmpty()) && (!mediaList.value.isNullOrEmpty())
    }

    private fun checkIsNoEmptyRequiredFields(): Boolean {
        _deviceName.value?.let {
            return true
        } ?: run {
            return false
        }
    }

    fun checkPopBack() {
        if (isChangedDefect) {
            _showDialogChangedFields.value = Event(Unit)
        } else {
            _popNavigation.value = Event(Unit)
        }
    }

}

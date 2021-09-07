package ru.madbrains.inspection.ui.main.defects.defectdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.RemoteInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.domain.model.*
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MediaUiModel
import java.io.File
import java.util.*

class DefectDetailViewModel(
    private val remoteInteractor: RemoteInteractor,
    private val syncInteractor: SyncInteractor,
    private val offlineInteractor: OfflineInteractor
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
    private val uiFiles = mutableListOf<MediaUiModel>()

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
            offlineInteractor.getDefectTypical()
                .observeOn(Schedulers.io())
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
                files?.forEach { fileModel ->
                    val file = offlineInteractor.getFileInFolder(
                        fileModel.fileName,
                        if (fileModel.isNew) AppDirType.Local else AppDirType.Defects
                    )
                    if (file != null) {
                        uiFiles.add(
                            MediaUiModel(
                                id = fileModel.id,
                                file = file,
                                isNew = fileModel.isNew
                            )
                        )
                    }
                }
                updateMediaList()
            }

        }
    }

    private fun updateMediaList() {
        _mediaList.value = mutableListOf<MediaUiModel>().apply {
            addAll(uiFiles)
        }
    }

    fun addFile(file: File) {
        uiFiles.add(
            MediaUiModel(
                id = UUID.randomUUID().toString(),
                isNew = true,
                file = file
            )
        )
        isChangedDefect = true
        updateMediaList()
    }

    fun deleteMedia(deleteItem: MediaUiModel) {
        if (uiFiles.remove(deleteItem)) {
            isChangedDefect = true
            updateMediaList()
        }
    }

    fun photoVideoClick() {
        if (uiFiles.size < 8)
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
            files = prepareFiles(),
            defectName = currentTypical?.name,
            equipmentName = currentDeviceModel?.name,
            extraData = null,
            staffDetectId = null,
            created = true,
            changed = false
        )
        syncInteractor.insertDefect(model).observeOn(Schedulers.io())
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
                statusProcessId = targetDefectStatus?.id ?: defectModel.statusProcessId,
                description = descriptionDefect.orEmpty(),
                dateDetectDefect = Date(),
                files = prepareFiles(),
                changed = !defectModel.created
            )
            syncInteractor.insertDefect(model)
                .observeOn(Schedulers.io())
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

    private fun prepareFiles(): List<FileModel> {
        return uiFiles.map {
            FileModel(
                id = UUID.randomUUID().toString(),
                fileId = UUID.randomUUID().toString(),
                url = "",
                fileName = it.file.name,
                extension = it.file.extension,
                date = Date(),
                routeMapName = "",
                isNew = it.isNew
            )
        }
    }


    fun checkAndSave() {
        if (defect == null) {
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
        } else {
            when {
                targetDefectStatus != null -> {
                    updateDefectDb()
                }
                isChangedDefect -> {
                    _showDialogConfirmChangedFields.value = Event(true)
                }
                else -> {
                    _popNavigation.value = Event(Unit)
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

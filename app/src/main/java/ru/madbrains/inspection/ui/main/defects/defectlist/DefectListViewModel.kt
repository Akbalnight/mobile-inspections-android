package ru.madbrains.inspection.ui.main.defects.defectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.data.extensions.toDDMMYYYY
import ru.madbrains.data.extensions.toHHmm
import ru.madbrains.data.extensions.toddMMyyyyHHmm
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.domain.model.DefectStatus
import ru.madbrains.domain.model.FileModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DefectListUiModel
import ru.madbrains.inspection.ui.delegates.MediaUiModel
import java.util.*

class DefectListViewModel(
    private val syncInteractor: SyncInteractor,
    private val offlineInteractor: OfflineInteractor
) : BaseViewModel() {

    val defectListModels = mutableListOf<DefectModel>()

    var isDefectRegistry: Boolean = false
    var isEditable: Boolean = false

    //Live Data
    private val _defectList = MutableLiveData<List<DiffItem>>()
    val defectList: LiveData<List<DiffItem>> = _defectList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    //Event
    private val _navigateToEditDefect = MutableLiveData<Event<DefectModel>>()
    val navigateToEditDefect: LiveData<Event<DefectModel>> = _navigateToEditDefect

    private val _navigateToConfirmDefect = MutableLiveData<Event<DefectModel>>()
    val navigateToConfirmDefect: LiveData<Event<DefectModel>> = _navigateToConfirmDefect

    private var savedEquipmentIds: List<String>? = null
    private var savedDetourId: String? = null

    fun editDefect(defect: DefectModel?) {
        defect?.let { _navigateToEditDefect.postValue(Event(it)) }
    }

    fun confirmDefect(defect: DefectModel?) {
        defect?.let { _navigateToConfirmDefect.postValue(Event(it)) }
    }

    fun initData(detourId: String? = null, equipmentIds: List<String>? = null) {
        savedEquipmentIds = equipmentIds
        savedDetourId = detourId

        getDefectList()
    }

    private fun getDefectList() {
        val equipmentIds = savedEquipmentIds
        val detourId = savedDetourId
        val single =
            if (equipmentIds != null) offlineInteractor.getActiveDefects(
                detourId,
                equipmentIds
            ) else offlineInteractor.getAllDefects()
        single
            .observeOn(Schedulers.io())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                defectListModels.clear()
                defectListModels.addAll(items.filter {
                    it.statusProcessId != DefectStatus.ELIMINATED.id
                })
                updateDefectList()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun deleteDefect(deleteItem: DefectModel?) {
        deleteItem?.let { item ->
            syncInteractor.delDefectDb(item.id)
                .observeOn(Schedulers.io())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({
                    getDefectList()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

    private fun updateDefectList() {
        val defects = mutableListOf<DiffItem>().apply {
            defectListModels.map { defect ->
                add(
                    DefectListUiModel(
                        id = defect.id,
                        detour = defect.detourId.orEmpty(),
                        date = defect.dateDetectDefect?.toDDMMYYYY().orEmpty(),
                        time = defect.dateDetectDefect?.toHHmm().orEmpty(),
                        device = defect.equipmentName.orEmpty(),
                        type = defect.defectName.orEmpty(),
                        description = defect.description.orEmpty(),
                        images = getMediaListItem(defect.files),
                        isEditConfirmMode = !isDefectRegistry && isEditable,
                        isEditMode = defect.created && isEditable,
                        dateConfirm = defect.getLastDateConfirm()?.toddMMyyyyHHmm().orEmpty()
                    )
                )
            }
        }
        _defectList.postValue(defects)
    }

    private fun getMediaListItem(files: List<FileModel>?): List<MediaUiModel> {
        val list: MutableList<MediaUiModel> = mutableListOf()
        files?.let {
            files.forEach { fileModel ->
                val file = offlineInteractor.getFileInFolder(
                    fileModel.fileName,
                    if (fileModel.isNew) AppDirType.Local else AppDirType.Defects
                )
                if (file != null) {
                    list.add(
                        MediaUiModel(
                            id = fileModel.id,
                            file = file,
                            isNew = fileModel.isNew
                        )
                    )
                }
            }
        }
        return list
    }

    fun eliminateDefect(deleteItem: DefectModel?) {
        deleteItem?.let { it ->
            syncInteractor.insertDefect(
                it.copy(
                    id = it.id,
                    statusProcessId = DefectStatus.ELIMINATED.id,
                    dateDetectDefect = Date(),
                    changed = true
                )
            )
                .observeOn(Schedulers.io())
                .subscribe({
                    getDefectList()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

}
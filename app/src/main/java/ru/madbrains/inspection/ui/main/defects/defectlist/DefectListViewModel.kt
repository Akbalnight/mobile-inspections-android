package ru.madbrains.inspection.ui.main.defects.defectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
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

    private var lastDeviceIds: List<String>? = null

    fun editDefect(defect: DefectModel?) {
        defect?.let { _navigateToEditDefect.value = Event(it) }
    }

    fun confirmDefect(defect: DefectModel?) {
        defect?.let { _navigateToConfirmDefect.value = Event(it) }
    }

    fun getDefectList(deviceIds: List<String>?) {
        lastDeviceIds = deviceIds
        val single =
            if (deviceIds != null) offlineInteractor.getActiveDefects(equipmentIds = deviceIds) else offlineInteractor.getDefects()
        single
            .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({
                    getDefectList(lastDeviceIds)
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
        _defectList.value = defects
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
            syncInteractor.insertDefect(it.copy(
                id = it.id,
                statusProcessId = DefectStatus.ELIMINATED.id,
                dateDetectDefect = Date()
            ).apply {
                changed = true
            })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getDefectList(lastDeviceIds)
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

}
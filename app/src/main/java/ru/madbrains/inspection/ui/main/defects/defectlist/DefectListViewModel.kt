package ru.madbrains.inspection.ui.main.defects.defectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.extensions.toDDMMYYYY
import ru.madbrains.data.extensions.toHHmm
import ru.madbrains.data.extensions.toddMMyyyyHHmm
import ru.madbrains.data.extensions.toyyyyMMddTHHmmssXXX
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.domain.model.DefectStatus
import ru.madbrains.domain.model.FileModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DefectListUiModel
import ru.madbrains.inspection.ui.delegates.MediaUiModel
import timber.log.Timber
import java.util.*

class DefectListViewModel(private val detoursInteractor: DetoursInteractor) : BaseViewModel() {

    val defectListModels = mutableListOf<DefectModel>()

    private var isConfirmList: Boolean = false

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

    fun editDefect(defect: DefectModel?) {
        defect?.let { _navigateToEditDefect.value = Event(it) }
    }

    fun confirmDefect(defect: DefectModel?) {
        defect?.let { _navigateToConfirmDefect.value = Event(it) }
    }

    fun getDefectList(deviceIds: List<String>?) {
        detoursInteractor.getDefectsDb(equipmentIds = deviceIds)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                defectListModels.clear()
                defectListModels.addAll(items)
                updateDefectList()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)

    }

    fun setConfirmList(isConfirm: Boolean) {
        isConfirmList = isConfirm
    }

    fun deleteDefect(deleteItem: DefectModel?) {
        //todo offline delete
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
                        isConfirmList = isConfirmList,
                        images = getMediaListItem(defect.files),
                        isLocal = defect.isNew,
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
            files.map { fileModel ->
                list.add(
                    MediaUiModel(
                        id = fileModel.id,
                        file = detoursInteractor.getFileInFolder(
                            fileModel.name,
                            if(fileModel.isLocal) AppDirType.Local else AppDirType.Defects
                        ),
                        isLocal = fileModel.isLocal
                    )
                )
            }
        }
        return list
    }

    fun eliminateDefect(deleteItem: DefectModel?) {
        deleteItem?.let { it ->
            detoursInteractor.updateDefectRemote(it.copy(
                id = it.id,
                statusProcessId = DefectStatus.ELIMINATED.id,
                dateDetectDefect = Date()
            ))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

}
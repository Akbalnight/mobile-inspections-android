package ru.madbrains.inspection.ui.main.defects.defectlist

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.domain.model.FileModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DefectListUiModel
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel
import java.text.ParseException
import java.text.SimpleDateFormat

class DefectListViewModel(private val routesInteractor: RoutesInteractor) : BaseViewModel() {


    private val _defectList = MutableLiveData<List<DiffItem>>()
    private var deviceIds: List<String>? = null
    val defectList: LiveData<List<DiffItem>> = _defectList

    private val defectListModels = mutableListOf<DefectModel>()

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    fun getDefectList(device: List<String>?) {
        deviceIds = device
        routesInteractor.getDefects(equipmentNames = getDeviceIdsList())
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

    private fun getDeviceIdsList(): List<String> {
        deviceIds?.let {
            return it
        }
        return emptyList()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateDefectList() {
        val defects = mutableListOf<DiffItem>().apply {
            defectListModels.map { defect ->
                var date = ""
                var time = ""
                defect.dateDetectDefect?.let {
                    val formatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                    val formatDate = SimpleDateFormat("dd.MM.yyyy")
                    val formatTime = SimpleDateFormat("HH:mm")
                    try {
                        val dateInput = formatInput.parse(it)
                        date = formatDate.format(dateInput)
                        time = formatTime.format(dateInput)

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
                add(
                        DefectListUiModel(
                                id = defect.id,
                                detour = defect.detourId.orEmpty(),
                                date = date,
                                time = time,
                                device = defect.equipmentName.orEmpty(),
                                type = defect.defectName.orEmpty(),
                                description = defect.description.orEmpty(),
                                isCommonList = getDeviceIdsList().isNullOrEmpty(),
                                images = getMediaListItem(defect.files)
                        )
                )
            }
        }
        _defectList.value = defects
    }

    private fun getMediaListItem(files: List<FileModel>?): List<MediaDefectUiModel> {

        val list: MutableList<MediaDefectUiModel> = mutableListOf()
        files?.let {
            files.map { fileModel ->
                when (fileModel.extension) {
                    "png" -> { // если в файле изображение добавляем в список
                        list.add(MediaDefectUiModel(
                                id = fileModel.id.orEmpty(),
                                isEditing = false,
                                url = "https://mobinspect.dias-dev.ru${fileModel.url.orEmpty()}" //todo change to constant
                                //todo isImage если видео
                                //todo image если видео
                        ))
                    }
                    "mpeg" -> {
                        //todo preview video
                    }
                    else -> {
                        //todo delete when extension all files
                        list.add(MediaDefectUiModel(
                                id = fileModel.id.orEmpty(),
                                isEditing = false,
                                url = "https://mobinspect.dias-dev.ru${fileModel.url.orEmpty()}" //todo change to constant
                                //todo isImage если видео
                                //todo image если видео
                        ))
                    }
                }
            }
        }
        return list
    }
}
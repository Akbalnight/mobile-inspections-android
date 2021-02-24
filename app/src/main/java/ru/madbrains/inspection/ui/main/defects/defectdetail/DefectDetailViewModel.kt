package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectTypicalModel
import ru.madbrains.domain.model.EquipmentsModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel
import java.util.*

class DefectDetailViewModel(private val routesInteractor: RoutesInteractor) :
        BaseViewModel() {

    private val defectTypicalModels = mutableListOf<DefectTypicalModel>()
    private val mediaModels = mutableListOf<MediaDefectUiModel>()

    private val _defectTypicalList = MutableLiveData<List<DefectTypicalUiModel>>()
    val defectTypicalList: LiveData<List<DefectTypicalUiModel>> = _defectTypicalList

    private val _device = MutableLiveData<EquipmentsModel>()
    val device: LiveData<EquipmentsModel> = _device

    private val _mediaList = MutableLiveData<List<MediaDefectUiModel>>()
    val mediaList: LiveData<List<MediaDefectUiModel>> = _mediaList

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
        // todo
    }


    fun changeCurrentDefectDevice(model: EquipmentsModel) {
        _device.value = model
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
        mediaModels.add(MediaDefectUiModel(
                id = UUID.randomUUID().toString(),
                image = image,
                isVideo = false))
        updateMediaList()
    }

    private fun updateMediaList() {
        val items = mutableListOf<MediaDefectUiModel>().apply {
            mediaModels.map { item ->
                add(item)
            }
        }
        _mediaList.value = items
    }
}

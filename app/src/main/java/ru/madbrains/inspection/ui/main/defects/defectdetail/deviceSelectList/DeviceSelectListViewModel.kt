package ru.madbrains.inspection.ui.main.defects.defectdetail.deviceSelectList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.EquipmentsModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.DeviceSelectUiModel

class DeviceSelectListViewModel(private val routesInteractor: RoutesInteractor) :
    BaseViewModel() {

    private var names: List<String> = emptyList()
    private var controlPointsIds: List<String> = emptyList()

    val deviceListModels = mutableListOf<EquipmentsModel>()

    private val _deviceList = MutableLiveData<List<DeviceSelectUiModel>>()
    val deviceList: LiveData<List<DeviceSelectUiModel>> = _deviceList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToDefectDetail = MutableLiveData<Event<EquipmentsModel>>()
    val navigateToDefectDetail: LiveData<Event<EquipmentsModel>> = _navigateToDefectDetail

    fun deviceSelectClick(equipment: EquipmentsModel?) {
        equipment?.let {
            _navigateToDefectDetail.value = Event(it) }
    }

    fun getEquipments() {
        routesInteractor.getEquipments(names, controlPointsIds)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                deviceListModels.addAll(items)
                updateDeviceList()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun updateDeviceList() {
        val items = mutableListOf<DeviceSelectUiModel>().apply {
            deviceListModels.map { item ->
                add(
                    DeviceSelectUiModel(
                        id = item.id.orEmpty(),
                        name = item.name.orEmpty()
                    )
                )
            }
        }
        _deviceList.value = items
    }

}
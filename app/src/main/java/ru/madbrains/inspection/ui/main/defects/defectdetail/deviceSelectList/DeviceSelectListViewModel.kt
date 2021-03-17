package ru.madbrains.inspection.ui.main.defects.defectdetail.deviceSelectList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.DeviceSelectUiModel

class DeviceSelectListViewModel(private val routesInteractor: RoutesInteractor) :
        BaseViewModel() {

    private var names: List<String> = emptyList()
    private var controlPointsIds: List<String> = emptyList()

    val deviceListModels = mutableListOf<EquipmentModel>()

    private val _deviceList = MutableLiveData<List<DeviceSelectUiModel>>()
    val deviceList: LiveData<List<DeviceSelectUiModel>> = _deviceList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToDefectDetail = MutableLiveData<Event<EquipmentModel>>()
    val navigateToDefectDetail: LiveData<Event<EquipmentModel>> = _navigateToDefectDetail

    fun deviceSelectClick(equipment: EquipmentModel?) {
        equipment?.let {
            _navigateToDefectDetail.value = Event(it)
        }
    }

    fun setEquipments(equipment: List<EquipmentModel>) {
        deviceListModels.clear()
        deviceListModels.addAll(equipment)
    }

    fun getEquipments() {
        if(deviceListModels.isNullOrEmpty()) {
            routesInteractor.getEquipments(names, controlPointsIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _progressVisibility.postValue(true) }
                    .doAfterTerminate { _progressVisibility.postValue(false) }
                    .subscribe({ items ->
                        deviceListModels.clear()
                        deviceListModels.addAll(items)
                        updateDeviceList()
                    }, {
                        it.printStackTrace()
                    })
                    .addTo(disposables)
        }
        else {
            updateDeviceList()
        }
    }

    fun searchEquipments(query: String) {
        val items = deviceListModels
                .filter { it.name.orEmpty().contains(query) }
                .map {
                    DeviceSelectUiModel(
                            id = it.id.orEmpty(),
                            name = it.name.orEmpty()
                    )
                }
        _deviceList.value = items
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
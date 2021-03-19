package ru.madbrains.inspection.ui.main.defects.defectdetail.equipmentselectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.EquipmentSelectUiModel

class EquipmentSelectListViewModel(private val routesInteractor: RoutesInteractor) :
        BaseViewModel() {

    private var names: List<String> = emptyList()
    private var controlPointsIds: List<String> = emptyList()

    val deviceListModels = mutableListOf<EquipmentModel>()
    private var currentDevice: EquipmentModel? = null

    private val _deviceList = MutableLiveData<List<EquipmentSelectUiModel>>()
    val deviceList: LiveData<List<EquipmentSelectUiModel>> = _deviceList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToDefectDetail = MutableLiveData<Event<EquipmentModel>>()
    val navigateToDefectDetail: LiveData<Event<EquipmentModel>> = _navigateToDefectDetail

    fun deviceSelectClick(equipment: EquipmentModel?) {
        equipment?.let {
            _navigateToDefectDetail.value = Event(it)
        }
    }

    fun setEquipments(equipment: List<EquipmentModel>?) {
        deviceListModels.clear()
        equipment?.let {
            deviceListModels.addAll(it)
        }
    }

    fun setCurrentDevice(device: EquipmentModel?) {
        currentDevice = device
    }

    fun getEquipments() {
        if (deviceListModels.isNullOrEmpty()) {
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
        } else {
            updateDeviceList()
        }
    }

    fun searchEquipments(query: String) {
        val items = deviceListModels
                .filter { it.name.orEmpty().contains(query) }
                .map {
                    EquipmentSelectUiModel(
                            id = it.id.orEmpty(),
                            name = it.name.orEmpty()
                    )
                }
        _deviceList.value = items
    }

    private fun updateDeviceList() {
        val items = mutableListOf<EquipmentSelectUiModel>().apply {
            deviceListModels.map { item ->
                add(
                        EquipmentSelectUiModel(
                                id = item.id,
                                name = item.name.orEmpty(),
                                isSelected = item.id == currentDevice?.id
                        )
                )
            }
        }
        _deviceList.value = items
    }

}
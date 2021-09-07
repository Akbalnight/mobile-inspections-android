package ru.madbrains.inspection.ui.main.defects.defectdetail.equipmentselectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.EquipmentSelectUiModel

class EquipmentSelectListViewModel(
    private val offlineInteractor: OfflineInteractor
) :
    BaseViewModel() {

    val deviceListModels = mutableListOf<EquipmentModel>()
    private var currentDevice: EquipmentModel? = null

    private val _deviceList = MutableLiveData<List<EquipmentSelectUiModel>>()
    val deviceList: LiveData<List<EquipmentSelectUiModel>> = _deviceList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _checkedDevice = MutableLiveData<Event<EquipmentModel>>()
    val checkedDevice: LiveData<Event<EquipmentModel>> = _checkedDevice

    private val _navigateToDefectDetail = MutableLiveData<Event<EquipmentModel>>()
    val navigateToDefectDetail: LiveData<Event<EquipmentModel>> = _navigateToDefectDetail

    fun deviceSelectClick(equipment: EquipmentModel?) {
        equipment?.let {
            _checkedDevice.value = Event(it)
            _navigateToDefectDetail.value = Event(it)
            deviceListModels.clear()
            currentDevice = null
            _deviceList.value = null

        }
    }

    fun setEquipments(equipment: List<EquipmentModel>?) {
        deviceListModels.clear()
        equipment?.let {
            deviceListModels.addAll(it)
            updateDeviceList()
        }
    }

    fun setCurrentDevice(device: EquipmentModel?) {
        currentDevice = device
    }

    fun getEquipments() {
        if (deviceListModels.isNullOrEmpty()) {
            offlineInteractor.getEquipments()
                .observeOn(Schedulers.io())
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
    }

    fun searchEquipments(query: String) {
        val items = deviceListModels
            .filter { it.name.orEmpty().contains(query, true) }
            .map {
                EquipmentSelectUiModel(
                    id = it.id,
                    name = it.name.orEmpty(),
                    isSelected = it.id == currentDevice?.id
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
package ru.madbrains.inspection.ui.main.routes.techoperations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.domain.model.TechOperationModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.TechOperationUiModel
import timber.log.Timber

class TechOperationsViewModel(
    private val routesInteractor: RoutesInteractor,
    private val rfidDevice: RfidDevice
) :
        BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _titleTechOperations = MutableLiveData<String>()
    val titleTechOperations: LiveData<String> = _titleTechOperations

    private val _techOperations = MutableLiveData<List<DiffItem>>()
    val techOperations: LiveData<List<DiffItem>> = _techOperations

    private val operationsModels = mutableListOf<TechOperationModel>()

    private val _popBack = MutableLiveData<Event<Unit>>()
    val popBack: LiveData<Event<Unit>> = _popBack

    private val _navigateToEquipment = MutableLiveData<Event<EquipmentModel>>()
    val navigateToEquipment: LiveData<Event<EquipmentModel>> = _navigateToEquipment

    private val _navigateToEquipmentList = MutableLiveData<Event<List<EquipmentModel>>>()
    val navigateToEquipmentList: LiveData<Event<List<EquipmentModel>>> = _navigateToEquipmentList

    private val _completeTechMapEvent = MutableLiveData<Event<TechMapModel>>()
    val completeTechMapEvent: LiveData<Event<TechMapModel>> = _completeTechMapEvent

    private val _navigatePop = MutableLiveData<Event<Unit>>()
    val navigatePop: LiveData<Event<Unit>> = _navigatePop

    private val _showDialogBlankFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankFields: LiveData<Event<Unit>> = _showDialogBlankFields

    var savedRouteData: RouteDataModel? = null

    //Models LiveData
    private val _rfidProgress = MutableLiveData<Boolean>()
    val rfidProgress: LiveData<Boolean> = _rfidProgress

    private val _showDialog = MutableLiveData<Event<Int>>()
    val showDialog: LiveData<Event<Int>> = _showDialog


    fun finishTechMap() {
        Timber.d("debug_dmm finish")
        savedRouteData?.techMap?.let { _completeTechMapEvent.value = Event(it) }
        _navigatePop.value = Event(Unit)
    }

    fun checkAvailableFinishTechMap() {
        savedRouteData?.let { routeData ->
            routeData.techMap?.techOperations?.filter {
                (it.needInputData == true) && it.valueInputData.isNullOrEmpty()
            }?.let { filterList ->
                if (filterList.isNullOrEmpty()) {
                    finishTechMap()
                } else {
                    _showDialogBlankFields.value = Event(Unit)
                }
            }
        }
    }

    fun setRouteData(routeDataModel: RouteDataModel) {

        savedRouteData = routeDataModel

        routeDataModel.techMap?.techOperations?.let {
            operationsModels.clear()
            operationsModels.addAll(it)
        }

        _titleTechOperations.value = routeDataModel.techMap?.name

        updateData()
    }

    fun onTechDataInput(techOperationId: String, dataValue: String) {
        savedRouteData?.let { routeData ->
            routeData.techMap?.techOperations?.find { it.id == techOperationId }?.let { techOperation ->
                techOperation.valueInputData = dataValue
            }
        }
    }

    private fun updateData() {
        val operations = mutableListOf<DiffItem>().apply {
            operationsModels.map { operation ->
                add(
                        TechOperationUiModel(
                                id = operation.id,
                                name = operation.name.orEmpty(),
                                labelInputData = operation.labelInputData.orEmpty(),
                                valueInputData = operation.valueInputData.orEmpty(),
                                needInputData = operation.needInputData,
                                position = operation.position
                        )
                )
            }
        }
        _techOperations.value = operations
    }

    fun checkRfidAndFinish(){
        rfidDevice.startScan({
            _rfidProgress.value = it
        }) { scannedCode->
            savedRouteData?.rfidCode?.let { code->
                if(scannedCode == code){
                    finishTechMap()
                    _popBack.value = Event(Unit)
                } else{
                    _showDialog.value = Event(R.string.fragment_tech_mark_is_different)
                }
            }
        }
    }

    fun stopRfidScan(){
        rfidDevice.stopScan()
    }

    fun toEquipmentFragment() {
        savedRouteData?.equipments?.let {
            if (it.size == 1) {
                _navigateToEquipment.value = Event(it[0])
            } else if (it.size > 1) {
                _navigateToEquipmentList.value = Event(it)
            }
        }
    }
}
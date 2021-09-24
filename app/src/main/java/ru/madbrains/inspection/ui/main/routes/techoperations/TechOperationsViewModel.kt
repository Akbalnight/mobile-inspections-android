package ru.madbrains.inspection.ui.main.routes.techoperations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.RfidInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.RouteDataModelWithDetourId
import ru.madbrains.domain.model.TechOperationModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.TechOperationUiModel

class TechOperationsViewModel(
    private val rfidInteractor: RfidInteractor
) :
    BaseViewModel() {

    private var _detourIsEditable: Boolean = false

    private val _uiMode = MutableLiveData<TechUIMode>()
    val uiMode: LiveData<TechUIMode> = _uiMode

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _titleTechOperations = MutableLiveData<String>()
    val titleTechOperations: LiveData<String> = _titleTechOperations

    private val _techOperations = MutableLiveData<List<DiffItem>>()
    val techOperations: LiveData<List<DiffItem>> = _techOperations

    private val operationsModels = mutableListOf<TechOperationModel>()

    private val _navigateToEquipment = MutableLiveData<Event<EquipmentModel>>()
    val navigateToEquipment: LiveData<Event<EquipmentModel>> = _navigateToEquipment

    private val _navigateToEquipmentList = MutableLiveData<Event<List<EquipmentModel>>>()
    val navigateToEquipmentList: LiveData<Event<List<EquipmentModel>>> = _navigateToEquipmentList

    private val _navigateToDefectDetailFragment =
        MutableLiveData<Event<RouteDataModelWithDetourId>>()
    val navigateToDefectDetailFragment: LiveData<Event<RouteDataModelWithDetourId>> =
        _navigateToDefectDetailFragment

    private val _navigateToDefectList = MutableLiveData<Event<RouteDataModelWithDetourId>>()
    val navigateToDefectList: LiveData<Event<RouteDataModelWithDetourId>> = _navigateToDefectList

    private val _completeTechMapEvent = MutableLiveData<Event<RouteDataModel>>()
    val completeTechMapEvent: LiveData<Event<RouteDataModel>> = _completeTechMapEvent

    private val _navigatePop = MutableLiveData<Event<Unit>>()
    val navigatePop: LiveData<Event<Unit>> = _navigatePop

    private val _showDialogBlankFields = MutableLiveData<Event<Unit>>()
    val showDialogBlankFields: LiveData<Event<Unit>> = _showDialogBlankFields

    private var savedRouteDataWrap: RouteDataModelWithDetourId? = null
    val savedRouteData: RouteDataModel? get() = savedRouteDataWrap?.routeData

    //Models LiveData
    private val _rfidProgress = MutableLiveData<Boolean>()
    val rfidProgress: LiveData<Boolean> = _rfidProgress

    private val _showDialog = MutableLiveData<Event<Int>>()
    val showDialog: LiveData<Event<Int>> = _showDialog


    fun finishTechMap() {
        savedRouteData?.let { data ->
            _completeTechMapEvent.postValue(Event(data))
            _navigatePop.postValue(Event(Unit))
        }
    }

    private fun checkAvailableFinishTechMap() {
        savedRouteData?.let { routeData ->
            routeData.techMap?.techOperations?.filter {
                (it.needInputData == true) && it.valueInputData.isNullOrEmpty()
            }?.let { filterList ->
                if (filterList.isNullOrEmpty()) {
                    finishTechMap()
                } else {
                    _showDialogBlankFields.postValue(Event(Unit))
                }
            }
        }
    }

    fun init(data: RouteDataModelWithDetourId, detourEditable: Boolean) {
        _detourIsEditable = detourEditable
        savedRouteDataWrap = data
        val routeData = data.routeData
        routeData.techMap?.techOperations?.let {
            operationsModels.clear()
            operationsModels.addAll(it)
        }

        _titleTechOperations.postValue(routeData.techMap?.name)

        val isRfid = routeData.rfidCode != null
        updateData(rfidBlocked = isRfid)
    }

    fun onTechDataInput(techOperationId: String, dataValue: String) {
        savedRouteData?.let { routeData ->
            routeData.techMap?.techOperations?.let { list ->
                val index = list.indexOfFirst { it.id == techOperationId }
                val mutableList = list.toMutableList()
                if (index != -1) {
                    val item = mutableList[index]
                    mutableList[index] = item.copy(valueInputData = dataValue)
                    savedRouteDataWrap = savedRouteDataWrap?.copy(
                        routeData = routeData.copy(
                            techMap = routeData.techMap?.copy(techOperations = mutableList)
                        )
                    )
                }
            }
        }
    }

    private fun updateData(rfidBlocked: Boolean) {
        val operations = mutableListOf<DiffItem>().apply {
            operationsModels.map { operation ->
                add(
                    TechOperationUiModel(
                        id = operation.id,
                        name = operation.name.orEmpty(),
                        labelInputData = operation.labelInputData.orEmpty(),
                        valueInputData = operation.valueInputData.orEmpty(),
                        needInputData = operation.needInputData,
                        position = operation.position,
                        editable = _detourIsEditable && !rfidBlocked
                    )
                )
            }
        }
        _techOperations.postValue(operations)

        val status = when {
            !_detourIsEditable -> {
                TechUIMode.Disabled
            }
            rfidBlocked -> {
                TechUIMode.RfidBlocked
            }
            else -> {
                TechUIMode.Enabled
            }
        }
        _uiMode.postValue(status)
    }

    private fun checkRfidAndUnblock() {
        rfidInteractor.startScan({
            _rfidProgress.postValue(it)
        }) { scannedCode ->
            if (scannedCode == savedRouteData?.rfidCode) {
                updateData(rfidBlocked = false)
            } else {
                _showDialog.postValue(Event(R.string.fragment_tech_mark_is_different))
            }
        }
    }

    fun stopRfidScan() {
        rfidInteractor.stopScan()
    }

    fun toEquipmentFragment() {
        savedRouteData?.equipments?.let {
            if (it.size == 1) {
                _navigateToEquipment.postValue(Event(it[0]))
            } else if (it.size > 1) {
                _navigateToEquipmentList.postValue(Event(it))
            }
        }
    }

    fun fabClick() {
        when (_uiMode.value) {
            TechUIMode.RfidBlocked -> checkRfidAndUnblock()
            TechUIMode.Enabled -> checkAvailableFinishTechMap()
            else -> {
            }
        }
    }

    fun addDefectClick() {
        savedRouteDataWrap?.let {
            _navigateToDefectDetailFragment.postValue(Event(it))
        }

    }

    fun navigateToDefectList() {
        val data = savedRouteDataWrap
        if (data != null) {
            _navigateToDefectList.postValue(Event(data))

        }
    }
}

enum class TechUIMode {
    Disabled, RfidBlocked, Enabled
}
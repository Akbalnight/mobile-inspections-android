package ru.madbrains.inspection.ui.main.routes.techoperations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.domain.model.TechOperationModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.TechOperationUiModel

class TechOperationsViewModel(private val routesInteractor: RoutesInteractor) :
    BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _titleTechOperations = MutableLiveData<String>()
    val titleTechOperations: LiveData<String> = _titleTechOperations

    private val _techOperations = MutableLiveData<List<DiffItem>>()
    val techOperations: LiveData<List<DiffItem>> = _techOperations

    private val operationsModels = mutableListOf<TechOperationModel>()

    private val _navigateToAddDefect = MutableLiveData<Event<Unit>>()
    val navigateToAddDefect: LiveData<Event<Unit>> = _navigateToAddDefect

    private val _navigateToEquipment = MutableLiveData<Event<EquipmentModel>>()
    val navigateToEquipment: LiveData<Event<EquipmentModel>> = _navigateToEquipment

    private val _navigateToEquipmentList = MutableLiveData<Event<List<EquipmentModel>>>()
    val navigateToEquipmentList: LiveData<Event<List<EquipmentModel>>> = _navigateToEquipmentList

    private val _completeTechMapEvent = MutableLiveData<Event<TechMapModel>>()
    val completeTechMapEvent: LiveData<Event<TechMapModel>> = _completeTechMapEvent

    var savedRouteData: RouteDataModel? = null

    fun finishTechMap() {
        savedRouteData?.techMap?.let { _completeTechMapEvent.value = Event(it) }
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
        savedRouteData?.let { routeData->
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


    fun toEquipmentFragment() {
        savedRouteData?.equipments?.let {
            if(it.size == 1){
                _navigateToEquipment.value = Event(it[0])
            } else if(it.size > 1){
                _navigateToEquipmentList.value = Event(it)
            }
        }
    }
}
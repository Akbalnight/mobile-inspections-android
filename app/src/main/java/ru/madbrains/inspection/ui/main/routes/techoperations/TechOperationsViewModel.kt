package ru.madbrains.inspection.ui.main.routes.techoperations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.RoutePointModel
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

    private val _completeTechMapEvent = MutableLiveData<Event<TechMapModel>>()
    val completeTechMapEvent: LiveData<Event<TechMapModel>> = _completeTechMapEvent

    var techMap: TechMapModel? = null

    fun finishTechMap() {
        techMap?.let { _completeTechMapEvent.value = Event(it) }
    }

    fun setTechMapModel(techMapModel: TechMapModel) {

        techMap = techMapModel

        techMapModel.techOperations.let {
            operationsModels.clear()
            operationsModels.addAll(it)
        }

        _titleTechOperations.value = techMapModel.name

        updateData()
    }

    fun onTechDataInput(techOperationId: String, dataValue: String) {
        techMap?.let { techMap ->
            techMap.techOperations.find { it.id == techOperationId }?.let { techOperation ->
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

}
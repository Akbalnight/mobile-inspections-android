package ru.madbrains.inspection.ui.main.routes.techoperations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.PlanTechOperationsModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.inspection.base.BaseViewModel
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

    private val operationsModels = mutableListOf<PlanTechOperationsModel>()

    var routePointModel: RoutePointModel? = null

    fun setPoint(routePoint: RoutePointModel) {
        routePointModel = routePoint
        getCard(routePoint.id)
        routePoint.techMapName?.let { name ->
            _titleTechOperations.value = name
        }

    }

    private fun getCard(dataId: String) {
        routesInteractor.getPlanTechOperations(dataId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({ operations ->
                    operationsModels.addAll(operations)
                    updateData()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
    }

    private fun updateData() {
        val operations = mutableListOf<DiffItem>().apply {
            operationsModels.map { operation ->
                add(
                        TechOperationUiModel(
                                id = operation.id,
                                name = operation.name.orEmpty(),
                                labelInputData = operation.labelInputData.orEmpty(),
                                needInputData = operation.needInputData,
                                position = operation.position
                        )
                )
            }
        }
        _techOperations.value = operations
    }
}
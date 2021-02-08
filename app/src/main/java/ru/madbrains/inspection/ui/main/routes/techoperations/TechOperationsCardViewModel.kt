package ru.madbrains.inspection.ui.main.routes.techoperations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.PlanTechOperationsModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem

class TechOperationsCardViewModel(private val routesInteractor: RoutesInteractor) :
    BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _techOperations = MutableLiveData<List<DiffItem>>()
    val techOperations: LiveData<List<DiffItem>> = _techOperations

    private val operationsModels = mutableListOf<PlanTechOperationsModel>()

    fun routeClick() {

    }

    fun getCard() {
        routesInteractor.getPlanTechOperations()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ operations ->
                operationsModels.addAll(operations)
              //  updateData()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }
}
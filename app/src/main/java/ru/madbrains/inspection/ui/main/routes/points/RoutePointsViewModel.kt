package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel

class RoutePointsViewModel(
    private val routesInteractor: RoutesInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _routePoints = MutableLiveData<List<DiffItem>>()
    val routePoints: LiveData<List<DiffItem>> = _routePoints

    var routeModel: DetourModel? = null
    val routePointModels = mutableListOf<RouteDataModel>()

    private fun getRoutePoints(routeId: String) {
        routePointModels.clear()
        routeModel?.route?.routeData?.let { routePointModels.addAll(it) }
        updateData()
//        routesInteractor.getRoutePoints(routeId)
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { _progressVisibility.postValue(true) }
//            .doAfterTerminate { _progressVisibility.postValue(false) }
//            .subscribe({
//                routePointModels.clear()
//                routePointModels.addAll(it)
//                updateData()
//            }, {
//                it.printStackTrace()
//            })
//            .addTo(disposables)
    }

    fun setRoute(route: DetourModel) {
        routeModel = route
        getRoutePoints(route.id)
    }

    private fun updateData() {
        val routePoints = mutableListOf<DiffItem>().apply {
            routePointModels.map { route ->
                add(
                    RoutePointUiModel(
                        id = route.techMap.id,
                        name = route.techMap.name.orEmpty()
                    )
                )
            }
        }
        _routePoints.value = routePoints
    }
}
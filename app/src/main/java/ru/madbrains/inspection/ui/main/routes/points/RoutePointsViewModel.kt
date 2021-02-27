package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DetourModel
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
    val routePointModels = mutableListOf<RoutePointModel>()

    private fun getRoutePoints(routeId: String) {
        routesInteractor.getRoutePoints(routeId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({
                routePointModels.clear()
                routePointModels.addAll(it)
                updateData()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun setRoute(route: DetourModel) {
        routeModel = route
        getRoutePoints(route.id)
    }

    private fun updateData() {
        val routePoints = mutableListOf<DiffItem>().apply {
            routePointModels.map { routePoint ->
                add(
                    RoutePointUiModel(
                        id = routePoint.id,
                        name = routePoint.techMapName.orEmpty()
                    )
                )
            }
        }
        _routePoints.value = routePoints
    }
}
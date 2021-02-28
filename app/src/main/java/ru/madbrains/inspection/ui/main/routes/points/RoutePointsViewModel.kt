package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
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

    var detourModel: DetourModel? = null

    val routeDataModels = mutableListOf<RouteDataModel>()

    fun setDetour(detour: DetourModel) {
        detourModel = detour
        routeDataModels.clear()
        routeDataModels.addAll(detour.route.routeData)
        updateData()
    }

    private fun updateData() {
        val routePoints = mutableListOf<DiffItem>().apply {
            routeDataModels.map { route ->
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
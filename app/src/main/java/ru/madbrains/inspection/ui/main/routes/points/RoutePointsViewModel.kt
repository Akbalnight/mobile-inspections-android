package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel

class RoutePointsViewModel(
    private val routesInteractor: RoutesInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToNextRoute = MutableLiveData<Event<RouteDataModel>>()
    val navigateToNextRoute: LiveData<Event<RouteDataModel>> = _navigateToNextRoute

    private val _routePoints = MutableLiveData<List<DiffItem>>()
    val routePoints: LiveData<List<DiffItem>> = _routePoints

    private val _routeStatus = MutableLiveData<RouteStatus>()
    val routeStatus: LiveData<RouteStatus> = _routeStatus

    var detourModel: DetourModel? = null

    val routeDataModels = mutableListOf<RouteDataModel>()

    fun completeTechMap(techMap: TechMapModel) {
        routeDataModels.find { it.techMap == techMap }?.completed = true
        updateData()
    }

    fun setDetour(detour: DetourModel) {
        detourModel = detour
        routeDataModels.clear()
        routeDataModels.addAll(detour.route.routeData.sortedBy { it.position })
        updateData()
    }

    fun startNextRoute() {
        val route  = routeDataModels.firstOrNull() { !it.completed }
        route?.let { _navigateToNextRoute.value = Event(route) }
    }

    private fun updateData() {
        val completedPoints = routeDataModels.filter { it.completed }.count()
        val allPoints = routeDataModels.count()
        _routeStatus.value = when {
            allPoints == completedPoints -> {
                RouteStatus.COMPLETED
            }
            completedPoints == 0  -> {
                RouteStatus.NOT_STARTED
            }
            else -> {
                RouteStatus.IN_PROGRESS
            }
        }

        val routePoints = mutableListOf<DiffItem>().apply {
            routeDataModels.map { route ->
                add(
                    RoutePointUiModel(
                        id = route.techMap.id,
                        name = route.techMap.name.orEmpty(),
                        position = route.position,
                        completed = route.completed
                    )
                )
            }
        }
        _routePoints.value = routePoints
    }

    enum class RouteStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
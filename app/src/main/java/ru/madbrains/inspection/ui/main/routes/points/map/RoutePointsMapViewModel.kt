package ru.madbrains.inspection.ui.main.routes.points.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.MapLevelUiModel

class RoutePointsMapViewModel : BaseViewModel() {
    private val _mapLevels = MutableLiveData<List<MapLevelUiModel>>()
    val mapLevels: LiveData<List<MapLevelUiModel>> = _mapLevels

    private val _mapPoints = MutableLiveData<List<RouteDataModel>>()
    val mapPoints: LiveData<List<RouteDataModel>> = _mapPoints

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModel>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModel>> = _navigateToTechOperations

    private lateinit var detourModel: DetourModel

    fun setData(detour: DetourModel) {
        detourModel = detour
        val levels = detour.route.routeMaps?.mapIndexed { i, map ->
            MapLevelUiModel(map.id, map.name, map.url, i == 0)
        }.also {
            _mapLevels.value = it
        }
        filterMapPoints(levels?.find { it.isActive })
    }

    fun setActiveMap(map: MapLevelUiModel) {
        _mapLevels.value = _mapLevels.value?.map {
            MapLevelUiModel(it.id, it.name, it.url, map.id == it.id)
        }
        filterMapPoints(map)
    }

    private fun filterMapPoints(map: MapLevelUiModel?) {
        if (map == null) return

        _mapPoints.value = detourModel.route.routesData.filter { it.routeMapId == map.id }
    }

    fun routePointClick(routeData: RouteDataModel) {
        val routes = detourModel.route.routesData.sortedBy { it.position }
        val clickedIndex = routes.indexOf(routeData)
        val prevWasCompleted = if (clickedIndex > 0) routes[clickedIndex - 1].completed else false
        val preserveOrder = detourModel.saveOrderControlPoints == true

        if (!preserveOrder || routeData.completed || clickedIndex == 0 || prevWasCompleted) {
            routeData.techMap?.pointNumber = routeData.position
            _navigateToTechOperations.value = Event(routeData)
        }
    }
}
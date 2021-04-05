package ru.madbrains.inspection.ui.main.routes.points.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.RouteMapModel
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

    fun setData(detour: DetourModel) {
        _mapLevels.value = detour.route.routeMaps?.mapIndexed { i, map ->
            MapLevelUiModel(map.id, map.name, i == 0)
        }
        _mapPoints.value = detour.route.routesData
    }

    fun setActiveMap(map: MapLevelUiModel) {
        _mapLevels.value = _mapLevels.value?.map {
            MapLevelUiModel(it.id, it.name,map.id == it.id)
        }
    }

    fun routePointClick(routeData: RouteDataModel?) {
        routeData?.let {  _navigateToTechOperations.value = Event(it) }
    }
}
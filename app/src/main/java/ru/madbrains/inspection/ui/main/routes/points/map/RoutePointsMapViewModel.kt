package ru.madbrains.inspection.ui.main.routes.points.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.MapLevelUiModel
import java.io.File

class RoutePointsMapViewModel(
    private val detoursInteractor: DetoursInteractor
) : BaseViewModel() {
    private val _mapLevels = MutableLiveData<List<MapLevelUiModel>>()
    val mapLevels: LiveData<List<MapLevelUiModel>> = _mapLevels

    private val _mapPoints = MutableLiveData<List<RouteDataModel>>()
    val mapPoints: LiveData<List<RouteDataModel>> = _mapPoints

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModel>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModel>> = _navigateToTechOperations

    private val _mapImage = MutableLiveData<File>()
    val mapImage: LiveData<File> = _mapImage

    private lateinit var detourModel: DetourModel

    fun setData(detour: DetourModel) {
        detourModel = detour
        val levels = detour.route.routeMaps?.mapIndexed { i, map ->
            MapLevelUiModel(
                id = map.id,
                fileName = map.fileName,
                routeMapName = map.routeMapName,
                url = map.url,
                isActive = i == 0
            )
        }.also {
            if (it != null) _mapLevels.value = it
        }
        filterMapPoints(levels?.find { it.isActive })
    }

    fun setActiveMap(map: MapLevelUiModel) {
        _mapLevels.value = _mapLevels.value?.map {
            MapLevelUiModel(
                id = it.id,
                fileName = it.fileName,
                routeMapName = it.routeMapName,
                url = it.url,
                isActive = map.id == it.id
            )
        }
        filterMapPoints(map)
    }

    private fun filterMapPoints(map: MapLevelUiModel?) {
        if (map == null) return

        _mapPoints.value = detourModel.route.routesData?.filter { it.routeMapId == map.id }
    }

    fun routePointClick(routeData: RouteDataModel) {
        detourModel.route.routesData?.sortedBy { it.position }?.let {routes->
            val clickedIndex = routes.indexOf(routeData)
            val prevWasCompleted = if (clickedIndex > 0) routes[clickedIndex - 1].completed else false
            val preserveOrder = detourModel.saveOrderControlPoints == true

            if (!preserveOrder || routeData.completed || clickedIndex == 0 || prevWasCompleted) {
                _navigateToTechOperations.value = Event(routeData)
            }
        }
    }

    fun loadImage(item: MapLevelUiModel) {
        _mapImage.postValue(detoursInteractor.getFileInFolder(item.fileName, AppDirType.Docs))
    }
}
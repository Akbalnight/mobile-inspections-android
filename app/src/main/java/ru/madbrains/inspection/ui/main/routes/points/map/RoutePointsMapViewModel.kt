package ru.madbrains.inspection.ui.main.routes.points.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteMapModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.ui.delegates.MapLevelUiModel

class RoutePointsMapViewModel : BaseViewModel() {
    private val _mapLevels = MutableLiveData<List<MapLevelUiModel>>()
    val mapLevels: LiveData<List<MapLevelUiModel>> = _mapLevels

    fun setData(detour: DetourModel) {
        _mapLevels.value = detour.route.routeMaps?.mapIndexed { i, map ->
            MapLevelUiModel(map.id, i == 0)
        }
    }

    fun setTestData(list: List<RouteMapModel>) { // TODO Delete
        _mapLevels.value = list.mapIndexed { i, map ->
            MapLevelUiModel(map.id, i == 0)
        }
    }

    fun setActiveMap(map: MapLevelUiModel) {
        _mapLevels.value = _mapLevels.value?.map {
            MapLevelUiModel(it.id, map.id == it.id)
        }
    }
}
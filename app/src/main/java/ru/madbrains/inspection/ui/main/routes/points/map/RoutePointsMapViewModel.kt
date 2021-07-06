package ru.madbrains.inspection.ui.main.routes.points.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MapLevelUiModel
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel
import java.io.File

class RoutePointsMapViewModel(
    private val offlineInteractor: OfflineInteractor
) : BaseViewModel() {
    private val _mapLevels = MutableLiveData<List<MapLevelUiModel>>()
    val mapLevels: LiveData<List<MapLevelUiModel>> = _mapLevels

    private val _mapPoints = MutableLiveData<List<MapPointUiModel>>()
    val mapPoints: LiveData<List<MapPointUiModel>> = _mapPoints

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

        detourModel.route.routesData?.let { list ->
            updateData(list.filter { it.routeMapId == map.id })
        }
    }

    fun routePointClick(point: MapPointUiModel) {
        detourModel.route.routesData?.let { routes ->
            routes.find { it.id == point.routeId }?.let {
                _navigateToTechOperations.value = Event(it)
            }
        }
    }

    fun loadImage(item: MapLevelUiModel) {
        _mapImage.postValue(offlineInteractor.getFileInFolder(item.fileName, AppDirType.Docs))
    }

    private fun updateData(points: List<RouteDataModel>) {
        val deviceIds = points.fold(mutableListOf<String>(), { acc, a ->
            val ids = a.equipments?.map { it.id }
            if (ids != null) {
                acc.addAll(ids)
            }
            acc
        })

        offlineInteractor.getEquipmentIdsWithDefectsDB(equipmentIds = deviceIds)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ ids ->
                val defectsMap = ids.fold(mutableMapOf<String, Boolean>()) { acc, id ->
                    acc[id] = true
                    acc
                }
                applyDefectDataAndUpdate(points, defectsMap)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun applyDefectDataAndUpdate(
        points: List<RouteDataModel>,
        defectsMap: MutableMap<String, Boolean>
    ) {
        val mapPoints = mutableListOf<MapPointUiModel>()
        val lastCompleted = points.indexOfLast { it.completed }
        points.mapIndexed { index, route ->
            route.techMap?.let { techMap ->
                val current = lastCompleted + 1 == index
                val preserveOrder = detourModel.saveOrderControlPoints == true
                val haveDefects =
                    route.equipments?.fold(false, { acc, a -> acc || defectsMap[a.id] == true })
                        ?: false

                val status = when {
                    current -> MapPointStatus.Current
                    route.completed && !haveDefects -> MapPointStatus.Completed
                    route.completed && haveDefects -> MapPointStatus.CompletedWithDefects
                    else -> MapPointStatus.None
                }

                mapPoints.add(
                    MapPointUiModel(
                        techMapId = techMap.id,
                        routeId = route.id,
                        xLocation = route.xLocation,
                        yLocation = route.yLocation,
                        name = techMap.name.orEmpty(),
                        position = route.position,
                        status = status,
                        clickable = !preserveOrder || route.completed || current
                    )
                )
            }
        }
        _mapPoints.value = mapPoints
    }
}

data class MapPointUiModel(
    val techMapId: String,
    val routeId: String?,
    val name: String,
    val position: Int?,
    val status: MapPointStatus,
    val clickable: Boolean,
    val xLocation: Int?,
    val yLocation: Int?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RoutePointUiModel && techMapId == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

enum class MapPointStatus {
    None, Current, CompletedWithDefects, Completed
}
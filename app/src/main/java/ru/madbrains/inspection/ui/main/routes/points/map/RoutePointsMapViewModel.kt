package ru.madbrains.inspection.ui.main.routes.points.map

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.getAllEquipmentIds
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MapLevelUiModel
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel
import java.io.File

class RoutePointsMapViewModel(
    private val offlineInteractor: OfflineInteractor
) : BaseViewModel() {

    private val mapDataMutable = MapDataMutable()
    private val _mapData = MutableLiveData<MapData>()
    val mapData: LiveData<MapData> = _mapData

    private val _mapLevels = MutableLiveData<List<MapLevelUiModel>>()
    val mapLevels: LiveData<List<MapLevelUiModel>> = _mapLevels

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
            if (it != null) _mapLevels.postValue(it)
        }
        filterMapPoints(levels?.find { it.isActive })
    }

    fun setActiveMap(map: MapLevelUiModel) {
        _mapLevels.postValue(_mapLevels.value?.map {
            MapLevelUiModel(
                id = it.id,
                fileName = it.fileName,
                routeMapName = it.routeMapName,
                url = it.url,
                isActive = map.id == it.id
            )
        })
        filterMapPoints(map)
    }

    private fun filterMapPoints(map: MapLevelUiModel?) {
        if (map == null) return

        detourModel.route.routesDataSorted.let { list ->
            val currentId = detourModel.route.getCurrentRouteId()
            updateData(
                list.filter { it.routeMapId == map.id },
                currentId
            )
        }
    }

    fun routePointClick(point: MapPointUiModel) {
        detourModel.route.routesDataSorted.let { routes ->
            routes.find { it.id == point.routeId }?.let {
                _navigateToTechOperations.postValue(Event(it))
            }
        }
    }

    fun loadImage(item: MapLevelUiModel) {
        _mapImage.postValue(offlineInteractor.getFileInFolder(item.fileName, AppDirType.Docs))
    }

    private fun updateData(points: List<RouteDataModel>, currentId: String?) {
        offlineInteractor.getEquipmentIdsWithDefects(equipmentIds = points.getAllEquipmentIds())
            .observeOn(Schedulers.io())
            .subscribe({ equipmentWithDefect ->
                applyDefectDataAndUpdate(points, equipmentWithDefect, currentId)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun Set<String>.isRouteHaveDefect(route: RouteDataModel): Boolean {
        return route.equipments?.fold(false, { acc, a -> acc || contains(a.id) })
            ?: false
    }

    private fun applyDefectDataAndUpdate(
        points: List<RouteDataModel>,
        equipmentWithDefect: Set<String>,
        currentId: String?
    ) {
        val mapPoints = points.mapNotNull { route ->
            route.techMap?.let { techMap ->
                val isCurrent = currentId == route.id
                val preserveOrder = detourModel.saveOrderControlPoints == true
                val haveDefects = equipmentWithDefect.isRouteHaveDefect(route)

                val status = when {
                    isCurrent -> MapPointStatus.Current
                    route.completed && !haveDefects -> MapPointStatus.Completed
                    route.completed && haveDefects -> MapPointStatus.CompletedWithDefects
                    else -> MapPointStatus.None
                }

                MapPointUiModel(
                    techMapId = techMap.id,
                    routeId = route.id,
                    xLocation = route.xLocation,
                    yLocation = route.yLocation,
                    name = techMap.name.orEmpty(),
                    position = route.position,
                    status = status,
                    clickable = !preserveOrder || route.completed || isCurrent
                )
            }
        }
        setPoints(mapPoints)
    }

    fun setRectF(rectF: RectF) {
        mapDataMutable.rectF = rectF
        checkAndEmitMapData()
    }

    private fun setPoints(points: List<MapPointUiModel>) {
        mapDataMutable.points = points
        checkAndEmitMapData()
    }

    fun setBitmap(bitmap: Bitmap) {
        mapDataMutable.bitmap = bitmap
        checkAndEmitMapData()
    }

    private fun checkAndEmitMapData() {
        val bitmap = mapDataMutable.bitmap
        val rectF = mapDataMutable.rectF
        val points = mapDataMutable.points
        if (bitmap != null && points != null && rectF != null) {
            _mapData.postValue(MapData(bitmap, rectF, points))
        }
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

data class MapDataMutable(
    var bitmap: Bitmap? = null,
    var rectF: RectF? = null,
    var points: List<MapPointUiModel>? = null
)

data class MapData(
    val bitmap: Bitmap,
    val rectF: RectF,
    val points: List<MapPointUiModel>
)

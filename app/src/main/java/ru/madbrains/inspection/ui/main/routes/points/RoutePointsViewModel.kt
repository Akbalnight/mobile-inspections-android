package ru.madbrains.inspection.ui.main.routes.points

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel
import java.text.SimpleDateFormat
import java.util.*

class RoutePointsViewModel(
    private val routesInteractor: RoutesInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToNextRoute = MutableLiveData<Event<RouteDataModel>>()
    val navigateToNextRoute: LiveData<Event<RouteDataModel>> = _navigateToNextRoute

    private val _navigateToBack = MutableLiveData<Event<Unit>>()
    val navigateToBack: LiveData<Event<Unit>> = _navigateToBack

    private val _navigateToCloseDialog = MutableLiveData<Event<Unit>>()
    val navigateToCloseDialog: LiveData<Event<Unit>> = _navigateToCloseDialog

    private val _routePoints = MutableLiveData<List<DiffItem>>()
    val routePoints: LiveData<List<DiffItem>> = _routePoints

    private val _routeStatus = MutableLiveData<Event<RouteStatus>>()
    val routeStatus: LiveData<Event<RouteStatus>> = _routeStatus

    var detourModel: DetourModel? = null

    val routeDataModels = mutableListOf<RouteDataModel>()

    private var startTime: Long? = null

    fun completeTechMap(techMap: TechMapModel) {
        routeDataModels.find { it.techMap == techMap }?.completed = true
        updateData()
    }

    @SuppressLint("SimpleDateFormat")
    fun finishDetour(statusId: String) {
        detourModel?.let { detour ->

            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

            val startTime = detour.startTime?.let { format.format(it) }.orEmpty()
            val finishTime = format.format(Date())

            detour.dateStartFact = startTime
            detour.dateFinishFact = finishTime
            detour.statusId = statusId

            routesInteractor.saveDetour(detour)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({
                    onBack()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

    fun onBack() {
        _navigateToBack.value = Event(Unit)
    }

    fun closeClick() {
        if (routeDataModels.all { it.completed } || detourModel?.statusId == DetourStatus.COMPLETED.id) {
            onBack()
        } else {
            _navigateToCloseDialog.value = Event(Unit)
        }
    }

    fun setDetour(detour: DetourModel) {
        detourModel = detour
        startTime = null
        routeDataModels.clear()
        routeDataModels.addAll(detour.route.routesData.sortedBy { it.position })
        updateData()
    }

    fun startRoute() {
        detourModel?.startTime = Date()
        val route = routeDataModels.firstOrNull() { !it.completed }
        route?.let { _navigateToNextRoute.value = Event(route) }
    }

    fun startNextRoute() {
        val route = routeDataModels.firstOrNull() { !it.completed }
        route?.let { _navigateToNextRoute.value = Event(route) }
    }

    private fun updateData() {
        setRouteStatus()
        val routePoints = mutableListOf<DiffItem>().apply {
            routeDataModels.mapIndexed { index, route ->
                val prevWasCompletedOrFirst = if(index > 0) routeDataModels[index].completed else true
                val preserveOrder = detourModel?.saveOrderControlPoints == true
                route.techMap?.let {
                    add(
                        RoutePointUiModel(
                            id = it.id,
                            name = it.name.orEmpty(),
                            position = route.position,
                            completed = route.completed,
                            clickable = !preserveOrder || prevWasCompletedOrFirst
                        )
                    )
                }
            }
        }
        _routePoints.value = routePoints
    }

    private fun setRouteStatus() {
        if (detourModel?.statusId == DetourStatus.COMPLETED.id) return
        val completedPoints = routeDataModels.filter { it.completed }.count()
        val allPoints = routeDataModels.count()
        _routeStatus.value = when {
            allPoints == completedPoints -> {
                Event(RouteStatus.COMPLETED)
            }
            completedPoints == 0 -> {
                Event(RouteStatus.NOT_STARTED)
            }
            else -> {
                Event(RouteStatus.IN_PROGRESS)
            }
        }
    }

    enum class RouteStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
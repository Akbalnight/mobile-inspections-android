package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.domain.model.*
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel
import java.util.*
import java.util.concurrent.TimeUnit

class RoutePointsViewModel(
    private val syncInteractor: SyncInteractor,
    private val offlineInteractor: OfflineInteractor,
    preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigatePop = MutableLiveData<Event<Unit>>()
    val navigatePop: LiveData<Event<Unit>> = _navigatePop

    private val _navigateToCloseDialog = MutableLiveData<Event<Unit>>()
    val navigateToCloseDialog: LiveData<Event<Unit>> = _navigateToCloseDialog

    private val _navigateToFinishDialog = MutableLiveData<Event<Unit>>()
    val navigateToFinishDialog: LiveData<Event<Unit>> = _navigateToFinishDialog

    private val _routePoints = MutableLiveData<List<DiffItem>>()
    val routePoints: LiveData<List<DiffItem>> = _routePoints

    private val _routeActionStatus = MutableLiveData<RouteStatus>()
    val routeActionStatus: LiveData<RouteStatus> = _routeActionStatus

    private var _durationTimer = MutableLiveData<Long?>(null)
    val durationTimer: LiveData<Long?> = _durationTimer

    var detourModel: DetourModel? = null
        private set

    private val routeDataModels get() = detourModel?.route?.routesDataSorted ?: listOf()

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModelWithDetourId>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModelWithDetourId>> =
        _navigateToTechOperations

    private val detourStatuses: DetourStatusHolder = preferenceStorage.detourStatuses
    private val isRouteNew get():Boolean = detourStatuses.isNew(detourModel?.statusId)
    private val isRouteCompleted get():Boolean = detourStatuses.isCompleted(detourModel?.statusId)
    val isRouteStarted get():Boolean = !isRouteNew && !isRouteCompleted

    val dateStartFact get():Date? = if (isRouteNew) null else detourModel?.dateStartFact
    val dateFinishFact get():Date? = if (isRouteNew) null else detourModel?.dateFinishFact

    private val allPointsCompleted get() = routeDataModels.isNotEmpty() && routeDataModels.all { it.completed }

    private var timerDisposable: Disposable? = null

    fun init(detour: DetourModel) {
        detourModel = detour
        refreshData()
    }

    private fun startTimer() {
        timerDisposable?.dispose()
        timerDisposable = Observable.timer(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .repeat()
            .observeOn(Schedulers.io())
            .doOnSubscribe {
                postDurationTime(Date())
            }
            .subscribe({
                postDurationTime(Date())
            }, {
                it.printStackTrace()
            })
    }

    private fun postDurationTime(endTime: Date) {
        val startTime = dateStartFact
        if (startTime != null) {
            val time = (endTime.time - startTime.time) / 1000
            _durationTimer.postValue(time)
        }
    }

    fun routePointClick(routeDataId: String?) {
        routeDataModels.find { it.id == routeDataId }?.let { routeData ->
            navigateToTechOperation(routeData)
        }
    }

    fun startDetour() {
        detourModel = detourModel?.copy(
            dateStartFact = Date(),
            dateFinishFact = null,
            statusId = detourStatuses.getStatusByType(DetourStatusType.IN_PROGRESS)?.id
        )?.saveChangesToDb()
        setRouteActionStatus()
        startNextRoute()
    }

    fun startNextRoute() {
        routeDataModels.firstOrNull { !it.completed }?.let {
            navigateToTechOperation(it)
        }
    }

    private fun navigateToTechOperation(routeData: RouteDataModel) {
        detourModel?.let { detour ->
            _navigateToTechOperations.postValue(
                Event(
                    RouteDataModelWithDetourId(
                        detour.id,
                        routeData
                    )
                )
            )
        }
    }

    fun completeTechMap(item: RouteDataModel) {
        val model = detourModel
        model?.run {
            route.routesDataSorted.toMutableList().let { list ->
                val index = list.indexOfFirst { item.id == it.id }
                if (index > -1) {
                    detourModel = copy(
                        route = route.copy(
                            routesData = list.apply {
                                this[index] = item.copy(completed = true)
                            }
                        )
                    ).saveChangesToDb()
                    refreshData()
                    if (routeDataModels.all { it.completed }) {
                        _navigateToFinishDialog.postValue(Event(Unit))
                    }
                }
            }
        }
    }

    fun finishDetourAndSave(type: DetourStatusType) {
        val currentStatus = detourStatuses.getStatusByType(type)
        detourModel?.copy(
            dateFinishFact = Date(),
            statusId = currentStatus?.id,
            changed = true
        )?.saveChangesToDb {
            navigatePop()
        }
    }

    private fun DetourModel.saveChangesToDb(callback: (() -> Unit)? = null): DetourModel {
        val data = this.copy(changed = true)
        syncInteractor.insertDetour(data)
            .observeOn(Schedulers.io())
            .andThen(offlineInteractor.getDetoursAndRefreshSource())
            .subscribe({
                callback?.invoke()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
        return data
    }

    private fun navigatePop() {
        _navigatePop.postValue(Event(Unit))
    }

    fun closeClick() {
        if (isRouteStarted) {
            _navigateToCloseDialog.postValue(Event(Unit))
        } else {
            navigatePop()
        }
    }

    fun refreshData() {
        setRouteActionStatus()
        detourModel?.let { detour ->
            offlineInteractor.getRoutesWithDefectCount(detour.id, detour.route.routesDataSorted)
                .observeOn(Schedulers.io())
                .subscribe({ data ->
                    val currentId = detour.route.getCurrentRouteId()
                    applyDefectData(data, currentId)
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)

        }
    }

    private fun applyDefectData(routes: List<RouteDataWithDefectCount>, currentId: String?) {
        val routePoints = routes.mapNotNull { data ->
            val route = data.route
            route.techMap?.let { techMap ->
                val current = route.id == currentId
                val preserveOrder = detourModel?.saveOrderControlPoints == true
                RoutePointUiModel(
                    id = techMap.id,
                    routeDataId = route.id,
                    name = techMap.name.orEmpty(),
                    position = route.position,
                    completed = route.completed,
                    clickable = !preserveOrder || route.completed || current,
                    defectCount = data.defectCount
                )
            }
        }
        _routePoints.postValue(routePoints)
    }

    private fun setRouteActionStatus() {
        _routeActionStatus.postValue(
            when {
                allPointsCompleted && !isRouteCompleted -> {
                    RouteStatus.FINISHED_NOT_COMPLETED
                }
                allPointsCompleted && isRouteCompleted -> {
                    RouteStatus.COMPLETED
                }
                !isRouteStarted -> {
                    RouteStatus.NOT_STARTED
                }
                else -> {
                    RouteStatus.IN_PROGRESS
                }
            }
        )
        if (isRouteStarted) {
            startTimer()
        } else {
            val dateFinishFact = dateFinishFact
            if (dateFinishFact != null) {
                postDurationTime(dateFinishFact)
            }
            timerDisposable?.dispose()
            timerDisposable = null
        }
    }

    fun doClean() {
        _routePoints.postValue(null)
        _routeActionStatus.postValue(null)
        _durationTimer.postValue(null)
        detourModel = null
        disposables.clear()
        timerDisposable?.dispose()
        timerDisposable = null
    }

    enum class RouteStatus {
        NOT_STARTED,
        IN_PROGRESS,
        FINISHED_NOT_COMPLETED,
        COMPLETED
    }
}
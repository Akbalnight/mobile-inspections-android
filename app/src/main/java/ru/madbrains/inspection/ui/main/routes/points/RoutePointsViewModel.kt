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
    private val preferenceStorage: PreferenceStorage
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

    private val _routeStatus = MutableLiveData<RouteStatus>()
    val routeStatus: LiveData<RouteStatus> = _routeStatus

    private var _durationTimer = MutableLiveData<Long?>(null)
    val durationTimer: LiveData<Long?> = _durationTimer

    var detourModel: DetourModel? = null
        private set

    private var timerDispose: Disposable? = null
    private val routeDataModels get() = detourModel?.route?.routesDataSorted ?: listOf()

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModelWithDetourId>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModelWithDetourId>> =
        _navigateToTechOperations

    val timerStarted get():Boolean = timerDispose != null

    fun routePointClick(routeDataId: String?) {
        routeDataModels.find { it.id == routeDataId }?.let { routeData ->
            navigateToTechOperation(routeData)
        }
    }

    fun startNextRoute() {
        triggerTimer()
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

    fun isDetourEditable(): Boolean {
        return preferenceStorage.detourStatuses?.data?.isEditable(detourModel?.statusId) == true && timerStarted
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
        detourModel?.let { detour ->
            val currentStatus = preferenceStorage.detourStatuses?.data?.getStatusByType(type)
            syncInteractor.insertDetour(
                detour.copy(
                    dateFinishFact = Date(),
                    statusId = currentStatus?.id,
                    changed = true
                )
            )
                .andThen(offlineInteractor.getDetoursAndRefreshSource())
                .observeOn(Schedulers.io())
                .doOnSubscribe { _progressVisibility.postValue(true) }
                .doAfterTerminate { _progressVisibility.postValue(false) }
                .subscribe({
                    navigatePop()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
        }
    }

    private fun DetourModel.saveChangesToDb(): DetourModel {
        val data = this.copy(changed = true)
        syncInteractor.insertDetour(data)
            .observeOn(Schedulers.io())
            .subscribe({
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
        val status =
            preferenceStorage.detourStatuses?.data?.getStatusById(detourModel?.statusId)?.type
        if (routeDataModels.all { it.completed } || status == DetourStatusType.COMPLETED ||
            detourModel?.dateStartFact == null) {
            navigatePop()
        } else {
            _navigateToCloseDialog.postValue(Event(Unit))
        }
    }

    fun setNavData(detour: DetourModel) {
        detourModel = detour
        refreshData()
    }

    fun refreshData() {
        setRouteStatus()
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

    private fun setRouteStatus() {
        val type =
            preferenceStorage.detourStatuses?.data?.getStatusById(detourModel?.statusId)?.type
        val completed =
            type == DetourStatusType.COMPLETED || type == DetourStatusType.COMPLETED_AHEAD
        val completedPoints = routeDataModels.filter { it.completed }.count()
        val allPoints = routeDataModels.count()
        val allPointsCompleted = allPoints == completedPoints
        _routeStatus.postValue(
            when {
                allPointsCompleted && !completed -> {
                    RouteStatus.FINISHED_NOT_COMPLETED
                }
                allPointsCompleted && completed -> {
                    RouteStatus.COMPLETED
                }
                completedPoints == 0 -> {
                    RouteStatus.NOT_STARTED
                }
                else -> {
                    RouteStatus.IN_PROGRESS
                }
            }
        )
    }

    private fun triggerTimer() {
        val startTime = detourModel?.dateStartFact ?: Date()
        detourModel = detourModel?.copy(dateStartFact = startTime)?.saveChangesToDb()
        if (timerDispose == null)
            timerDispose = Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .repeat()
                .observeOn(Schedulers.io())
                .doOnSubscribe {
                    _durationTimer.postValue(0L)
                }
                .doOnDispose {
                    _durationTimer.postValue(null)
                }
                .subscribe({
                    _durationTimer.postValue((Date().time - startTime.time) / 1000)
                }, {
                    it.printStackTrace()
                })
    }

    private fun stopTimer() {
        if (_durationTimer.value != null) {
            timerDispose?.dispose()
            timerDispose = null
        }
    }

    fun doClean() {
        stopTimer()
        _routePoints.postValue(null)
        _routeStatus.postValue(null)
        _durationTimer.postValue(null)
        _durationTimer.postValue(null)
        detourModel = null
    }

    enum class RouteStatus {
        NOT_STARTED,
        IN_PROGRESS,
        FINISHED_NOT_COMPLETED,
        COMPLETED
    }
}
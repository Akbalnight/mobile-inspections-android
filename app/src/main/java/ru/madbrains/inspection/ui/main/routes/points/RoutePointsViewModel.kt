package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class RoutePointsViewModel(
    private val syncInteractor: SyncInteractor,
    private val offlineInteractor: OfflineInteractor,
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToNextRoute = MutableLiveData<Event<RouteDataModel>>()
    val navigateToNextRoute: LiveData<Event<RouteDataModel>> = _navigateToNextRoute

    private val _navigateToBack = MutableLiveData<Event<Unit>>()
    val navigateToBack: LiveData<Event<Unit>> = _navigateToBack

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
    private val routeDataModels
        get() = detourModel?.route?.routesData?.sortedBy { it.position } ?: emptyList()

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModel>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModel>> = _navigateToTechOperations

    private val _navigateToDefectList = MutableLiveData<Event<Boolean>>()
    val navigateToDefectList: LiveData<Event<Boolean>> = _navigateToDefectList

    private val timerStarted get():Boolean = timerDispose != null

    fun routePointClick(id: String?) {
        routeDataModels.find { data ->
            data.id == id
        }?.let { _navigateToTechOperations.value = Event(it) }
    }

    fun isDetourEditable(): Boolean {
        return preferenceStorage.detourStatuses?.data?.isEditable(detourModel?.statusId) == true && timerStarted
    }

    fun navigateToDefectList() {
        _navigateToDefectList.value = Event(timerStarted)
    }

    fun completeTechMap(item: RouteDataModel) {
        detourModel?.let { model ->
            val index = routeDataModels.indexOfFirst { item.id == it.id }
            if (index > -1) {
                detourModel = model.copy(
                    route = model.route.copy(
                        routesData = model.route.routesData?.toMutableList()
                            ?.apply { this[index] = item.copy(completed = true) }
                    )
                )
                updateData()
                if (routeDataModels.all { it.completed }) {
                    _navigateToFinishDialog.value = Event(Unit)
                }

            }
        }
    }

    fun finishDetourAndSave(type: DetourStatusType) {
        stopTimer()
        detourModel?.let { detour ->
            val currentStatus = preferenceStorage.detourStatuses?.data?.getStatusByType(type)
            syncInteractor.insertDetour(
                detour.copy(
                    dateStartFact = detour.startTime,
                    dateFinishFact = Date(),
                    statusId = currentStatus?.id,
                    changed = true
                )
            )
                .andThen(offlineInteractor.getDetoursAndRefreshSource())
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

    private fun onBack() {
        _navigateToBack.value = Event(Unit)
    }

    fun closeClick() {
        val status =
            preferenceStorage.detourStatuses?.data?.getStatusById(detourModel?.statusId)?.type
        if (routeDataModels.all { it.completed } || status == DetourStatusType.COMPLETED ||
            detourModel?.startTime == null) {
            onBack()
        } else {
            _navigateToCloseDialog.value = Event(Unit)
        }
    }

    fun setDetour(detour: DetourModel) {
        Timber.d("debug_dmm setDetour")
        stopTimer()
        detourModel = detour
        updateData()
    }

    fun startNextRoute() {
        triggerTimer()
        val route = routeDataModels.firstOrNull { !it.completed }
        route?.let {
            _navigateToNextRoute.value = Event(route)
        }
    }

    private fun updateData() {
        Timber.d("debug_dmm updateData")
        setRouteStatus()
        val routePoints = mutableListOf<DiffItem>()
        val lastCompleted = routeDataModels.indexOfLast { it.completed }
        routeDataModels.forEachIndexed { index, route ->
            route.techMap?.let { techMap ->
                val current = lastCompleted + 1 == index
                val preserveOrder = detourModel?.saveOrderControlPoints == true
                routePoints.add(
                    RoutePointUiModel(
                        id = techMap.id,
                        parentId = route.id,
                        name = techMap.name.orEmpty(),
                        position = route.position,
                        completed = route.completed,
                        clickable = !preserveOrder || route.completed || current
                    )
                )
            }
        }
        _routePoints.value = routePoints
    }

    private fun setRouteStatus() {
        val type =
            preferenceStorage.detourStatuses?.data?.getStatusById(detourModel?.statusId)?.type
        val completed =
            type == DetourStatusType.COMPLETED || type == DetourStatusType.COMPLETED_AHEAD
        val completedPoints = routeDataModels.filter { it.completed }.count()
        val allPoints = routeDataModels.count()
        val allPointsCompleted = allPoints == completedPoints
        _routeStatus.value = when {
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
    }

    private fun triggerTimer() {
        val startTime = detourModel?.startTime ?: Date()
        detourModel = detourModel?.copy(startTime = startTime)
        if (timerDispose == null)
            timerDispose = Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    _durationTimer.value = 0L
                }
                .doOnDispose {
                    _durationTimer.value = null
                }
                .subscribe({
                    _durationTimer.value = (Date().time - startTime.time) / 1000
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

    enum class RouteStatus {
        NOT_STARTED,
        IN_PROGRESS,
        FINISHED_NOT_COMPLETED,
        COMPLETED
    }
}
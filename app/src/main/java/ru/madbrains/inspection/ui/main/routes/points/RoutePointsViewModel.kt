package ru.madbrains.inspection.ui.main.routes.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.data.extensions.toyyyyMMddTHHmmssXXX
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.*
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RoutePointUiModel
import java.util.*
import java.util.concurrent.TimeUnit

class RoutePointsViewModel(
    private val detoursInteractor: DetoursInteractor,
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

    private val _routeStatus = MutableLiveData<Event<RouteStatus>>()
    val routeStatus: LiveData<Event<RouteStatus>> = _routeStatus

    private var _durationTimer = MutableLiveData<Long?>(null)
    val durationTimer: LiveData<Long?> = _durationTimer

    var detourModel: DetourModel? = null
    lateinit var timerDispose: Disposable
    val routeDataModels
        get() = detourModel?.route?.routesData?.sortedBy { it.position } ?: emptyList()

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModel>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModel>> = _navigateToTechOperations

    fun routePointClick(id: String?) {
        routeDataModels.find { data ->
            data.id == id
        }?.let { _navigateToTechOperations.value = Event(it) }
    }

    private var startTime: Long? = null

    fun isDetourEditable(): Boolean {
        return preferenceStorage.detourStatuses?.data?.isEditable(detourModel?.statusId) == true
    }

    fun completeTechMap(route: RouteDataModel) {
        route.completed = true
        updateData()

        if (routeDataModels.all { it.completed }) {
            _navigateToFinishDialog.value = Event(Unit)
        }
    }

    fun finishDetourAndSave(type: DetourStatusType) {
        stopTimer()
        detourModel?.let { detour ->
            val currentStatus = preferenceStorage.detourStatuses?.data?.getStatusByType(type)
            detour.dateStartFact = detour.startTime?.toyyyyMMddTHHmmssXXX()
            detour.dateFinishFact = Date().toyyyyMMddTHHmmssXXX()
            detour.statusId = currentStatus?.id
            detour.changed = true
            detoursInteractor.updateDetourDB(detour)
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
        if (routeDataModels.all { it.completed } ||
            preferenceStorage.detourStatuses?.data?.getStatusById(detourModel?.statusId)?.type == DetourStatusType.COMPLETED ||
            detourModel?.startTime == null) {
            onBack()
        } else {
            _navigateToCloseDialog.value = Event(Unit)
        }
    }

    fun setDetour(detour: DetourModel) {
        detourModel = detour
        startTime = null
        updateData()
    }

    fun startRoute() {
        detourModel?.startTime = Date()
        startTimer()
        val route = routeDataModels.firstOrNull { !it.completed }
        route?.let { _navigateToNextRoute.value = Event(route) }
    }

    fun startNextRoute() {
        val route = routeDataModels.firstOrNull { !it.completed }
        route?.let { _navigateToNextRoute.value = Event(route) }
    }

    private fun updateData() {
        setRouteStatus()
        val routePoints = mutableListOf<DiffItem>()
        routeDataModels.mapIndexed { index, route ->
            val prevWasCompleted = if (index > 0) routeDataModels[index - 1].completed else false
            val preserveOrder = detourModel?.saveOrderControlPoints == true
            route.techMap?.let {
                routePoints.add(
                    RoutePointUiModel(
                        id = it.id,
                        parentId = route.id,
                        name = it.name.orEmpty(),
                        position = route.position,
                        completed = route.completed,
                        clickable = !preserveOrder || route.completed || index == 0 || prevWasCompleted
                    )
                )
            }
        }
        _routePoints.value = routePoints
    }

    private fun setRouteStatus() {
        val type =
            preferenceStorage.detourStatuses?.data?.getStatusById(detourModel?.statusId)?.type
        if (type == DetourStatusType.COMPLETED || type == DetourStatusType.COMPLETED_AHEAD) return
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

    private fun startTimer() {
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
                _durationTimer.value = _durationTimer.value?.plus(1L)
            }, {
                it.printStackTrace()
            })
    }

    private fun stopTimer() {
        if (_durationTimer.value != null) {
            timerDispose.dispose()
        }
    }

    enum class RouteStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
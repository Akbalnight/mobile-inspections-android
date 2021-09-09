package ru.madbrains.inspection.ui.main.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.model.*
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DetourUiModel

class DetoursViewModel(
    offlineInteractor: OfflineInteractor,
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _navigateToDateRoutePoints = MutableLiveData<Event<DetourModel>>()
    val navigateToDateRoutePoints: LiveData<Event<DetourModel>> = _navigateToDateRoutePoints

    private val _navigateToRoutePoints = MutableLiveData<Event<DetourModel>>()
    val navigateToRoutePoints: LiveData<Event<DetourModel>> = _navigateToRoutePoints

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _detours = MutableLiveData<List<DiffItem>>()
    val detours: LiveData<List<DiffItem>> = _detours

    private val detourModels = mutableListOf<DetourModel>()

    private var savedFilter: DetourStatus? = null

    init {
        offlineInteractor.detoursSource
            .observeOn(Schedulers.io())
            .doOnNext { routes ->
                detourModels.clear()
                detourModels.addAll(routes)
                updateData(detourModels, savedFilter)
            }
            .subscribe({}, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun getStatusesForFiltration(): List<String> {
        return preferenceStorage.detourStatuses?.data?.getStatusesByType(
            arrayOf(
                DetourStatusType.PAUSED,
                DetourStatusType.NEW
            )
        )?.map { it.id } ?: arrayListOf()
    }

    private fun updateData(_routes: List<DetourModel>, filter: DetourStatus?) {
        val routes = if (filter != null) {
            _routes.filter { it.statusId == filter.id }
        } else _routes
        val uiData = routes.map {
            DetourUiModel(
                id = it.id,
                name = it.name.orEmpty(),
                status = preferenceStorage.detourStatuses?.data?.getStatusById(it.statusId),
                dateStartPlan = it.dateStartPlan
            )
        }
        _detours.postValue(uiData)
    }

    fun saveFilter(filter: DetourStatus?) {
        savedFilter = filter
        updateData(detourModels, filter)
    }

    fun dateRouteClick(id: String) {
        val detour = detourModels.find { detourModel -> detourModel.id == id }
        detour?.let { _navigateToDateRoutePoints.postValue(Event(it)) }
    }

    fun routeClick(id: String) {
        val detour = detourModels.find { detourModel -> detourModel.id == id }
        detour?.let { _navigateToRoutePoints.postValue(Event(it)) }
    }
}
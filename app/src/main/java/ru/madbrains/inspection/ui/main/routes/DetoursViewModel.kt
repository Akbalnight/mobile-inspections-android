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
import timber.log.Timber

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

    private val detourModels = mutableListOf<DetourModelWithDefectCount>()

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

    private fun updateData(routes: List<DetourModelWithDefectCount>, _filter: DetourStatus?) {
        val detours = mutableListOf<DiffItem>()
        _filter?.let { filter ->
            val filteredModels = filter.let { routes.filter { it.data.statusId == filter.id } }
            filteredModels.map { detour ->
                detours.add(
                    DetourUiModel(
                        id = detour.data.id,
                        name = detour.data.name.orEmpty(),
                        status = preferenceStorage.detourStatuses?.data?.getStatusById(detour.data.statusId),
                        dateStartPlan = detour.data.dateStartPlan
                    )
                )
            }
        } ?: run {
            routes.map { detour ->
                Timber.d("debug_dmm detour.defectCount ${detour.defectCount}")
                detours.add(
                    DetourUiModel(
                        id = detour.data.id,
                        name = detour.data.name.orEmpty(),
                        status = preferenceStorage.detourStatuses?.data?.getStatusById(detour.data.statusId),
                        dateStartPlan = detour.data.dateStartPlan
                    )
                )
            }
        }
        _detours.postValue(detours)
    }

    fun saveFilter(filter: DetourStatus?) {
        savedFilter = filter
        updateData(detourModels, filter)
    }

    fun dateRouteClick(id: String) {
        val detour = detourModels.find { detourModel -> detourModel.data.id == id }
        detour?.let { _navigateToDateRoutePoints.postValue(Event(it.data)) }
    }

    fun routeClick(id: String) {
        val detour = detourModels.find { detourModel -> detourModel.data.id == id }
        detour?.let { _navigateToRoutePoints.postValue(Event(it.data)) }
    }
}
package ru.madbrains.inspection.ui.main.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
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

    private var savedStatus: DetourStatus? = null

    init {
        offlineInteractor.detoursSource
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { routes ->
                detourModels.clear()
                detourModels.addAll(routes)
                updateData(detourModels, savedStatus)
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

    private fun updateData(models: List<DetourModel>, status: DetourStatus?) {
        val detours = mutableListOf<DiffItem>().apply {
            status?.let { status ->
                val filteredModels = status.let { models.filter { it.statusId == status.id } }
                filteredModels.map { detour ->
                    add(
                        DetourUiModel(
                            id = detour.id,
                            name = detour.name.orEmpty(),
                            status = preferenceStorage.detourStatuses?.data?.getStatusById(detour.statusId),
                            date = detour.dateStartPlan.orEmpty()
                        )
                    )
                }
            } ?: run {
                models.map { detour ->
                    add(
                        DetourUiModel(
                            id = detour.id,
                            name = detour.name.orEmpty(),
                            status = preferenceStorage.detourStatuses?.data?.getStatusById(detour.statusId),
                            date = detour.dateStartPlan.orEmpty()
                        )
                    )
                }
            }
        }
        _detours.postValue(detours)
    }

    fun saveFilter(status: DetourStatus?) {
        savedStatus = status
        updateData(detourModels, status)
    }

    fun dateRouteClick(id: String) {
        val detour = detourModels.find { detourModel -> detourModel.id == id }
        detour?.let { _navigateToDateRoutePoints.value = Event(it) }
    }

    fun routeClick(id: String) {
        val detour = detourModels.find { detourModel -> detourModel.id == id }
        detour?.let { _navigateToRoutePoints.value = Event(it) }
    }
}
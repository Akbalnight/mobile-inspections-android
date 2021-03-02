package ru.madbrains.inspection.ui.main.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DetourUiModel

class DetoursViewModel(
    private val routesInteractor: RoutesInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _detours = MutableLiveData<List<DiffItem>>()
    val detours: LiveData<List<DiffItem>> = _detours

    val detourModels = mutableListOf<DetourModel>()

    fun getDetours() {
        routesInteractor.getDetours()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .flatMapCompletable { routes ->
                detourModels.clear()
                detourModels.addAll(
                    routes
                        .filter { it.statusId != null }
                        .filter { it.route.routesData.all { it.techMap != null } }
                        .filter { it.route.routesData.all { it.equipments != null } }
                )
                routesInteractor.freezeDetours(detourModels
                    .filter { it.frozen != true }
                    .map { it.id }
                )
            }
            .subscribe({
                updateData()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun updateData(status: DetourStatus? = null) {
        val detours = mutableListOf<DiffItem>().apply {
            val models = detourModels
            status?.let {
                val filteredModels = status.let { models.filter { it.statusId == status.id } }
                filteredModels.map { detour ->
                    add(
                        DetourUiModel(
                            id = detour.id,
                            name = detour.name.orEmpty(),
                            status = DetourStatus.values().find { it.id == detour.statusId },
                            date = detour.dateStartPlan.orEmpty()
                        )
                    )
                }
            } ?: run {
                detourModels.map { detour ->
                    add(
                        DetourUiModel(
                            id = detour.id,
                            name = detour.name.orEmpty(),
                            status = DetourStatus.values().find { it.id == detour.statusId },
                            date = detour.dateStartPlan.orEmpty()
                        )
                    )
                }
            }
        }
        _detours.postValue(detours)
    }
}
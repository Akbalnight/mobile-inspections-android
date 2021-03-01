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
            .subscribe({ routes ->
                detourModels.addAll(
                    routes
                        .filter { it.status != null }
                        .filter { it.route.routeData.all { it.techMap != null } }
                        .filter { it.route.routeData.all { it.equipments != null } }
                )
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
                val filteredModels = status.let { models.filter { it.status == status } }
                filteredModels.map { detour ->
                    add(
                        DetourUiModel(
                            id = detour.id,
                            name = detour.name.orEmpty(),
                            status = detour.status,
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
                            status = detour.status,
                            date = detour.dateStartPlan.orEmpty()
                        )
                    )
                }
            }
        }
        _detours.value = detours
    }
}
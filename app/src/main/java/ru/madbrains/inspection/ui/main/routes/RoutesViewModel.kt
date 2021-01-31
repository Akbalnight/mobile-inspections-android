package ru.madbrains.inspection.ui.main.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RouteStatus
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RouteUiModel

class RoutesViewModel(
    private val routesInteractor: RoutesInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _routes = MutableLiveData<List<DiffItem>>()
    val routes: LiveData<List<DiffItem>> = _routes

    private val routeModels = mutableListOf<RouteModel>()

    fun getRoutes() {
        routesInteractor.getRoutes()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ routes ->
                routeModels.addAll(routes.filter { it.status != null })
                updateData()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun updateData(status: RouteStatus? = null) {
        val routes = mutableListOf<DiffItem>().apply {
            val models = routeModels
            status?.let {
                val filteredModels = status.let { models.filter { it.status == status } }
                filteredModels.map { route ->
                    add(
                        RouteUiModel(
                            id = route.id,
                            name = route.name.orEmpty(),
                            status = route.status,
                            date = route.dateStartPlan.orEmpty()
                        )
                    )
                }
            } ?: run {
                routeModels.map { route ->
                    add(
                        RouteUiModel(
                            id = route.id,
                            name = route.name.orEmpty(),
                            status = route.status,
                            date = route.dateStartPlan.orEmpty()
                        )
                    )
                }
            }
        }
        _routes.value = routes
    }
}
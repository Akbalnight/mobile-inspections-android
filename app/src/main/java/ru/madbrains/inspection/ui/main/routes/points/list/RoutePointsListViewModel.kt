package ru.madbrains.inspection.ui.main.routes.points.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class RoutePointsListViewModel : BaseViewModel() {

    private val _navigateToTechOperations = MutableLiveData<Event<RouteDataModel>>()
    val navigateToTechOperations: LiveData<Event<RouteDataModel>> = _navigateToTechOperations

    fun routePointClick(routeData: RouteDataModel?) {
        routeData?.let {  _navigateToTechOperations.value = Event(it) }
    }
}
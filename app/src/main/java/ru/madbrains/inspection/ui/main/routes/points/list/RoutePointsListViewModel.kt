package ru.madbrains.inspection.ui.main.routes.points.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class RoutePointsListViewModel : BaseViewModel() {

    private val _navigateToTechOperations = MutableLiveData<Event<RoutePointModel>>()
    val navigateToTechOperations: LiveData<Event<RoutePointModel>> = _navigateToTechOperations

    fun routePointClick(point: RoutePointModel?) {
        point?.let {  _navigateToTechOperations.value = Event(it) }
    }
}
package ru.madbrains.inspection.ui.main.routes.dateroutelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class DateRouteListViewModel(

) : BaseViewModel() {

    private val _navigateToRoutePoints = MutableLiveData<Event<RouteModel>>()
    val navigateToRoutePoints: LiveData<Event<RouteModel>> = _navigateToRoutePoints

    fun routeClick(route: RouteModel?) {
        route?.let {  _navigateToRoutePoints.value = Event(it) }
    }
}
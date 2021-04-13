package ru.madbrains.inspection.ui.main.routes.routelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.data.utils.RfidListener
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class RouteListViewModel() : BaseViewModel() {

    private val _navigateToRoutePoints = MutableLiveData<Event<DetourModel>>()
    val navigateToRoutePoints: LiveData<Event<DetourModel>> = _navigateToRoutePoints

    fun routeClick(route: DetourModel?) {
        route?.let {  _navigateToRoutePoints.value = Event(it) }
    }
}
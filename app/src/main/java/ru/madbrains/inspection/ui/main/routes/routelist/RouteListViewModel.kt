package ru.madbrains.inspection.ui.main.routes.routelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.data.utils.RfidListener
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class RouteListViewModel(
    private val rfidDevice: RfidDevice
) : BaseViewModel() {

    private val _navigateToRoutePoints = MutableLiveData<Event<DetourModel>>()
    val navigateToRoutePoints: LiveData<Event<DetourModel>> = _navigateToRoutePoints
    private val _rfidDataReceiver = MutableLiveData<Event<String>>()
    val rfidDataReceiver: LiveData<Event<String>> = _rfidDataReceiver

    fun routeClick(route: DetourModel?) {
        route?.let {  _navigateToRoutePoints.value = Event(it) }
    }

    fun startScan(){
        rfidDevice.startScan {
            _rfidDataReceiver.value = Event(it)
        }
    }

    fun stopScan(){
        rfidDevice.stopScan()
    }
}
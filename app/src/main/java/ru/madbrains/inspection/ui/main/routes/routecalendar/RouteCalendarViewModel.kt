package ru.madbrains.inspection.ui.main.routes.routecalendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.RouteUiModel

class RouteCalendarViewModel : BaseViewModel() {

    private val _navigateToDateRouteList = MutableLiveData<Event<String>>()
    val navigateToDateRouteList: LiveData<Event<String>> = _navigateToDateRouteList

    var routeDates = mutableListOf<String>()

    fun updateRouteDates(routes: List<RouteUiModel>) {
        routeDates.clear()
        routes.map { route ->
            route.date.split("T").firstOrNull()?.let { routeDates.add(it) }
        }
    }

    fun dateClick(date: String) {
        if (routeDates.contains(date)) {
            _navigateToDateRouteList.value = Event(date)
        }
        Log.d("myLog", "vm date: $date EXISTS: ${routeDates.contains(date)}")
    }
}
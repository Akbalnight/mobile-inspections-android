package ru.madbrains.inspection.ui.main.routes.routecalendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.threeten.bp.LocalDate
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.extensions.toDate
import ru.madbrains.inspection.extensions.toLocalDate
import ru.madbrains.inspection.ui.delegates.DetourUiModel
import java.util.*

class RouteCalendarViewModel : BaseViewModel() {

    private val _navigateToDateRouteList = MutableLiveData<Event<Date>>()
    val navigateToDateRouteList: LiveData<Event<Date>> = _navigateToDateRouteList

    var routeDates = mutableListOf<LocalDate>()

    fun updateRouteDates(routes: List<DetourUiModel>) {
        routeDates.clear()
        routes.map { route ->
            route.dateStartPlan?.let { routeDates.add(it.toLocalDate()) }
        }
    }

    fun dateClick(date: LocalDate) {
        if (routeDates.contains(date)) {

            _navigateToDateRouteList.value = Event(date.toDate())
        }
        Log.d("myLog", "vm date: $date EXISTS: ${routeDates.contains(date)}")
    }
}
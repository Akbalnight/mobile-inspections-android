package ru.madbrains.inspection.ui.main.routes.points.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class RoutePointsListViewModel : BaseViewModel() {

    private val _navigateToTechOperations = MutableLiveData<Event<TechMapModel>>()
    val navigateToTechOperations: LiveData<Event<TechMapModel>> = _navigateToTechOperations

    fun routePointClick(point: TechMapModel?) {
        point?.let {  _navigateToTechOperations.value = Event(it) }
    }
}
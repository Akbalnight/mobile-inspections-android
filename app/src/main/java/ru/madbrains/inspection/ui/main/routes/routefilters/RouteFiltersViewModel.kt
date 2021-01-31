package ru.madbrains.inspection.ui.main.routes.routefilters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.RouteStatus
import ru.madbrains.inspection.base.BaseViewModel

class RouteFiltersViewModel : BaseViewModel() {

    private val _selectedFilter = MutableLiveData<RouteStatus>()
    val selectedFilter: LiveData<RouteStatus> = _selectedFilter

    var currentFilter: RouteStatus? = null

    fun setFilter(status: RouteStatus?) {
        currentFilter = status
    }

    fun applyFilter() {
        _selectedFilter.value = currentFilter
    }
}
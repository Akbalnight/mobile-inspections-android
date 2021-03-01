package ru.madbrains.inspection.ui.main.routes.routefilters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.base.BaseViewModel

class RouteFiltersViewModel : BaseViewModel() {

    private val _selectedFilter = MutableLiveData<DetourStatus>()
    val selectedFilter: LiveData<DetourStatus> = _selectedFilter

    var currentFilter: DetourStatus? = null

    fun setFilter(status: DetourStatus?) {
        currentFilter = status
    }

    fun applyFilter() {
        _selectedFilter.value = currentFilter
    }
}
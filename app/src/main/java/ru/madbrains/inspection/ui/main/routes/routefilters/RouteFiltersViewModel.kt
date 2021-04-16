package ru.madbrains.inspection.ui.main.routes.routefilters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.domain.model.DetourStatusType
import ru.madbrains.inspection.base.BaseViewModel

class RouteFiltersViewModel(
    val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _selectedFilter = MutableLiveData<DetourStatus>()
    val selectedFilter: LiveData<DetourStatus> = _selectedFilter

    var currentFilter: DetourStatus? = null

    fun setFilter(type: DetourStatusType?) {
        currentFilter = preferenceStorage.detourStatuses?.getStatusByType(type)
    }

    fun applyFilter() {
        _selectedFilter.value = currentFilter
    }
}
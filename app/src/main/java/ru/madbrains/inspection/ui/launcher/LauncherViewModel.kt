package ru.madbrains.inspection.ui.launcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class LauncherViewModel(
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _launchDestination = MutableLiveData<Event<LaunchDestination>>()
    val launchDestination: LiveData<Event<LaunchDestination>> = _launchDestination

    init {
        if (preferenceStorage.token.isNullOrEmpty()) {
            _launchDestination.value = Event(LaunchDestination.Authorization)
        } else {
            _launchDestination.value = Event(LaunchDestination.Main)
        }
    }
}

sealed class LaunchDestination {
    object Authorization : LaunchDestination()
    object Main : LaunchDestination()
}
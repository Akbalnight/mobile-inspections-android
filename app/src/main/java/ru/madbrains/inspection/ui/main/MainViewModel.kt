package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class MainViewModel(
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    val username: String
        get() = preferenceStorage.username.orEmpty()

    private val _navigateToMenu = MutableLiveData<Event<Unit>>()
    val navigateToMenu: LiveData<Event<Unit>> = _navigateToMenu

    private val _navigateToRoutes = MutableLiveData<Event<Unit>>()
    val navigateToRoutes: LiveData<Event<Unit>> = _navigateToRoutes

    private val _navigateToDefects = MutableLiveData<Event<Unit>>()
    val navigateToDefects: LiveData<Event<Unit>> = _navigateToDefects

    private val _navigateToMarks = MutableLiveData<Event<Unit>>()
    val navigateToMarks: LiveData<Event<Unit>> = _navigateToMarks

    private val _navigateToSync = MutableLiveData<Event<Unit>>()
    val navigateToSync: LiveData<Event<Unit>> = _navigateToSync

    private val _navigateToSettings = MutableLiveData<Event<Unit>>()
    val navigateToSettings: LiveData<Event<Unit>> = _navigateToSettings

    fun menuClick() {
        _navigateToMenu.value = Event(Unit)
    }

    fun routesClick() {
        _navigateToRoutes.value = Event(Unit)
    }

    fun defectsClick() {
        _navigateToDefects.value = Event(Unit)
    }

    fun marksClick() {
        _navigateToMarks.value = Event(Unit)
    }

    fun syncClick() {
        _navigateToSync.value = Event(Unit)
    }

    fun settingsClick() {
        _navigateToSettings.value = Event(Unit)
    }

    fun logoutClick() {
        // TODO add logout action
    }

}
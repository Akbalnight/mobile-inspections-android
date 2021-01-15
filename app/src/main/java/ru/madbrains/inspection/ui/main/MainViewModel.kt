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

    fun menuClick() {
        _navigateToMenu.value = Event(Unit)
    }
}
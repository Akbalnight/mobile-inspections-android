package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class MainViewModel(
    private val preferenceStorage: PreferenceStorage,
    private val authInteractor: AuthInteractor
) : BaseViewModel() {

    val username: String
        get() = preferenceStorage.username.orEmpty()

    val isAdmin: Boolean
        get() = preferenceStorage.isAdmin

    val isCreator: Boolean
        get() = preferenceStorage.isCreator

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

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

    private val _navigateToAuthorization = MutableLiveData<Event<Unit>>()
    val navigateToAuthorization: LiveData<Event<Unit>> = _navigateToAuthorization

    private val _showSnackBar = MutableLiveData<Event<String>>()
    val showSnackBar: LiveData<Event<String>> = _showSnackBar

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

    fun openSnackBar(text: String) {
        _showSnackBar.value = Event(text)
    }

    fun logoutClick() {
        val accessToken = preferenceStorage.token.orEmpty()
        logout(accessToken)
    }

    private fun logout(accessToken: String) {
        authInteractor.logout(accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({
                preferenceStorage.clearData()
                _navigateToAuthorization.postValue(Event(Unit))
            }, {
                //TODO change to 401 when server is ready
                if(it is HttpException && it.code() == 500){
                    preferenceStorage.clearData()
                    _navigateToAuthorization.postValue(Event(Unit))
                }
                it.printStackTrace()
            })
            .addTo(disposables)
    }
}
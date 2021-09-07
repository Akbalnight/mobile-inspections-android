package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.interactor.RemoteInteractor
import ru.madbrains.domain.interactor.SyncInteractor
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.TextData
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

class MainViewModel(
    private val preferenceStorage: PreferenceStorage,
    private val authInteractor: AuthInteractor,
    private val remoteInteractor: RemoteInteractor,
    private val syncInteractor: SyncInteractor
) : BaseViewModel() {

    companion object {
        private const val lockTime = 5 * 60 * 1000
    }

    private var lastActive = Date()

    val username: String
        get() = preferenceStorage.username.orEmpty()

    val isAdmin: Boolean
        get() = preferenceStorage.isAdmin

    val isCreator: Boolean
        get() = preferenceStorage.isCreator

    private val _progressVisibility = MutableLiveData<Pair<Boolean, Int?>>()
    val progressVisibility: LiveData<Pair<Boolean, Int?>> = _progressVisibility

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

    private val _navigateToLock = MutableLiveData<Event<Unit>>()
    val navigateToLock: LiveData<Event<Unit>> = _navigateToLock

    private val _navigateToAuthorization = MutableLiveData<Event<Unit>>()
    val navigateToAuthorization: LiveData<Event<Unit>> = _navigateToAuthorization

    private val _showSnackBar = MutableLiveData<Event<TextData>>()
    val showSnackBar: LiveData<Event<TextData>> = _showSnackBar

    private val _showExitDialog = MutableLiveData<Event<Unit>>()
    val showExitDialog: LiveData<Event<Unit>> = _showExitDialog

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
        _showSnackBar.value = Event(TextData.Str(text))
    }

    fun logoutClick() {
        _showExitDialog.value = Event(Unit)
    }

    fun logout() {
        val accessToken = preferenceStorage.token.orEmpty()
        remoteInteractor.sendSyncDataAndRefreshDb()
            .observeOn(Schedulers.io())
            .doOnSubscribe { _progressVisibility.postValue(Pair(true, R.string.sync)) }
            .andThen(authInteractor.logout(accessToken).doOnSubscribe {
                _progressVisibility.postValue(Pair(true, null))
            })
            .andThen(syncInteractor.logoutClean())
            .doFinally { _progressVisibility.postValue(Pair(false, null)) }
            .subscribe({
                _navigateToAuthorization.postValue(Event(Unit))
            }, {
                if (it is UnknownHostException || it is SocketTimeoutException) {
                    _showSnackBar.postValue(Event(TextData.ResId(R.string.server_unavailable)))
                } else {
                    _showSnackBar.postValue(Event(TextData.Str(it.message ?: "")))
                }
            })
            .addTo(disposables)
    }

    fun forceLogout() {
        syncInteractor.logoutClean()
            .observeOn(Schedulers.io())
            .subscribe({
                _navigateToAuthorization.postValue(Event(Unit))
            }, {
                Timber.d("debug_dmm error: $it")
            })
            .addTo(disposables)

    }

    fun onPause() {
        lastActive = Date()
    }

    fun onResume() {
        if (Date().time - lastActive.time > lockTime) {
            _navigateToLock.postValue(Event(Unit))
        }
    }
}
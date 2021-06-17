package ru.madbrains.inspection.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.extensions.toBase64HashWith256
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LockScreenViewModel(
    private val preferenceStorage: PreferenceStorage,
    private val authInteractor: AuthInteractor,
    private val detoursInteractor: DetoursInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Pair<Boolean, Int?>>()
    val progressVisibility: LiveData<Pair<Boolean, Int?>> = _progressVisibility

    private val _navigateToMain = MutableLiveData<Event<Unit>>()
    val navigateToMain: LiveData<Event<Unit>> = _navigateToMain

    private val _navigateToAuthorization = MutableLiveData<Event<Unit>>()
    val navigateToAuthorization: LiveData<Event<Unit>> = _navigateToAuthorization

    private val _showError = MutableLiveData<Event<Int>>()
    val showError: LiveData<Event<Int>> = _showError

    private val _showSnackBar = MutableLiveData<Event<String>>()
    val showSnackBar: LiveData<Event<String>> = _showSnackBar

    fun login(login: String, password: String) {
        if(
            login.toBase64HashWith256() == preferenceStorage.loginHash &&
            password.toBase64HashWith256() == preferenceStorage.passwordHash
        ){
            _progressVisibility.postValue(Pair(true, null))
            _navigateToMain.postValue(Event(Unit))
            Completable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe({
                    _progressVisibility.postValue(Pair(false, null))
                },{}).addTo(disposables)
        } else{
            _showError.value = Event(R.string.login_and_password_do_not_match)
        }
    }
    fun logout() {
        val accessToken = preferenceStorage.token.orEmpty()
        detoursInteractor.syncStartSendingData()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(Pair(true, R.string.sync)) }
            .andThen(authInteractor.logout(accessToken).doOnSubscribe {
                _progressVisibility.postValue(Pair(true, null))
            })
            .andThen(detoursInteractor.logoutClean())
            .doFinally { _progressVisibility.postValue(Pair(false, null)) }
            .subscribe({
                _navigateToAuthorization.postValue(Event(Unit))
            }, {
                _showSnackBar.postValue(Event(it.message?:""))
            })
            .addTo(disposables)
    }
}
package ru.madbrains.inspection.ui.main

import android.webkit.CookieManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import ru.madbrains.data.extensions.toBase64HashWith256
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class LockScreenViewModel(
    private val preferenceStorage: PreferenceStorage,
    private val authInteractor: AuthInteractor
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToMain = MutableLiveData<Event<Unit>>()
    val navigateToMain: LiveData<Event<Unit>> = _navigateToMain

    private val _navigateToAuthorization = MutableLiveData<Event<Unit>>()
    val navigateToAuthorization: LiveData<Event<Unit>> = _navigateToAuthorization

    private val _showError = MutableLiveData<Event<Int>>()
    val showError: LiveData<Event<Int>> = _showError

    fun login(login: String, password: String) {
        if(
            login.toBase64HashWith256() == preferenceStorage.loginHash &&
            password.toBase64HashWith256() == preferenceStorage.passwordHash
        ){
            _navigateToMain.postValue(Event(Unit))
        } else{
            _showError.value = Event(R.string.login_and_password_do_not_match)
        }
    }
    fun logout() {
        val accessToken = preferenceStorage.token.orEmpty()

        authInteractor.logout(accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .onErrorResumeNext {
                if (it is HttpException && it.code() == 500) {
                    Completable.complete()
                } else{
                    throw it
                }
            }
            //.andThen(detoursInteractor.cleanEverything())
            .subscribe({
                clearDataAndNavToAuth()
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun clearDataAndNavToAuth(){
        CookieManager.getInstance().removeAllCookies(null)
        preferenceStorage.clearLogout()
        _navigateToAuthorization.postValue(Event(Unit))
    }
}
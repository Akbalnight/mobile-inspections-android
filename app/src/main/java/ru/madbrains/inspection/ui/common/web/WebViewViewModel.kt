package ru.madbrains.inspection.ui.common.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event

class WebViewViewModel(
    private val authInteractor: AuthInteractor,
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToMain = MutableLiveData<Event<Unit>>()
    val navigateToMain: LiveData<Event<Unit>> = _navigateToMain

    fun getToken(authCode: String) {
        val codeVerifier = preferenceStorage.codeVerifier.orEmpty()
        authInteractor.getToken(authCode, codeVerifier)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.value = true }
            .doAfterTerminate { _progressVisibility.value = false }
            .subscribe({
                preferenceStorage.apply {
                    token = it.accessToken
                    refreshToken = it.refreshToken
                    username = it.username
                    userId = it.userId
                    codeChallenge = it.codeChallenge
                    isAdmin = it.isAdmin
                    isCreator = it.isCreator
                }
                _navigateToMain.value = Event(Unit)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }
}
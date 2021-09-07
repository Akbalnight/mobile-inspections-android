package ru.madbrains.inspection.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.data.network.OAuthData
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import java.util.*

class AuthorizationViewModel(
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _navigateToAuth = MutableLiveData<Event<String>>()
    val navigateToAuth: LiveData<Event<String>> = _navigateToAuth

    private val _navigateToServerSettings = MutableLiveData<Event<Unit>>()
    val navigateToServerSettings: LiveData<Event<Unit>> = _navigateToServerSettings

    init {
        generateCodeVerifier()
    }

    private fun generateCodeVerifier() {
        if (preferenceStorage.codeVerifier.isNullOrEmpty()) {
            preferenceStorage.codeVerifier = UUID.randomUUID().toString()
        }
    }

    fun authClick() {
        preferenceStorage.codeVerifier?.let {
            val authUrl = OAuthData.getAuthorizeUrl(
                preferenceStorage.apiUrl ?: "",
                preferenceStorage.authUrl ?: "",
                it
            )
            _navigateToAuth.postValue(Event(authUrl))
        }
    }

    fun serverSettingsClick() {
        _navigateToServerSettings.postValue(Event(Unit))
    }
}
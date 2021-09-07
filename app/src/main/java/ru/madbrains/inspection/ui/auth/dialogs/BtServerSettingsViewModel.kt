package ru.madbrains.inspection.ui.auth.dialogs

import ru.madbrains.data.network.ApiData
import ru.madbrains.data.network.IAuthenticator
import ru.madbrains.data.network.OAuthData
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.inspection.base.BaseViewModel

class BtServerSettingsViewModel(
    private val preferenceStorage: PreferenceStorage,
    private val authenticator: IAuthenticator
) : BaseViewModel() {

    val currentApiUrl = preferenceStorage.apiUrl
    val currentAuthUrl = preferenceStorage.authUrl

    fun applyClick(authServer: String, portalServer: String) {
        preferenceStorage.apiUrl = portalServer
        preferenceStorage.authUrl = authServer
        initApi(preferenceStorage)
    }

    private fun initApi(preferenceStorage: PreferenceStorage) {
        OAuthData.initApi(preferenceStorage)
        ApiData.initApi(preferenceStorage, authenticator)
    }
}
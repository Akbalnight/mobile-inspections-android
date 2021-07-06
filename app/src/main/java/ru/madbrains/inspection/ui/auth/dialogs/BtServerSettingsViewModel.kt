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

    fun applyClick(authServer: String, portalServer: String) {
        ApiData.apiUrl = portalServer
        OAuthData.oauthUrl = authServer
        initApi(preferenceStorage)
    }

    private fun initApi(preferenceStorage: PreferenceStorage) {
        OAuthData.initApi(preferenceStorage)
        ApiData.initApi(preferenceStorage, authenticator)
    }
}
package ru.madbrains.inspection.ui.main.settings

import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.inspection.base.BaseViewModel

class SettingsViewModel(
    private val preferenceStorage: PreferenceStorage
) : BaseViewModel() {
    var saveInfoDuration: Int
        get() {
            return preferenceStorage.saveInfoDuration
        }
        set(value) {
            preferenceStorage.saveInfoDuration = value
        }
}
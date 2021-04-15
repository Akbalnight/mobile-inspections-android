package ru.madbrains.data.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface PreferenceStorage {
    var token: String?
    var refreshToken: String?
    var codeVerifier: String?
    var username: String?
    var userId: String?
    var codeChallenge: String?
    var saveInfoDuration: Int
    var isAdmin: Boolean
    var isCreator: Boolean
    fun clearData()
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
class SharedPreferenceStorage(
    context: Context
) : PreferenceStorage {

    private companion object {
        const val PREFS_NAME = "inspection"
        const val PREF_TOKEN = "pref_token"
        const val PREF_REFRESH_TOKEN = "pref_refresh_token"
        const val PREF_CODE_VERIFIER = "pref_code_verifier"
        const val PREF_USERNAME = "pref_username"
        const val PREF_USER_ID = "pref_user_id"
        const val PREF_CODE_CHALLENGE = "pref_code_challenge"
        const val PREF_SAVE_INFO_DURATION = "PREF_SAVE_INFO_DURATION"
        const val PREF_IS_ADMIN = "PREF_IS_ADMIN"
        const val PREF_IS_CREATOR = "PREF_IS_CREATOR"
    }

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    override var token by StringPreference(prefs, PREF_TOKEN, "")
    override var refreshToken by StringPreference(prefs, PREF_REFRESH_TOKEN, "")
    override var codeVerifier by StringPreference(prefs, PREF_CODE_VERIFIER, "")
    override var username by StringPreference(prefs, PREF_USERNAME, "")
    override var userId by StringPreference(prefs, PREF_USER_ID, "")
    override var codeChallenge by StringPreference(prefs, PREF_CODE_CHALLENGE, "")
    override var saveInfoDuration by IntPreference(prefs, PREF_SAVE_INFO_DURATION, 5)
    override var isAdmin by BooleanPreference(prefs, PREF_IS_ADMIN, false)
    override var isCreator by BooleanPreference(prefs, PREF_IS_CREATOR, false)

    override fun clearData() {
        prefs.edit { clear() }
    }
}

class BooleanPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit { putString(name, value) }
    }
}

class IntPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.edit { putInt(name, value) }
    }
}
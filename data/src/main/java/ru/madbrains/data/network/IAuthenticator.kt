package ru.madbrains.data.network

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.reactivex.subjects.BehaviorSubject
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.madbrains.data.network.interceptors.SessionInterceptor
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.model.UserInfoModel
import timber.log.Timber

class IAuthenticator constructor(
    private val context: Context,
    private val authInteractor: AuthInteractor,
    private val preferenceStorage: PreferenceStorage
) : Authenticator {

    companion object {
        const val ACTION_FORCE_LOGOUT = "ACTION_FORCE_LOGOUT"
        var refreshTokenSource: BehaviorSubject<UserInfoModel>? = null
    }

    private fun sendForceLogoutSignal() {
        val intent = Intent(ACTION_FORCE_LOGOUT)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val expiredToken = preferenceStorage.refreshToken
        if (expiredToken != null) {
            try {
                val source = refreshTokenSource
                val model: UserInfoModel
                if (source == null) {
                    refreshTokenSource = BehaviorSubject.create<UserInfoModel>()
                    model = authInteractor.refreshToken(expiredToken).blockingGet()
                    preferenceStorage.apply {
                        token = model.accessToken
                        refreshToken = model.refreshToken
                        username = model.username
                        userId = model.userId
                        codeChallenge = model.codeChallenge
                        isAdmin = model.isAdmin
                        isCreator = model.isCreator
                    }
                    refreshTokenSource?.onNext(model)
                    refreshTokenSource = null
                } else {
                    model = source.blockingFirst()
                }
                return response.request.newBuilder()
                    .header(
                        SessionInterceptor.KEY_TOKEN,
                        String.format(SessionInterceptor.VALUE_TOKEN, model.accessToken)
                    )
                    .header(
                        SessionInterceptor.KEY_USER_ID,
                        String.format(SessionInterceptor.VALUE_USER_ID, model.userId)
                    )
                    .header(
                        SessionInterceptor.KEY_COOKIE,
                        String.format(SessionInterceptor.VALUE_COOKIE, model.codeChallenge)
                    )
                    .build()
            } catch (e: Throwable) {
                Timber.d("debug_dmm refresh token error: $e")
            }
        }
        sendForceLogoutSignal()
        return null
    }
}
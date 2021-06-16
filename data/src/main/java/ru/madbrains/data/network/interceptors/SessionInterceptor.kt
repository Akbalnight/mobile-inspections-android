package ru.madbrains.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.madbrains.data.prefs.PreferenceStorage

class SessionInterceptor constructor(
    private val preferenceStorage: PreferenceStorage
) : Interceptor {

    companion object {
        const val KEY_TOKEN = "Authorization"
        const val VALUE_TOKEN = "Bearer %s"

        const val KEY_USER_ID = "userId"
        const val VALUE_USER_ID = "%s"

        const val KEY_COOKIE = "Cookie"
        const val VALUE_COOKIE = "code_challenge=%s"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferenceStorage.token
        val userId = preferenceStorage.userId
        val codeChallenge = preferenceStorage.codeChallenge

        val originalRequest = chain.request()
        val tokenRequest = originalRequest.newBuilder()

        if (!token.isNullOrBlank()) {
            tokenRequest.addHeader(KEY_TOKEN, String.format(VALUE_TOKEN, token))
            tokenRequest.addHeader(KEY_USER_ID, String.format(VALUE_USER_ID, userId))
            tokenRequest.addHeader(KEY_COOKIE, String.format(VALUE_COOKIE, codeChallenge))
        }

        return chain.proceed(tokenRequest.build())
    }
}
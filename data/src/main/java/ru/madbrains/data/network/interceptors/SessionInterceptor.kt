package ru.madbrains.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.madbrains.data.prefs.PreferenceStorage

class SessionInterceptor constructor(
    private val preferenceStorage: PreferenceStorage
) : Interceptor {

    companion object {
        private const val KEY_TOKEN = "Authorization"
        private const val VALUE_TOKEN = "Bearer %s"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferenceStorage.token

        val originalRequest = chain.request()
        val tokenRequest = originalRequest.newBuilder()

        if (!token.isNullOrBlank()) {
            tokenRequest.addHeader(KEY_TOKEN, String.format(VALUE_TOKEN, token))
        }

        return chain.proceed(tokenRequest.build())
    }
}
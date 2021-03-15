package ru.madbrains.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.madbrains.data.extensions.toBase64Hash
import ru.madbrains.data.network.OAuthData
import ru.madbrains.data.prefs.PreferenceStorage

class AuthInterceptor(
    private val preferenceStorage: PreferenceStorage
) : Interceptor {

    companion object {
        private const val KEY_TOKEN = "Authorization"
        private const val VALUE_TOKEN = "Basic %s"

        private const val KEY_COOKIE = "Cookie"
        private const val VALUE_COOKIE = "code_challenge=%s"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "${OAuthData.clientId}:${OAuthData.clientSecret}".toBase64Hash()
        val codeChallenge = preferenceStorage.codeChallenge

        val originalRequest = chain.request()
        val tokenRequest = originalRequest.newBuilder()

        tokenRequest.addHeader(KEY_TOKEN, String.format(VALUE_TOKEN, token))

        codeChallenge?.let {
            tokenRequest.addHeader(KEY_COOKIE, String.format(VALUE_COOKIE, codeChallenge))
        }

        return chain.proceed(tokenRequest.build())
    }
}
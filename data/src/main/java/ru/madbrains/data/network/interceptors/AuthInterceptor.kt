package ru.madbrains.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.madbrains.data.extensions.toBase64Hash
import ru.madbrains.data.network.OAuthData

class AuthInterceptor : Interceptor {

    companion object {
        private const val KEY_TOKEN = "Authorization"
        private const val VALUE_TOKEN = "Basic %s"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "${OAuthData.clientId}:${OAuthData.clientSecret}".toBase64Hash()

        val originalRequest = chain.request()
        val tokenRequest = originalRequest.newBuilder()

        tokenRequest.addHeader(KEY_TOKEN, String.format(VALUE_TOKEN, token))

        return chain.proceed(tokenRequest.build())
    }
}
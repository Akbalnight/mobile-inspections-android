package ru.madbrains.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.madbrains.data.BuildConfig
import ru.madbrains.data.extensions.toBase64HashWith256
import ru.madbrains.data.network.api.AuthApi
import ru.madbrains.data.network.interceptors.AuthInterceptor
import ru.madbrains.data.prefs.PreferenceStorage
import java.util.concurrent.TimeUnit

object OAuthData {

    lateinit var authApi: AuthApi

    var clientId = "System-Service-Dias"
    var clientSecret = "24U7tcNLHRSvvjrr9sFEXGyMTpzk59mG"

    fun getAuthorizeUrl(
        apiUrl: String,
        authUrl: String,
        codeVerifier: String
    ): String {
        return "$authUrl/oauth/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&redirect_uri=$apiUrl/authorization_code" +
                "&scope=read&code_challenge=${codeVerifier.toBase64HashWith256()}" +
                "&code_challenge_method=s256"
    }

    private fun getAuthOkHttpClient(preferenceStorage: PreferenceStorage): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            addInterceptor(AuthInterceptor(preferenceStorage))
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        return builder.build()
    }

    fun initApi(preferenceStorage: PreferenceStorage) {
        authApi = Retrofit.Builder()
            .baseUrl(preferenceStorage.authUrl ?: "")
            .client(getAuthOkHttpClient(preferenceStorage))
            .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    private fun getMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
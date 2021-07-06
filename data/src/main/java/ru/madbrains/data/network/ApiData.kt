package ru.madbrains.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.madbrains.data.BuildConfig
import ru.madbrains.data.network.api.InspectionApi
import ru.madbrains.data.network.interceptors.SessionInterceptor
import ru.madbrains.data.prefs.PreferenceStorage
import java.util.*
import java.util.concurrent.TimeUnit

object ApiData {

    var apiUrl = "https://mobinspect.dias-dev.ru"

    lateinit var inspectionApi: InspectionApi

    private fun getInspectionOkHttpClient(
        preferenceStorage: PreferenceStorage,
        authenticator: IAuthenticator
    ): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            addInterceptor(SessionInterceptor(preferenceStorage))
            authenticator(authenticator)
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        return builder.build()
    }

    fun initApi(preferenceStorage: PreferenceStorage, authenticator: IAuthenticator) {
        inspectionApi = Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(getInspectionOkHttpClient(preferenceStorage, authenticator))
            .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(InspectionApi::class.java)
    }

    private fun getMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()
    }
}
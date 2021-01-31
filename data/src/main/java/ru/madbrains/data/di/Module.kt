package ru.madbrains.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.madbrains.data.BuildConfig
import ru.madbrains.data.network.ApiData
import ru.madbrains.data.network.OAuthData
import ru.madbrains.data.network.api.AuthApi
import ru.madbrains.data.network.api.InspectionApi
import ru.madbrains.data.network.interceptors.AuthInterceptor
import ru.madbrains.data.network.interceptors.SessionInterceptor
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.data.prefs.SharedPreferenceStorage
import ru.madbrains.data.repository.AuthRepositoryImpl
import ru.madbrains.data.repository.RoutesRepositoryImpl
import ru.madbrains.domain.repository.AuthRepository
import ru.madbrains.domain.repository.RoutesRepository
import java.util.concurrent.TimeUnit

val dataModule = module {
    single { getPreferenceStorage(androidContext()) }
    single { getMoshi() }
    single { getInspectionApi(get(), get()) }
    single { getAuthApi(get()) }
    single { getAuthRepository(get()) }
    single { getRoutesRepository(get()) }
}

private fun getPreferenceStorage(context: Context): PreferenceStorage {
    return SharedPreferenceStorage(context)
}

private fun getMoshi(): Moshi {
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

private fun getInspectionApi(preferenceStorage: PreferenceStorage, moshi: Moshi): InspectionApi {
    return Retrofit.Builder()
        .baseUrl(ApiData.apiUrl)
        .client(getInspectionOkHttpClient(preferenceStorage))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(InspectionApi::class.java)
}

private fun getAuthApi(moshi: Moshi): AuthApi {
    return Retrofit.Builder()
        .baseUrl(OAuthData.oauthUrl)
        .client(getAuthOkHttpClient())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(AuthApi::class.java)
}

private fun getInspectionOkHttpClient(preferenceStorage: PreferenceStorage): OkHttpClient {
    val builder = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        addInterceptor(SessionInterceptor(preferenceStorage))
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }
    return builder.build()
}

private fun getAuthOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        addInterceptor(AuthInterceptor())
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }
    return builder.build()
}

private fun getAuthRepository(api: AuthApi): AuthRepository {
    return AuthRepositoryImpl(api)
}

private fun getRoutesRepository(api: InspectionApi): RoutesRepository {
    return RoutesRepositoryImpl(api)
}
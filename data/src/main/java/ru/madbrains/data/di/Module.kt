package ru.madbrains.data.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.data.prefs.SharedPreferenceStorage
import ru.madbrains.data.repository.AuthRepositoryImpl
import ru.madbrains.data.repository.RoutesRepositoryImpl
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.domain.repository.AuthRepository
import ru.madbrains.domain.repository.DetoutsRepository

val dataModule = module {
    single { getPreferenceStorage(androidContext()) }
    single { getAuthRepository() }
    single { getDetoursRepository(get()) }
    single { getFileUtil(get()) }
}

private fun getPreferenceStorage(context: Context): PreferenceStorage {
    return SharedPreferenceStorage(context)
}

private fun getAuthRepository(): AuthRepository {
    return AuthRepositoryImpl()
}

private fun getDetoursRepository(preferenceStorage: PreferenceStorage): DetoutsRepository {
    return RoutesRepositoryImpl(preferenceStorage)
}

private fun getFileUtil(context: Context): FileUtil {
    return FileUtil(context)
}
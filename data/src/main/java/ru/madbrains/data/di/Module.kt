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
import com.uhf.api.cls.Reader
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.data.utils.RfidMock
import ru.madbrains.data.utils.RfidReader
import timber.log.Timber

val dataModule = module {
    single { getPreferenceStorage(androidContext()) }
    single { getAuthRepository() }
    single { getDetoursRepository(get()) }
    single { getFileUtil(get()) }
    single { getReader() }
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

private fun getReader(): RfidDevice {
    return try {
        RfidReader()
    } catch (e: Throwable){
        Timber.d("debug_dmm Rfid not supported! Running mock instead. $e")
        RfidMock()
    }
}

package ru.madbrains.data.di

import android.content.Context
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.madbrains.data.database.HcbDatabase
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.data.prefs.SharedPreferenceStorage
import ru.madbrains.data.repository.AuthRepositoryImpl
import ru.madbrains.data.repository.DetoursRepositoryImpl
import ru.madbrains.data.repository.OfflineRepositoryImpl
import ru.madbrains.data.utils.FileUtil
import ru.madbrains.data.utils.RfidDevice
import ru.madbrains.data.utils.RfidMock
import ru.madbrains.data.utils.RfidReader
import ru.madbrains.domain.repository.AuthRepository
import ru.madbrains.domain.repository.DetoursRepository
import ru.madbrains.domain.repository.OfflineRepository
import timber.log.Timber

val dataModule = module {
    single { getPreferenceStorage(androidContext()) }
    single { getFileUtil(get()) }
    single { getReader() }
    single { getDatabase(androidContext()) }

    single { getAuthRepository() }
    single { getDetoursRepository(get()) }
    single { getOfflineRepository(get(), get()) }
}

private fun getPreferenceStorage(context: Context): PreferenceStorage {
    return SharedPreferenceStorage(context)
}

private fun getAuthRepository(): AuthRepository {
    return AuthRepositoryImpl()
}

private fun getDetoursRepository(preferenceStorage: PreferenceStorage): DetoursRepository {
    return DetoursRepositoryImpl(preferenceStorage)
}

private fun getOfflineRepository(
    preferenceStorage: PreferenceStorage,
    db: HcbDatabase
): OfflineRepository {
    return OfflineRepositoryImpl(preferenceStorage, db)
}

private fun getFileUtil(context: Context): FileUtil {
    return FileUtil(context)
}

private fun getReader(): RfidDevice {
    return try {
        RfidReader()
    } catch (e: Throwable) {
        Timber.d("debug_dmm Rfid not supported! Running mock instead. $e")
        RfidMock()
    }
}

private fun getDatabase(context: Context): HcbDatabase {
    return Room.databaseBuilder(context, HcbDatabase::class.java, "hcb-database")
        .fallbackToDestructiveMigration()
        .build()
}
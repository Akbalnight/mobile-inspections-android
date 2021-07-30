package ru.madbrains.data.di

import android.content.Context
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.madbrains.data.database.HcbDatabase
import ru.madbrains.data.network.IAuthenticator
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.data.prefs.SharedPreferenceStorage
import ru.madbrains.data.repository.*
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.repository.AuthRepository
import ru.madbrains.domain.repository.OfflineRepository
import ru.madbrains.domain.repository.RemoteRepository
import ru.madbrains.domain.repository.RfidRepository
import timber.log.Timber

val dataModule = module {
    single { getPreferenceStorage(androidContext()) }
    single { getDatabase(androidContext()) }

    single { getRfidRepository() }
    single { getAuthRepository() }
    single { getIAuthenticator(androidContext(), get(), get()) }
    single { getRemoteRepository(get()) }
    single { getOfflineRepository(get(), get()) }
}

private fun getPreferenceStorage(context: Context): PreferenceStorage {
    return SharedPreferenceStorage(context)
}

private fun getIAuthenticator(
    context: Context,
    authInteractor: AuthInteractor,
    preferenceStorage: PreferenceStorage
): IAuthenticator {
    return IAuthenticator(context, authInteractor, preferenceStorage)
}

private fun getAuthRepository(): AuthRepository {
    return AuthRepositoryImpl()
}

private fun getRemoteRepository(preferenceStorage: PreferenceStorage): RemoteRepository {
    return RemoteRepositoryImpl(preferenceStorage)
}

private fun getOfflineRepository(
    preferenceStorage: PreferenceStorage,
    db: HcbDatabase
): OfflineRepository {
    return OfflineRepositoryImpl(preferenceStorage, db)
}

private fun getRfidRepository(): RfidRepository {
    return try {
        RfidRepositoryImpl()
    } catch (e: Throwable) {
        Timber.d("debug_dmm Rfid not supported! Running mock instead. $e")
        RfidRepositoryMockImpl()
    }
}

private fun getDatabase(context: Context): HcbDatabase {
    return Room.databaseBuilder(context, HcbDatabase::class.java, "hcb-database")
        .fallbackToDestructiveMigration()
        .build()
}
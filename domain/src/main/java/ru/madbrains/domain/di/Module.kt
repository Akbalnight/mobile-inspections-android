package ru.madbrains.domain.di

import org.koin.dsl.module
import ru.madbrains.domain.interactor.*

val domainModule = module {
    factory { AuthInteractor(get()) }
    factory { RemoteInteractor(get(), get()) }
    factory { OfflineInteractor(get()) }
    factory { SyncInteractor(get()) }
    factory { RfidInteractor(get()) }
}
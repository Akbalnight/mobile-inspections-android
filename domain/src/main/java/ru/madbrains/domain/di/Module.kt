package ru.madbrains.domain.di

import org.koin.dsl.module
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.interactor.RemoteInteractor
import ru.madbrains.domain.interactor.SyncInteractor

val domainModule = module {
    factory { AuthInteractor(get()) }
    factory { RemoteInteractor(get(), get()) }
    factory { OfflineInteractor(get()) }
    factory { SyncInteractor(get()) }
}
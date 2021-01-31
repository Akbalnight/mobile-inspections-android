package ru.madbrains.domain.di

import org.koin.dsl.module
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.interactor.RoutesInteractor

val domainModule = module {
    factory { AuthInteractor(get()) }
    factory { RoutesInteractor(get()) }
}
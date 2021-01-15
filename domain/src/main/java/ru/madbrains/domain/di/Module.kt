package ru.madbrains.domain.di

import org.koin.dsl.module
import ru.madbrains.domain.interactor.AuthInteractor

val domainModule = module {
    factory { AuthInteractor(get()) }
}
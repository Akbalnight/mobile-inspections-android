package ru.madbrains.domain.di

import org.koin.dsl.module
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.interactor.DetoursInteractor

val domainModule = module {
    factory { AuthInteractor(get()) }
    factory { DetoursInteractor(get(), get()) }
}
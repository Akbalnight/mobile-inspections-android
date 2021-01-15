package ru.madbrains.inspection.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.madbrains.inspection.ui.auth.AuthorizationViewModel
import ru.madbrains.inspection.ui.common.WebViewViewModel
import ru.madbrains.inspection.ui.launcher.LauncherViewModel
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.routes.routelist.RouteListViewModel

val appModule = module {
    viewModel { LauncherViewModel(get()) }
    viewModel { AuthorizationViewModel(get()) }
    viewModel { WebViewViewModel(get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { RouteListViewModel() }
}
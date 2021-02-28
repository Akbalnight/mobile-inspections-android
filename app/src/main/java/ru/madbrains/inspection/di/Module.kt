package ru.madbrains.inspection.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.madbrains.inspection.ui.auth.AuthorizationViewModel
import ru.madbrains.inspection.ui.common.camera.CameraViewModel
import ru.madbrains.inspection.ui.common.web.WebViewViewModel
import ru.madbrains.inspection.ui.launcher.LauncherViewModel
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.deviceSelectList.DeviceSelectListViewModel
import ru.madbrains.inspection.ui.main.defects.defectlist.DefectListViewModel
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.dateroutelist.DateRouteListViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel
import ru.madbrains.inspection.ui.main.routes.points.list.RoutePointsListViewModel
import ru.madbrains.inspection.ui.main.routes.points.map.RoutePointsMapViewModel
import ru.madbrains.inspection.ui.main.routes.routecalendar.RouteCalendarViewModel
import ru.madbrains.inspection.ui.main.routes.routefilters.RouteFiltersViewModel
import ru.madbrains.inspection.ui.main.routes.routelist.RouteListViewModel
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsViewModel

val appModule = module {
    viewModel { LauncherViewModel(get()) }
    viewModel { AuthorizationViewModel(get()) }
    viewModel {
        WebViewViewModel(
            get(),
            get()
        )
    }
    viewModel { MainViewModel(get()) }
    viewModel { DetoursViewModel(get()) }
    viewModel { RouteFiltersViewModel() }
    viewModel { RouteListViewModel() }
    viewModel { RouteCalendarViewModel() }
    viewModel { DateRouteListViewModel() }
    viewModel { RoutePointsViewModel(get()) }
    viewModel { RoutePointsListViewModel() }
    viewModel { RoutePointsMapViewModel() }
    viewModel { TechOperationsViewModel(get()) }
    viewModel { DefectListViewModel(get()) }
    viewModel { DefectDetailViewModel(get()) }
    viewModel { DeviceSelectListViewModel(get()) }
    viewModel { CameraViewModel() }
}
package ru.madbrains.inspection.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.madbrains.inspection.ui.auth.AuthorizationViewModel
import ru.madbrains.inspection.ui.auth.dialogs.BtServerSettingsViewModel
import ru.madbrains.inspection.ui.common.camera.CameraViewModel
import ru.madbrains.inspection.ui.common.web.WebViewViewModel
import ru.madbrains.inspection.ui.launcher.LauncherViewModel
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.SyncViewModel
import ru.madbrains.inspection.ui.main.checkpoints.detail.CheckpointDetailViewModel
import ru.madbrains.inspection.ui.main.checkpoints.list.CheckpointListViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.DefectDetailViewModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.equipmentselectlist.EquipmentSelectListViewModel
import ru.madbrains.inspection.ui.main.defects.defectlist.DefectListViewModel
import ru.madbrains.inspection.ui.main.equipment.EquipmentViewModel
import ru.madbrains.inspection.ui.main.equipmentList.EquipmentListViewModel
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel
import ru.madbrains.inspection.ui.main.routes.points.map.RoutePointsMapViewModel
import ru.madbrains.inspection.ui.main.routes.routecalendar.RouteCalendarViewModel
import ru.madbrains.inspection.ui.main.routes.routefilters.RouteFiltersViewModel
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsViewModel
import ru.madbrains.inspection.ui.main.settings.SettingsViewModel

val appModule = module {
    viewModel { LauncherViewModel(get(), get()) }
    viewModel { AuthorizationViewModel(get()) }
    viewModel {
        WebViewViewModel(
            get(),
            get()
        )
    }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { SyncViewModel(get(), get()) }
    viewModel { DetoursViewModel(get(), get()) }
    viewModel { RouteFiltersViewModel(get()) }
    viewModel { RouteCalendarViewModel() }
    viewModel { RoutePointsViewModel(get(), get()) }
    viewModel { RoutePointsMapViewModel(get()) }
    viewModel { TechOperationsViewModel(get(), get()) }
    viewModel { DefectListViewModel(get()) }
    viewModel { DefectDetailViewModel(get()) }
    viewModel { EquipmentSelectListViewModel(get()) }
    viewModel { CameraViewModel(get(), get()) }
    viewModel { BtServerSettingsViewModel(get(), get()) }
    viewModel { EquipmentViewModel(get()) }
    viewModel { EquipmentListViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CheckpointListViewModel(get()) }
    viewModel { CheckpointDetailViewModel(get(), get(), get()) }
}
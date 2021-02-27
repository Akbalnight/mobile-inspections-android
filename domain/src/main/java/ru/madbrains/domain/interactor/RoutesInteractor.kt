package ru.madbrains.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoutsRepository

class RoutesInteractor(
    private val routesRepository: DetoutsRepository
) {
    fun getDetours(): Single<List<DetourModel>> {
        return routesRepository.getDetours()
            .subscribeOn(Schedulers.io())
    }

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>> {
        return routesRepository.getRoutePoints(routeId)
            .subscribeOn(Schedulers.io())
    }

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>> {
        return routesRepository.getPlanTechOperations(dataId)
            .subscribeOn(Schedulers.io())
    }

    fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        return routesRepository.getDefectTypical()
                .subscribeOn(Schedulers.io())
    }

    fun getEquipments(names: List<String>, uuid: List<String>): Single<List<EquipmentsModel>> {
        return routesRepository.getEquipments(names, uuid)
            .subscribeOn(Schedulers.io())
    }
}
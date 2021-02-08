package ru.madbrains.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.PlanTechOperationsModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.repository.RoutesRepository

class RoutesInteractor(
    private val routesRepository: RoutesRepository
) {
    fun getRoutes(): Single<List<RouteModel>> {
        return routesRepository.getRoutes()
            .subscribeOn(Schedulers.io())
    }

    fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>> {
        return routesRepository.getPlanTechOperations(dataId)
            .subscribeOn(Schedulers.io())
    }
}
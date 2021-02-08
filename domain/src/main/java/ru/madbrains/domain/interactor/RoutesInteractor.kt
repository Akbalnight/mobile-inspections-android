package ru.madbrains.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.domain.repository.RoutesRepository

class RoutesInteractor(
    private val routesRepository: RoutesRepository
) {
    fun getRoutes(): Single<List<RouteModel>> {
        return routesRepository.getRoutes()
            .subscribeOn(Schedulers.io())
    }

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>> {
        return routesRepository.getRoutePoints(routeId)
            .subscribeOn(Schedulers.io())
    }
}
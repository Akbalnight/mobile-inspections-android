package ru.madbrains.domain.repository

import io.reactivex.Single
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel

interface RoutesRepository {
    fun getRoutes(): Single<List<RouteModel>>

    fun getRoutePoints(routeId: String): Single<List<RoutePointModel>>
}
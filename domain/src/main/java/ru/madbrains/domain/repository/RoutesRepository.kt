package ru.madbrains.domain.repository

import io.reactivex.Single
import ru.madbrains.domain.model.PlanTechOperationsModel
import ru.madbrains.domain.model.RouteModel

interface RoutesRepository {
    fun getRoutes(): Single<List<RouteModel>>

    fun getPlanTechOperations(): Single<List<PlanTechOperationsModel>>
}
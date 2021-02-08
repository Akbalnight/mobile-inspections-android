package ru.madbrains.data.network.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import ru.madbrains.data.network.request.GetRotesReq
import ru.madbrains.data.network.request.GetRoutePointsReq
import ru.madbrains.data.network.response.GetRoutePointResp
import ru.madbrains.data.network.response.GetRouteResp

interface InspectionApi {

    // region routes
    @POST("/api/dynamicdq/data/flat/mobileDetours")
    fun getRoutes(@Body request: GetRotesReq): Single<List<GetRouteResp>>

    @POST("/api/dynamicdq/data/flat/mobileDetoursPlanData")
    fun getRoutePoints(@Body request: GetRoutePointsReq): Single<List<GetRoutePointResp>>
    // endregion
}
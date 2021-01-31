package ru.madbrains.data.network.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import ru.madbrains.data.network.request.GetRotesReq
import ru.madbrains.data.network.response.GetRouteResp

interface InspectionApi {

    // region routes
    @POST("/api/dynamicdq/data/flat/mobileDetours")
    fun getRoutes(@Body request: GetRotesReq): Single<List<GetRouteResp>>
    // endregion
}
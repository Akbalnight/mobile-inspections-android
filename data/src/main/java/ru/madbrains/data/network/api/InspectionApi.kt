package ru.madbrains.data.network.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import ru.madbrains.data.network.request.*
import ru.madbrains.data.network.response.*

interface InspectionApi {

    // region routes
    @POST("/api/dynamicdq/data/flat/mobileDetours")
    fun getRoutes(@Body request: GetRotesReq): Single<List<GetRouteResp>>

    @POST("/api/dynamicdq/data/flat/mobileDetoursPlanData")
    fun getRoutePoints(@Body request: GetRoutePointsReq): Single<List<GetRoutePointResp>>
    // endregion

    // region tech operations
    @POST("/api/dynamicdq/data/flat/mobileDetoursPlanTechOperations")
    fun getPlanTechOperations(@Body request: GetPlanTechOperationsReq): Single<List<GetPlanTechOperationsResp>>
    // endregion

    // region defects

    @POST("/api/dynamicdq/data/flat/mobileDefects")
    fun getDefects(@Body request: GetDefectsReq): Single<List<GetDefectsResp>>

    @POST("/api/dynamicdq/data/flat/mobileDefectTypical")
    fun getDefectTypical(@Body request: GetDefectTypicalReq): Single<List<GetDefectTypicalResp>>
    // endregion

    // region Equipments
    @POST("/api/dynamicdq/data/flat/mobileEquipments")
    fun getEquipments(@Body request: GetEquipmentsReq): Single<List<GetEquipmentsResp>>
    // endregion
}
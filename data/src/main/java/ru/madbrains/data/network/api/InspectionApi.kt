package ru.madbrains.data.network.api

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*
import ru.madbrains.data.network.request.*
import ru.madbrains.data.network.response.*

interface InspectionApi {

    // region detours
    @POST("/api/dynamicdq/data/flat/mobileDetours")
    fun getDetours(@Body request: GetDetoursReq): Single<List<GetDetoursResp>>

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

    @Multipart
    @POST("/api/dynamicdq/mobile/saveDefects")
    fun saveDefect(@Part("defectObject") defectObject: CreateDefectReq/*, @Part files: List<MultipartBody.Part>?*/): Single<String>

    @POST("/api/dynamicdq/data/flat/mobileDefectTypical")
    fun getDefectTypical(@Body request: GetDefectTypicalReq): Single<List<GetDefectTypicalResp>>
    // endregion

    // region Equipments
    @POST("/api/dynamicdq/data/flat/mobileEquipments")
    fun getEquipments(@Body request: GetEquipmentsReq): Single<List<GetEquipmentsResp>>
    // endregion
}
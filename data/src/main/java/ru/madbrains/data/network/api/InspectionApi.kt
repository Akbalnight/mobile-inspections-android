package ru.madbrains.data.network.api

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.madbrains.data.network.request.*
import ru.madbrains.data.network.response.*
import ru.madbrains.domain.model.DetourModel

interface InspectionApi {

    // region detours
    @POST("/api/dynamicdq/data/flat/mobileDetours")
    fun getDetours(@Body request: GetDetoursReq): Single<List<GetDetoursResp>>

    @POST("/api/dynamicdq/data/flat/mobileDetoursPlanData")
    fun getRoutePoints(@Body request: GetRoutePointsReq): Single<List<GetRoutePointResp>>

    @POST("/api/dynamicdq/mobile/detours")
    fun saveDetour(@Body detour: DetourModel): Completable

    @POST("/api/dynamicdq/data/flat/mobileDetoursFreeze")
    fun freezeDetours(@Body request: FreezeDetoursReq): Completable
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
    fun saveDefect(
        @Part("defectObject") defectObject: CreateDefectReq,
        @Part files: List<MultipartBody.Part>?
    ): Single<String>

    @POST("/api/dynamicdq/data/flat/mobileDefectTypical")
    fun getDefectTypical(@Body request: GetDefectTypicalReq): Single<List<GetDefectTypicalResp>>
    // endregion

    // region Equipments
    @POST("/api/dynamicdq/data/flat/mobileEquipments")
    fun getEquipments(@Body request: GetEquipmentsReq): Single<List<GetEquipmentResp>>
    // endregion
}
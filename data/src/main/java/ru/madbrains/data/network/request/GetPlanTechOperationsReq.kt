package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPlanTechOperationsReq(
    @field:Json(name = "dataId") val dataId: String
)
package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetRoutePointResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "row_number") val rowNumber: String,
    @field:Json(name = "controlPointId") val controlPointId: String?,
    @field:Json(name = "controlPointName") val controlPointName: String?,
    @field:Json(name = "techMapName") val techMapName: String?,
    @field:Json(name = "detoursId") val detoursId: String?,
    @field:Json(name = "position") val position: Int?,
    @field:Json(name = "duration") val duration: Int?
)
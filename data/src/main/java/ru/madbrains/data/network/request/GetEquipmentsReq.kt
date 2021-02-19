package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetEquipmentsReq(
    @field:Json(name = "names") val names: List<String>,
    @field:Json(name = "controlPointIds") val controlPointIds: List<String>
)
package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDefectsReq(
    @field:Json(name = "detourIds") val detourIds: List<String>
)
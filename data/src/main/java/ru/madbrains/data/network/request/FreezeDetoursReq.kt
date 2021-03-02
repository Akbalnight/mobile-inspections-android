package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FreezeDetoursReq(
    @field:Json(name = "detoursId") val detourIds: List<String>
)
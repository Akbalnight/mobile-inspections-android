package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDetoursReq(
    @field:Json(name = "id") val id: String? = null
)
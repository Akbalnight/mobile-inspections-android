package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDetoursReq(
    @field:Json(name = "staffIds") val staffIds: List<String>,
    @field:Json(name = "statusIds") val statusIds: List<String>? = null,
    @field:Json(name = "dateBegin") val dateBegin: String? = null,
    @field:Json(name = "dateEnd") val dateEnd: String? = null
)
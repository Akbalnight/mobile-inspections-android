package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckpointUpdateReq(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "rfidCode") val rfidCode: String
)
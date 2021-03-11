package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogoutReq(
    @field:Json(name = "token") val accessToken: String
)
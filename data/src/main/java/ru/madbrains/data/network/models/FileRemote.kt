package ru.madbrains.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class FileRemote(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "fileId") val fileId: String?,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "extension") val extension: String,
    @field:Json(name = "ts") val date: Date?,
    @field:Json(name = "routeMapName") val routeMapName: String?
)
package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateDefectReq(
    @field:Json(name = "id") val id: String? = null,
    @field:Json(name = "statusProcessId") val statusProcessId: String? = null,
    @field:Json(name = "extraData") val extraData: UpdateExtraDefectReq? = null
)

@JsonClass(generateAdapter = true)
data class UpdateExtraDefectReq(
    @field:Json(name = "dateDetectDefect") val dateDetectDefect: String? = null,
    @field:Json(name = "staffDetectId") val staffDetectId: String? = null,
    @field:Json(name = "description") val description: String? = null,
    @field:Json(name = "detoursId") val detoursId: String? = null
)
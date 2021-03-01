package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDefectReq(
        @field:Json(name = "detoursId") val detoursId: String? = null,
        @field:Json(name = "equipmentId") val equipmentId: String? = null,
        @field:Json(name = "staffDetectId") val staffDetectId: String? = null,
        @field:Json(name = "defectTypicalId") val defectTypicalId: String? = null,
        @field:Json(name = "description") val description: String? = null,
        @field:Json(name = "dateDetectDefect") val dateDetectDefect: String? = null
)
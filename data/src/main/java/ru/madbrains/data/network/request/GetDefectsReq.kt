package ru.madbrains.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDefectsReq(
        @field:Json(name = "id") val id: String? = null,
        @field:Json(name = "codes") val codes: List<String>? = null,
        @field:Json(name = "dateDetectStart") val dateDetectStart: String? = null,
        @field:Json(name = "dateDetectEnd") val dateDetectEnd: String? = null,
        @field:Json(name = "detourIds") val detourIds: List<String>? = null,
        @field:Json(name = "defectNames") val defectNames: List<String>? = null,
        @field:Json(name = "equipmentNames") val equipmentNames: List<String>? = null,
        @field:Json(name = "equipmentIds") val equipmentIds: List<String>? = null,
        @field:Json(name = "statusProcessId") val statusProcessId: String? = null
)
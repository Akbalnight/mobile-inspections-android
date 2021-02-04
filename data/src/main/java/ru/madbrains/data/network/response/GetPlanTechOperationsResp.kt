package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPlanTechOperationsResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "dataId") val dataId: String,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "needInputData") val needInputData: Boolean?,
    @field:Json(name = "labelInputData") val labelInputData: String?,
    @field:Json(name = "techMapName") val techMapName: String?
)
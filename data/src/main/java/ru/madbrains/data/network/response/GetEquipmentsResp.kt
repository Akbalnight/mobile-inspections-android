package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class GetEquipmentsResp(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "is_group") val isGroup: Boolean?,
    @field:Json(name = "controlPointId") val controlPointId: String?,
    @field:Json(name = "markName") val markName: String?,
    @field:Json(name = "modelName") val modelName: String?,
    @field:Json(name = "defects") val defects: String?,
    @field:Json(name = "equipmentFiles") val equipmentFiles: String?
)
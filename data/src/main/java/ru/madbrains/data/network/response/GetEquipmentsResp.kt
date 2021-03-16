package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetEquipmentResp(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "code") val code: Int?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "parentId") val parentId: String?,
    @field:Json(name = "isGroup") val isGroup: Boolean?,
    @field:Json(name = "techPlace") val techPlace: String?,
    @field:Json(name = "techPlacePath") val techPlacePath: String?,
    @field:Json(name = "sapId") val sapId: String?,
    @field:Json(name = "constructionType") val constructionType: String?,
    @field:Json(name = "material") val material: String?,
    @field:Json(name = "size") val size: String?,
    @field:Json(name = "weight") val weight: String?,
    @field:Json(name = "manufacturer") val manufacturer: String?,
    @field:Json(name = "dateFinish") val dateFinish: String?,
    @field:Json(name = "measuringPoints") val measuringPoints: String?,
    @field:Json(name = "dateWarrantyStart") val dateWarrantyStart: String?,
    @field:Json(name = "dateWarrantyFinish") val dateWarrantyFinish: String?,
    @field:Json(name = "typeEquipment") val typeEquipment: String?,
    @field:Json(name = "deleted") val deleted: Boolean?,
    @field:Json(name = "warrantyFiles") val warrantyFiles: List<GetFileResp>?,
    @field:Json(name = "attachmentFiles") val attachmentFiles: List<GetFileResp>?
)
package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.madbrains.domain.model.ExtraDataModel
import ru.madbrains.domain.model.FileModel
import java.io.Serializable
import java.util.*

@JsonClass(generateAdapter = true)
data class GetCheckpointResp(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "code") val code: Int,
        @field:Json(name = "name") val name: String,
        @field:Json(name = "parentId") val parentId: String?,
        @field:Json(name = "parentName") val parentName: String?,
        @field:Json(name = "rfidCode") val rfidCode: String?
)
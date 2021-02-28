package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.madbrains.domain.model.ExtraDataModel
import ru.madbrains.domain.model.FileModel
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class GetFileResp(
        @field:Json(name = "id") val id: String?,
        @field:Json(name = "someIdDef") val someIdDef: String?,
        @field:Json(name = "fileId") val fileId: String?
)
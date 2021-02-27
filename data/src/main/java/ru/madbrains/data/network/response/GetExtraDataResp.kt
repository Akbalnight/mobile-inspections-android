package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.madbrains.domain.model.ExtraDataModel
import ru.madbrains.domain.model.FileModel
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class GetExtraDataResp(
        @field:Json(name = "dateDetectDefect") val dateDetectDefect: String?,
        @field:Json(name = "staffDetectId") val staffDetectId: String?,
        @field:Json(name = "description") val description: String?,
        @field:Json(name = "detoursId") val detoursId: String?
)
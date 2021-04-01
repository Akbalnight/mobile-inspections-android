package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.madbrains.domain.model.ExtraDataModel
import ru.madbrains.domain.model.FileModel
import java.io.Serializable
import java.util.*

@JsonClass(generateAdapter = true)
data class GetDefectsResp(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "equipmentId") val equipmentId: String?,
        @field:Json(name = "staffDetectId") val staffDetectId: String?,
        @field:Json(name = "defectTypicalId") val defectTypicalId: String?,
        @field:Json(name = "description") val description: String?,
        @field:Json(name = "dateDetectDefect") val dateDetectDefect: Date?,
        @field:Json(name = "detourId") val detourId: String?,
        @field:Json(name = "files") val files: List<GetFileResp>?,
        @field:Json(name = "defectName") val defectName: String?,
        @field:Json(name = "equipmentName") val equipmentName: String?,
        @field:Json(name = "statusProcessId") val statusProcessId: String?,
        @field:Json(name = "extraData") val extraData: List<GetExtraDataResp>?
)
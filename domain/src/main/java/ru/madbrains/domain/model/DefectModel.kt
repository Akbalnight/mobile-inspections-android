package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

//TODO: refact
data class DefectModel(
    val id: String,
    val equipmentId: String?,
    val staffDetectId: String?,
    val defectTypicalId: String?,
    val description: String?,
    val dateDetectDefect: Date?,
    val detourId: String?,
    var files: List<FileModel>?,
    val defectName: String?,
    val equipmentName: String?,
    val statusProcessId: String?,
    val extraData: List<ExtraDataModel>?,
    var created: Boolean,
    var changed: Boolean
) : Serializable {

    fun getLastDateConfirm(): Date? {
        extraData?.let { extraList ->
            val sortList = extraList.sortedBy {
                it.dateDetectDefect
            }
            if (!sortList.isNullOrEmpty()) {
                return sortList.last().dateDetectDefect
            }
        }
        return dateDetectDefect
    }
}

fun List<DefectModel>.getAllFilesIds(): List<String> {
    val res = arrayListOf<FileModel>()
    for (item in this) {
        item.files?.let {
            res.addAll(it)
        }
    }
    return res.mapNotNull { it.fileId }.distinct()
}

data class ExtraDataModel(
    val dateDetectDefect: Date?,
    val staffDetectId: String?,
    val description: String?,
    val detoursId: String?
) : Serializable
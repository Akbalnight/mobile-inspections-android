package ru.madbrains.domain.model

import java.io.Serializable

data class DefectModel(
        val id: String,
        val equipmentId: String?,
        val staffDetectId: String?,
        val defectTypicalId: String?,
        val description: String?,
        val dateDetectDefect: String?,
        val detourId: String?,
        val files: List<FileModel>?,
        val defectName: String?,
        val equipmentName: String?,
        val statusProcessId: String?,
        val extraData: List<ExtraDataModel>?
) : Serializable
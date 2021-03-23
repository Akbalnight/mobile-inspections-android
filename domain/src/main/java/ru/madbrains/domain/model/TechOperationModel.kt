package ru.madbrains.domain.model

import java.io.Serializable

data class TechOperationModel(
    val id: String,
    val name: String?,
    val code: Int?,
    val needInputData: Boolean?,
    val labelInputData: String?,
    var valueInputData: String?,
    val equipmentStop: Boolean?,
    val increasedDanger: Boolean?,
    val duration: Int?,
    val techMapId: String?,
    val position: Int?
): Serializable
package ru.madbrains.domain.model

data class TechOperationModel(
    val id: String,
    val name: String?,
    val code: Int?,
    val needInputData: Boolean?,
    val labelInputData: String?,
    val valueInputData: String?,
    val equipmentStop: Boolean?,
    val increasedDanger: Boolean?,
    val duration: Int?,
    val techMapId: String?,
    val position: Int?
)
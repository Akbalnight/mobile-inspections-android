package ru.madbrains.domain.model

data class PlanTechOperationsModel(
    val id: String,
    val position: Int?,
    val dataId: String,
    val name: String?,
    val needInputData: Boolean?,
    val labelInputData: String?,
    val techMapName: String?
)
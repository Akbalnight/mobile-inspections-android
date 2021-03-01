package ru.madbrains.domain.model

import java.io.Serializable

data class TechMapModel(
    val id: String,
    val name: String?,
    val code: Int?,
    val dateStart: String?,
    val techMapsStatusId: String?,
    val parentId: String?,
    val isGroup: Boolean?,
    val techOperations: List<TechOperationModel>
) : Serializable {
    var pointNumber: Int? = null
}
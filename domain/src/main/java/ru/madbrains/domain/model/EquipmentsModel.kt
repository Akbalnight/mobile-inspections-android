package ru.madbrains.domain.model

import java.io.Serializable

data class EquipmentsModel(
        val id: String?,
        val name: String?,
        val isGroup: Boolean?,
        val controlPointId: String?,
        val markName: String?,
        val modelName: String?,
        val defects: String?,
        val equipmentFiles: String?
) : Serializable




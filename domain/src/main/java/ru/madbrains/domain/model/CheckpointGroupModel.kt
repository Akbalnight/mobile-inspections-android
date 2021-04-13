package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

data class CheckpointGroupModel(
    val parentId: String?,
    val parentName: String?,
    val points: List<CheckpointModel>
): Serializable

data class CheckpointModel(
    val id: String,
    val code: Int,
    val name: String,
    val rfidCode: String?
): Serializable

package ru.madbrains.domain.model

import java.io.Serializable

data class CheckpointModel(
    val id: String,
    val code: Int,
    val name: String,
    val rfidCode: String?
) : Serializable

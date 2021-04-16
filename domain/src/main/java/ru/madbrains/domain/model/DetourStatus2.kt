package ru.madbrains.domain.model

import java.io.Serializable

data class DetourStatus2(
        val id: String?,
        val name: String?,
        val code: Int?
) : Serializable

data class DetourStatusHolder(
        val statuses: List<DetourStatus2>
) : Serializable
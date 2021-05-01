package ru.madbrains.domain.model

import java.io.Serializable

data class DetourStatus(
    val id: String,
    val name: String?,
    val code: Int?
) : Serializable {
    val type: DetourStatusType
        get(): DetourStatusType {
            return when (code) {
                1 -> DetourStatusType.PENDING
                2 -> DetourStatusType.IN_PROGRESS
                3 -> DetourStatusType.NOT_COMPLETED
                4 -> DetourStatusType.COMPLETED
                5 -> DetourStatusType.COMPLETED_AHEAD
                6 -> DetourStatusType.PAUSED
                7 -> DetourStatusType.NEW
                else -> DetourStatusType.UNKNOWN
            }
        }
}

data class DetourStatusHolder(
    val data: List<DetourStatus>
) : Serializable

fun List<DetourStatus>.getStatusById(id: String?): DetourStatus? {
    return find { it.id == id }
}

fun List<DetourStatus>.isEditable(id: String?): Boolean {
    val type = getStatusById(id)?.type
    val completedStatuses = arrayOf(DetourStatusType.COMPLETED_AHEAD, DetourStatusType.COMPLETED)
    return type !in completedStatuses
}

fun List<DetourStatus>.getStatusesByType(listTypes: Array<DetourStatusType>): List<DetourStatus> {
    return filter {
        it.type in listTypes
    }
}

fun List<DetourStatus>.getStatusByType(type: DetourStatusType?): DetourStatus? {
    return find {
        it.type == type
    }
}

enum class DetourStatusType {
    PENDING,
    COMPLETED,
    NOT_COMPLETED,
    IN_PROGRESS,
    COMPLETED_AHEAD,
    PAUSED,
    NEW,
    UNKNOWN,
}
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
) : Serializable {
    private fun isInStatus(id: String?, statuses: Set<DetourStatusType>): Boolean {
        val type = getStatusById(id)?.type
        return type in statuses
    }

    fun isNew(id: String?): Boolean {
        return isInStatus(id, setOf(DetourStatusType.NEW))
    }

    fun isCompleted(id: String?): Boolean {
        return isInStatus(id, setOf(DetourStatusType.COMPLETED_AHEAD, DetourStatusType.COMPLETED))
    }

    fun isInProgress(id: String?): Boolean {
        return isInStatus(id, setOf(DetourStatusType.IN_PROGRESS))
    }

    fun getStatusById(id: String?): DetourStatus? {
        return data.find { it.id == id }
    }

    fun getStatusByType(type: DetourStatusType?): DetourStatus? {
        return data.find {
            it.type == type
        }
    }

    fun getStatusesByType(listTypes: Array<DetourStatusType>): List<DetourStatus> {
        return data.filter {
            it.type in listTypes
        }
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
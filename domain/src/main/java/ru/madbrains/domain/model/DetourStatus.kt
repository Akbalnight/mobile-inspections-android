package ru.madbrains.domain.model

import java.io.Serializable

data class DetourStatus(
        val id: String?,
        val name: String?,
        val code: Int?
) : Serializable{
        val type: DetourStatusType get(): DetourStatusType{
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
        val statuses: List<DetourStatus>
) : Serializable{
        fun getStatusById(id:String?): DetourStatus?{
                return statuses.find {
                        it.id == id
                }
        }
        fun getStatusByType(type:DetourStatusType?): DetourStatus?{
                return statuses.find {
                        it.type == type
                }
        }
        fun isEditable(id:String?):Boolean{
                val type = getStatusById(id)?.type
                return DetourStatusType.COMPLETED == type || DetourStatusType.COMPLETED_AHEAD == type
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
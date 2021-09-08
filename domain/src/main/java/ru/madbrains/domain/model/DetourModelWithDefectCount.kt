package ru.madbrains.domain.model

data class DetourModelWithDefectCount(
    val data: DetourModel,
    val defectCount: Int
)
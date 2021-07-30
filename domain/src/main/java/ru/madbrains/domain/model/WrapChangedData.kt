package ru.madbrains.domain.model

data class WrapChangedData(
    val detours: List<DetourModel>,
    val defects: List<DefectModel>,
    val checkpoints: List<CheckpointModel>
)
package ru.madbrains.domain.model

import java.io.File

data class WrapPendingDataSync(
    var routes: List<DetourModel>? = null,
    var defects: List<DefectModel>? = null,
    var equipment: List<EquipmentModel>? = null,
    var defectsTypical: List<DefectTypicalModel>? = null,
    var checkpoints: List<CheckpointModel>? = null,
    var mediaArchive: File? = null,
    var docArchive: File? = null
)
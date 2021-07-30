package ru.madbrains.domain.model

data class WrapEtcSync(
    var equipment: List<EquipmentModel>,
    var defectsTypical: List<DefectTypicalModel>,
    var checkpoints: List<CheckpointModel>
)
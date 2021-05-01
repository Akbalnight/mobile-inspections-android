package ru.madbrains.inspection.ui.adapters

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.madbrains.inspection.base.BaseDiffCallback
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.EquipmentListImageUiModel
import ru.madbrains.inspection.ui.delegates.equipmentListMediaDelegate

class EquipmentListMediaAdapter(
    onMediaImageClick: ((EquipmentListImageUiModel) -> Unit)?
) : AsyncListDifferDelegationAdapter<DiffItem>(BaseDiffCallback()) {

    init {
        delegatesManager
            .addDelegate(equipmentListMediaDelegate(onMediaImageClick))
    }
}
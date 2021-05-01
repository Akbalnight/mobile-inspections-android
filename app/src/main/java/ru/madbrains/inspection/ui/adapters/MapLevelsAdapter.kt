package ru.madbrains.inspection.ui.adapters

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.madbrains.inspection.base.BaseDiffCallback
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.MapLevelUiModel
import ru.madbrains.inspection.ui.delegates.mapLevelDelegate

class MapLevelsAdapter(
    onItemClick: (MapLevelUiModel) -> Unit
) : AsyncListDifferDelegationAdapter<DiffItem>(BaseDiffCallback()) {

    init {
        delegatesManager
            .addDelegate(mapLevelDelegate(onItemClick))
    }
}
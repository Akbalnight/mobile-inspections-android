package ru.madbrains.inspection.ui.adapters

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.madbrains.inspection.base.BaseDiffCallback
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.*

class MediaAdapter(
    onMediaImageClick: (MediaUiModel) -> Unit,
    onMediaDeleteClick: (MediaUiModel) -> Unit
) : AsyncListDifferDelegationAdapter<DiffItem>(BaseDiffCallback()) {

    init {
        delegatesManager
                .addDelegate(mediaDelegate(onMediaImageClick, onMediaDeleteClick))
    }
}
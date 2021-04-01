package ru.madbrains.inspection.ui.adapters

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.madbrains.inspection.base.BaseDiffCallback
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DefectListUiModel
import ru.madbrains.inspection.ui.delegates.defectListDelegate

class DefectListAdapter(
        onEditClick: (DefectListUiModel) -> Unit,
        onDeleteClick: (DefectListUiModel) -> Unit,
        onConfirmClick: (DefectListUiModel) -> Unit,
        onEliminatedClick: (DefectListUiModel) -> Unit
) : AsyncListDifferDelegationAdapter<DiffItem>(BaseDiffCallback()) {

    init {
        delegatesManager
                .addDelegate(defectListDelegate(onEditClick, onDeleteClick, onConfirmClick, onEliminatedClick))
    }
}
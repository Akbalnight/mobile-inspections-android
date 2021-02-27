package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_defect.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun defectListDelegate(clickListener: (DefectListUiModel) -> Unit) =
        adapterDelegateLayoutContainer<DefectListUiModel, DiffItem>(R.layout.item_defect) {

            bind {
                itemView.apply {

                    tvTitleDate.text = item.name

                }
            }
        }

data class DefectListUiModel(
        val id: String,
        val name: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is DefectListUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
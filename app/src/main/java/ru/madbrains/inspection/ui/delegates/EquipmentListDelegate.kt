package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_equipment.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun equipmentListDelegate(clickListener: (EquipmentListUiModel) -> Unit) =
        adapterDelegateLayoutContainer<EquipmentListUiModel, DiffItem>(R.layout.item_equipment) {
            bind {
                itemView.apply {
                    tvTitleDate.text = item.name
                }
            }
        }

data class EquipmentListUiModel(
        val id: String,
        val name: String?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is EquipmentListUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
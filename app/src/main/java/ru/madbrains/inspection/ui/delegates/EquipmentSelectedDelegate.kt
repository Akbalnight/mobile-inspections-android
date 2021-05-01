package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_equipment_select.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun equipmentSelectDelegate(clickListener: (EquipmentSelectUiModel) -> Unit) =
    adapterDelegateLayoutContainer<EquipmentSelectUiModel, DiffItem>(R.layout.item_equipment_select) {

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
                ivSelectedStatus.visibility = if (item.isSelected) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        }
    }

data class EquipmentSelectUiModel(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is EquipmentSelectUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

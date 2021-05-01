package ru.madbrains.inspection.ui.delegates

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_equipment.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.adapters.EquipmentListMediaAdapter
import java.io.File

fun equipmentListDelegate(clickListener: (EquipmentListUiModel) -> Unit) =
    adapterDelegateLayoutContainer<EquipmentListUiModel, DiffItem>(R.layout.item_equipment) {
        bind {
            itemView.apply {
                tvEquipmentType.text = item.type ?: "-"
                tvEquipmentName.text = item.name ?: "-"
                clCard.setOnClickListener { clickListener(item) }
                rvEquipmentMedia.apply {
                    isVisible = true
                    adapter = EquipmentListMediaAdapter(onMediaImageClick = null).apply {
                        items = item.images?.map {
                            EquipmentListImageUiModel(it)
                        }
                    }
                    setOnClickListener { clickListener(item) }
                }
            }
        }
    }

data class EquipmentListUiModel(
    val id: String,
    val name: String?,
    val type: String?,
    val images: List<File?>?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is EquipmentListUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
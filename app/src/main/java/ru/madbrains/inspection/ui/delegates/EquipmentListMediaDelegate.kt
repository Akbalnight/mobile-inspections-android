package ru.madbrains.inspection.ui.delegates

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_media_equipment.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun equipmentListMediaDelegate(clickImageListener: ((EquipmentListImageUiModel) -> Unit)?) =
        adapterDelegateLayoutContainer<EquipmentListImageUiModel, DiffItem>(R.layout.item_media_equipment) {

            bind {
                itemView.apply {
                    Glide.with(ivMediaContent)
                        .load(item.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_item_media_padded)
                        .into(ivMediaContent)

                    clickImageListener?.run {
                        ivMediaContent.setOnClickListener { invoke(item) }
                    }
                }
            }
        }

data class EquipmentListImageUiModel(
        val url: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is EquipmentListImageUiModel && url == newItem.url

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
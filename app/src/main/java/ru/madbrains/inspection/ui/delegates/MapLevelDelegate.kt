package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_map_level.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun mapLevelDelegate(clickListener: (MapLevelUiModel) -> Unit) =
        adapterDelegateLayoutContainer<MapLevelUiModel, DiffItem>(R.layout.item_map_level) {

            bind {
                itemView.apply {
                    setOnClickListener {
                        clickListener(item)
                    }

                    if (item.isActive) {
                        ivSelected.visibility = View.VISIBLE
                    } else {
                        ivSelected.visibility = View.INVISIBLE
                    }
                    tvLabel.text = item.id
                }
            }
        }

data class MapLevelUiModel(
        val id: String,
        val isActive: Boolean
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is MapLevelUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
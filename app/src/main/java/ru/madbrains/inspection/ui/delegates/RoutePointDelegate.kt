package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_route_point.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun routePointDelegate(clickListener: (RoutePointUiModel) -> Unit) =
    adapterDelegateLayoutContainer<RoutePointUiModel, DiffItem>(R.layout.item_route_point) {

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvPointNumber.text = (adapterPosition + 1).toString()
                tvPointName.text = item.name
            }
        }
    }

data class RoutePointUiModel(
    val id: String,
    val name: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RoutePointUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
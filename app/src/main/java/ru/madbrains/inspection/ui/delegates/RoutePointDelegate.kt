package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_route_point.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.extensions.colors
import ru.madbrains.inspection.extensions.drawables

fun routePointDelegate(clickListener: (RoutePointUiModel) -> Unit) =
    adapterDelegateLayoutContainer<RoutePointUiModel, DiffItem>(R.layout.item_route_point) {

        bind {
            itemView.apply {
                if (item.clickable) clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvPointNumber.text = item.position.toString()
                tvPointName.text = item.name
                tvPointNumber.background =
                    when {
                        item.completed && item.haveDefects -> context.drawables[R.drawable.light_red_circle]
                        item.completed && !item.haveDefects -> context.drawables[R.drawable.light_green_circle]
                        else -> context.drawables[R.drawable.light_blue_circle]
                    }
            }
        }
    }

data class RoutePointUiModel(
    val id: String,
    val parentId: String?,
    val name: String,
    val position: Int?,
    val completed: Boolean,
    val haveDefects: Boolean,
    val clickable: Boolean
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RoutePointUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
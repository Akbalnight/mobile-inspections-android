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
                if (item.clickable) {
                    clContainer.setOnClickListener { clickListener.invoke(item) }
                    clContainer.isClickable = true
                } else {
                    clContainer.setOnClickListener(null)
                    clContainer.isClickable = false
                }

                tvPointNumber.text = item.position.toString()
                tvPointName.text = item.name
                tvPointNumber.background =
                    if (item.completed) context.drawables[R.drawable.light_blue_circle]
                    else context.drawables[R.drawable.light_grey_circle]
                if (item.completed) tvPointNumber.setTextColor(context.colors[R.color.textWhite])
                else tvPointNumber.setTextColor(context.colors[R.color.textMain])

            }
        }
    }

data class RoutePointUiModel(
    val id: String,
    val parentId: String?,
    val name: String,
    val position: Int?,
    val completed: Boolean = false,
    val clickable: Boolean
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RoutePointUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
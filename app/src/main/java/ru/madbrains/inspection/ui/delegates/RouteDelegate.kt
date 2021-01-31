package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_route.view.*
import ru.madbrains.domain.model.RouteStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun routeDelegate(clickListener: (RouteUiModel) -> Unit) =
    adapterDelegateLayoutContainer<RouteUiModel, DiffItem>(R.layout.item_route) {

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
                val routeImageStatus = when (item.status) {
                    RouteStatus.PENDING -> R.drawable.ic_route_pending
                    RouteStatus.COMPLETED -> R.drawable.ic_route_completed
                    RouteStatus.NOT_COMPLETED -> R.drawable.ic_route_not_completed
                    RouteStatus.IN_PROGRESS -> R.drawable.ic_route_in_progress
                    RouteStatus.COMPLETED_AHEAD -> R.drawable.ic_route_completed
                    else -> null
                }
                routeImageStatus?.let {
                    ivRouteStatus.setImageResource(routeImageStatus)
                }
                tvDate.text = item.date.replace("T", " ")
            }
        }
    }

data class RouteUiModel(
    val id: String,
    val name: String,
    val status: RouteStatus?,
    val date: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RouteUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
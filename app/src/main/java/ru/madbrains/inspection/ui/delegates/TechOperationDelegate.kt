package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_route.view.*
import ru.madbrains.domain.model.RouteStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun techOperationDelegate() =
    adapterDelegateLayoutContainer<TechOperationUiModel, DiffItem>(R.layout.item_tech_operations) {

        bind {
            itemView.apply {
                tvName.text = item.name
            }
        }
    }

data class TechOperationUiModel(
    val id: String,
    val name: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RouteUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
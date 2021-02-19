package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_device_select.view.*
import kotlinx.android.synthetic.main.item_route.view.tvName
import kotlinx.android.synthetic.main.item_tech_operations.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun deviceSelectDelegate() =
    adapterDelegateLayoutContainer<DeviceSelectUiModel, DiffItem>(R.layout.item_device_select) {

        bind {
            itemView.apply {
                tvName.text = item.name
            }
        }
    }

data class DeviceSelectUiModel(
    val id: String,
    val name: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RouteUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

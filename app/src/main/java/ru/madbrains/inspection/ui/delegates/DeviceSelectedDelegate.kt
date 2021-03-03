package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_device_select.view.*
import kotlinx.android.synthetic.main.item_device_select.view.clContainer
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun deviceSelectDelegate(clickListener: (DeviceSelectUiModel) -> Unit) =
    adapterDelegateLayoutContainer<DeviceSelectUiModel, DiffItem>(R.layout.item_device_select) {

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
            }
        }
    }

data class DeviceSelectUiModel(
    val id: String,
    val name: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is DeviceSelectUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

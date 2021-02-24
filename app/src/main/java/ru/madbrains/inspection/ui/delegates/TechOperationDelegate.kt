package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_tech_operations.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun techOperationDelegate() =
    adapterDelegateLayoutContainer<TechOperationUiModel, DiffItem>(R.layout.item_tech_operations) {

        bind {
            itemView.apply {
                var name: String = item.name
                item.position?.let {
                    name = "$it. $name"
                }
                tvName.text = name
                item.needInputData?.let {
                    if (it) {
                        item.labelInputData?.let { label ->
                            tvInputData.visibility = View.VISIBLE
                            tvInputData.text = label
                            etInputData.visibility = View.VISIBLE
                        }
                    } else {
                        tvInputData.visibility = View.GONE
                        etInputData.visibility = View.GONE
                    }
                }
            }
        }
    }

data class TechOperationUiModel(
    val id: String,
    val name: String,
    val needInputData: Boolean?,
    val labelInputData: String?,
    val position: Int?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is RouteUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

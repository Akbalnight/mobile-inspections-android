package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_checkpoint.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun checkpointDelegate(clickListener: (CheckpointUiModel) -> Unit) =
    adapterDelegateLayoutContainer<CheckpointUiModel, DiffItem>(R.layout.item_checkpoint) {

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
                ivRfid.visibility = if (item.hasRfid) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        }
    }

data class CheckpointUiModel(
    val id: String?,
    val name: String,
    val hasRfid: Boolean = false
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is CheckpointUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

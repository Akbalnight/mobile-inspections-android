package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_file.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun filesDelegate(clickListener: (FilesUiModel) -> Unit) =
    adapterDelegateLayoutContainer<FilesUiModel, DiffItem>(R.layout.item_file) {
        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
                tvDate.text = item.date
                tvExt.text = item.extension
            }
        }
    }

data class FilesUiModel(
    val id: String,
    val date: String,
    val url: String,
    val name: String?,
    val extension: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is FilesUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
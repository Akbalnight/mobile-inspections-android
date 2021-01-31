package ru.madbrains.inspection.base

import androidx.recyclerview.widget.DiffUtil
import ru.madbrains.inspection.base.model.DiffItem

class BaseDiffCallback : DiffUtil.ItemCallback<DiffItem>() {

    override fun areItemsTheSame(oldItem: DiffItem, newItem: DiffItem): Boolean =
        oldItem.areItemsTheSame(newItem)

    override fun areContentsTheSame(oldItem: DiffItem, newItem: DiffItem): Boolean =
        oldItem.areContentsTheSame(newItem)
}
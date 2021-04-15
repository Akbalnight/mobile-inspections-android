package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_detour.view.*
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun detourDelegate(clickListener: (DetourUiModel) -> Unit) =
    adapterDelegateLayoutContainer<DetourUiModel, DiffItem>(R.layout.item_detour) {

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
                val detourImageStatus = when (item.status) {
                    DetourStatus.NEW -> R.drawable.ic_detour_new
                    DetourStatus.COMPLETED -> R.drawable.ic_detour_completed
                    DetourStatus.NOT_COMPLETED -> R.drawable.ic_detour_not_completed
                    DetourStatus.IN_PROGRESS -> R.drawable.ic_detour_in_progress
                    DetourStatus.COMPLETED_AHEAD -> R.drawable.ic_detour_completed
                    DetourStatus.PAUSED -> R.drawable.ic_detour_paused
                    else -> null
                }
                detourImageStatus?.let {
                    ivDetourStatus.setImageResource(detourImageStatus)
                }
                tvDate.text = item.date.replace("T", " ")
            }
        }
    }

data class DetourUiModel(
    val id: String,
    val name: String,
    val status: DetourStatus?,
    val date: String
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is DetourUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
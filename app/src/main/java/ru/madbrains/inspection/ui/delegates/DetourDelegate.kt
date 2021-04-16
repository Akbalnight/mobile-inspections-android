package ru.madbrains.inspection.ui.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_detour.view.*
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.domain.model.DetourStatusType
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
                item.status?.type?.icon()?.let {
                    ivDetourStatus.setImageResource(it)
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

fun DetourStatusType.icon(): Int{
    return when(this){
        DetourStatusType.PENDING -> R.drawable.ic_detour_pending
        DetourStatusType.COMPLETED -> R.drawable.ic_detour_completed
        DetourStatusType.NOT_COMPLETED -> R.drawable.ic_detour_not_completed
        DetourStatusType.IN_PROGRESS -> R.drawable.ic_detour_in_progress
        DetourStatusType.COMPLETED_AHEAD -> R.drawable.ic_detour_completed
        DetourStatusType.PAUSED -> R.drawable.ic_detour_paused
        //TODO: add icons
        DetourStatusType.NEW -> R.drawable.ic_defect_card_media
        DetourStatusType.UNKNOWN -> R.drawable.ic_defect_card_media
    }
}
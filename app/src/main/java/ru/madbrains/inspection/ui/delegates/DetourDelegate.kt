package ru.madbrains.inspection.ui.delegates

import android.widget.Toast
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_detour.view.*
import org.joda.time.DateTime
import ru.madbrains.data.extensions.toyyyyMMddHHmmss
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.domain.model.DetourStatusType
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import java.util.*

fun detourDelegate(clickListener: (DetourUiModel) -> Unit) =
    adapterDelegateLayoutContainer<DetourUiModel, DiffItem>(R.layout.item_detour) {

        bind {
            itemView.apply {

                val isExpired = if (item.dateStartPlan != null) {
                    DateTime(item.dateStartPlan).isBeforeNow
                } else {
                    false
                }


                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                tvName.text = item.name
                ivIcon.setImageResource(item.status?.type?.icon() ?: 0)
                ivExpired.isVisible = isExpired
                ivExpired.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        R.string.fragment_routes_expired_time,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                tvDate.text = item.dateStartPlan?.toyyyyMMddHHmmss()
            }
        }
    }

data class DetourUiModel(
    val id: String,
    val name: String,
    val status: DetourStatus?,
    val dateStartPlan: Date?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is DetourUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

fun DetourStatusType.icon(): Int {
    return when (this) {
        DetourStatusType.PENDING -> R.drawable.ic_detour_pending
        DetourStatusType.COMPLETED -> R.drawable.ic_detour_completed
        DetourStatusType.NOT_COMPLETED -> R.drawable.ic_detour_not_completed
        DetourStatusType.IN_PROGRESS -> R.drawable.ic_detour_in_progress
        DetourStatusType.COMPLETED_AHEAD -> R.drawable.ic_detour_completed
        DetourStatusType.PAUSED -> R.drawable.ic_detour_paused
        DetourStatusType.NEW -> R.drawable.ic_detour_new
        DetourStatusType.UNKNOWN -> R.drawable.ic_defect_card_media
    }
}
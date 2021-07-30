package ru.madbrains.inspection.ui.delegates

import android.widget.Toast
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_changed_item.view.*
import org.joda.time.DateTime
import ru.madbrains.data.extensions.toyyyyMMddHHmmss
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import java.util.*

fun changedItemDelegate(clickListener: (ChangedItemUiModel) -> Unit) =
    adapterDelegateLayoutContainer<ChangedItemUiModel, DiffItem>(R.layout.item_changed_item) {

        bind {
            itemView.apply {
                val data = item
                clContainer.setOnClickListener {
                    clickListener.invoke(data)
                }
                tvName.text = data.name
                when (data) {
                    is ChangedItemUiDetour -> {
                        ivIcon.setImageResource(data.status?.type?.icon() ?: 0)
                        tvExtra.text = data.dateStartPlan?.toyyyyMMddHHmmss()
                        ivExpired.isVisible = DateTime(data.dateStartPlan).isBeforeNow
                        ivExpired.setOnClickListener {
                            Toast.makeText(
                                itemView.context,
                                R.string.fragment_routes_expired_time,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is ChangedItemUiDefect -> {
                        ivIcon.setImageResource(R.drawable.ic_defect_card_detour)
                        tvExtra.text = data.dateDetectDefect?.toyyyyMMddHHmmss()
                        ivExpired.isVisible = false
                        ivExpired.setOnClickListener(null)
                    }
                    is ChangedItemUiCheckpoint -> {
                        ivIcon.setImageResource(R.drawable.ic_rfid)
                        tvExtra.text = data.rfidCode
                        ivExpired.isVisible = false
                        ivExpired.setOnClickListener(null)
                    }
                }

            }
        }
    }

abstract class ChangedItemUiModel : DiffItem {
    abstract val id: String
    abstract val name: String

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is ChangedItemUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

data class ChangedItemUiDetour(
    override val id: String,
    override val name: String,
    val status: DetourStatus?,
    val dateStartPlan: Date?
) : ChangedItemUiModel()

data class ChangedItemUiDefect(
    override val id: String,
    override val name: String,
    val dateDetectDefect: Date?
) : ChangedItemUiModel()

data class ChangedItemUiCheckpoint(
    override val id: String,
    override val name: String,
    val rfidCode: String?
) : ChangedItemUiModel()

package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_defect.view.*
import kotlinx.android.synthetic.main.item_defect.view.clContainer
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun defectListDelegate(editItem: (DefectListUiModel) -> Unit,
                       deleteItem: (DefectListUiModel) -> Unit) =
        adapterDelegateLayoutContainer<DefectListUiModel, DiffItem>(R.layout.item_defect) {

            bind {
                itemView.apply {

                    if(item.detour.isEmpty()){
                        ivIconDetour.setImageResource(R.drawable.ic_defect_card_no_detour)
                    }
                    else {
                        ivIconDetour.setImageResource(R.drawable.ic_defect_card_detour)
                    }
                        
                    tvTitleDate.text = item.date
                    tvTime.text = item.time
                    tvDeviceData.text = item.device
                    tvTypeDefectData.text = item.type
                    tvDescriptionData.text = item.description

                    clContainer.setOnClickListener {
                        if (ivIconClosedStatus.visibility == View.VISIBLE) {
                            ivIconClosedStatus.visibility = View.GONE
                            ivIconOpenedStatus.visibility = View.VISIBLE
                            tvDescriptionTitle.visibility = View.VISIBLE
                            tvDescriptionData.visibility = View.VISIBLE
                            rvDefectMedia.visibility = View.VISIBLE
                            btnEdit.visibility = View.VISIBLE
                            btnDelete.visibility = View.VISIBLE
                            //todo
                            ivIconMedia.visibility = View.GONE

                        } else {
                            ivIconClosedStatus.visibility = View.VISIBLE
                            ivIconOpenedStatus.visibility = View.GONE
                            tvDescriptionTitle.visibility = View.GONE
                            tvDescriptionData.visibility = View.GONE
                            rvDefectMedia.visibility = View.GONE
                            btnEdit.visibility = View.GONE
                            btnDelete.visibility = View.GONE
                            ivIconMedia.visibility = View.GONE
                            ivIconMedia.visibility = View.VISIBLE
                        }
                    }

                    btnEdit.setOnClickListener {
                        editItem.invoke(item)
                    }

                    btnDelete.setOnClickListener {
                        deleteItem.invoke(item)
                    }

                }
            }
        }

data class DefectListUiModel(
        val id: String,
        val detour: String,
        val date: String,
        val time: String,
        val device: String,
        val type: String,
        val description: String

) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is DefectListUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
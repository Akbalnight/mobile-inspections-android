package ru.madbrains.inspection.ui.delegates

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_defect.view.*
import kotlinx.android.synthetic.main.item_defect.view.clContainer
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.ui.adapters.DefectMediaAdapter
import java.util.*

fun defectListDelegate(
    clickActionLeft: (DefectListUiModel) -> Unit,
    clickActionRight: (DefectListUiModel) -> Unit
) =
    adapterDelegateLayoutContainer<DefectListUiModel, DiffItem>(R.layout.item_defect) {

        bind {
            itemView.apply {
                if (item.detour.isEmpty()) {
                    ivIconDetour.setImageDrawable(context.drawables[R.drawable.ic_defect_card_no_detour])
                } else {
                    ivIconDetour.setImageDrawable(context.drawables[R.drawable.ic_defect_card_detour])
                }

                tvTitleDate.text = item.date
                tvTime.text = item.time
                tvDeviceData.text = item.device
                tvTypeDefectData.text = item.type
                tvDescriptionData.text = item.description

                clContainer.setOnClickListener {
                    if (tvDescriptionData.visibility != View.VISIBLE) {
                        ivUnfoldStatus.setImageResource(R.drawable.ic_defect_card_close)
                        tvDescriptionTitle.visibility = View.VISIBLE
                        tvDescriptionData.visibility = View.VISIBLE
                        rvDefectMedia.visibility = View.VISIBLE
                        btnActionLeft.visibility = View.VISIBLE
                        btnActionRight.visibility = View.VISIBLE
                        //todo
                        ivIconMedia.visibility = View.GONE

                    } else {
                        ivUnfoldStatus.setImageResource(R.drawable.ic_defect_card_open)
                        tvDescriptionTitle.visibility = View.GONE
                        tvDescriptionData.visibility = View.GONE
                        rvDefectMedia.visibility = View.GONE
                        btnActionLeft.visibility = View.GONE
                        btnActionRight.visibility = View.GONE
                        ivIconMedia.visibility = View.GONE
                        ivIconMedia.visibility = View.VISIBLE
                    }
                }

                btnActionLeft.setOnClickListener {
                    clickActionLeft.invoke(item)
                }

                btnActionRight.setOnClickListener {
                    clickActionRight.invoke(item)
                }


                if (item.isCommonList) {
                    btnActionLeft.text = resources?.getText(R.string.fragment_btn_label_edit)
                    btnActionRight.text = resources?.getText(R.string.fragment_btn_label_delete)
                } else {
                    btnActionLeft.text = resources?.getText(R.string.fragment_btn_label_confirm)
                    btnActionRight.text = resources?.getText(R.string.fragment_btn_label_fixed)
                }

                val mediaAdapter = DefectMediaAdapter(
                    onMediaDeleteClick = {

                    },
                    onMediaImageClick = {

                    })

              //  if(!item.images.isNullOrEmpty()){
                    rvDefectMedia.adapter = mediaAdapter


                    mediaAdapter.items = listOf(
                        MediaDefectUiModel(
                            id = UUID.randomUUID().toString(),
                            url = "https://mobinspect.dias-dev.ru/api/dynamicdq/data/file/mobileFiles/7b46c999-d2e3-4a4a-840e-a90d6b7166ac"
                        )
                    )
               // }

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
    val description: String,
    val isCommonList: Boolean,
    val images: List<MediaDefectUiModel>?

) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is DefectListUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
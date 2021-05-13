package ru.madbrains.inspection.ui.delegates

import android.view.View
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_defect.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.MediaAdapter
import timber.log.Timber

fun defectListDelegate(
    clickEdit: (DefectListUiModel) -> Unit,
    clickDelete: (DefectListUiModel) -> Unit,
    clickConfirm: (DefectListUiModel) -> Unit,
    clickEliminated: (DefectListUiModel) -> Unit
) =
    adapterDelegateLayoutContainer<DefectListUiModel, DiffItem>(R.layout.item_defect) {

        bind {
            itemView.apply {
                // customization popup
                if (item.detour.isEmpty()) {
                    ivIconDetour.setImageDrawable(context.drawables[R.drawable.ic_defect_card_no_detour])
                    tvPopupLinkDetour.text =
                        context.strings[R.string.fragment_defect_card_no_link_detour]
                } else {
                    ivIconDetour.setImageDrawable(context.drawables[R.drawable.ic_defect_card_detour])
                    tvPopupLinkDetour.text =
                        context.strings[R.string.fragment_defect_card_link_detour]
                }

                //setting new card
                if (item.hideDetail) {
                    unfoldingContainer.visibility = View.GONE
                    ivUnfoldStatus.setImageResource(R.drawable.ic_defect_card_close)
                    ivIconMedia.visibility = visibleIvMedia(item.images)
                } else {
                    unfoldingContainer.visibility = View.VISIBLE
                    ivUnfoldStatus.setImageResource(R.drawable.ic_defect_card_open)
                    ivIconMedia.visibility = View.GONE
                }

                tvPopupLinkDetour.visibility = if (item.hideLinkDetour) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                //filling data
                tvTitleDate.text = item.id
                tvTitleDate.text = item.date
                tvTime.text = item.time
                tvDeviceData.text = item.device
                tvTypeDefectData.text = item.type
                tvDescriptionData.text = item.description
                rvDefectMedia.visibility = visibleIvMedia(item.images)
                tvDateConfirmData.text = item.dateConfirm

                //show popup link detour
                ivIconDetour.setOnClickListener {
                    tvPopupLinkDetour.visibility = if (item.hideLinkDetour) {
                        item.hideLinkDetour = false
                        View.VISIBLE
                    } else {
                        item.hideLinkDetour = true
                        View.GONE
                    }
                }

                //hide popup link detour
                tvPopupLinkDetour.setOnClickListener {
                    item.hideLinkDetour = true
                    tvPopupLinkDetour.visibility = View.GONE
                }


                // show/hide unfold card
                clContainer.setOnClickListener {
                    if (item.hideDetail) {
                        unfoldingContainer.visibility = View.VISIBLE
                        ivUnfoldStatus.setImageResource(R.drawable.ic_defect_card_open)
                        ivIconMedia.visibility = View.GONE
                        item.hideDetail = false
                    } else {
                        unfoldingContainer.visibility = View.GONE
                        ivIconMedia.visibility = visibleIvMedia(item.images)
                        ivUnfoldStatus.setImageResource(R.drawable.ic_defect_card_close)
                        item.hideDetail = true
                    }
                }

                btnDelete.setOnClickListener {
                    clickDelete.invoke(item)
                }

                btnEdit.setOnClickListener {
                    clickEdit.invoke(item)
                }

                btnConfirm.setOnClickListener {
                    clickConfirm.invoke(item)
                }

                btnEliminated.setOnClickListener {
                    clickEliminated.invoke(item)
                }

                when {
                    item.isCreated -> {
                        btnConfirmContainer.isVisible = false
                        btnEditContainer.isVisible = true
                    }
                    item.isConfirmMode -> {
                        btnConfirmContainer.isVisible = true
                        btnEditContainer.isVisible = false
                    }
                    else -> {
                        btnConfirmContainer.isVisible = false
                        btnEditContainer.isVisible = false
                    }
                }

                val mediaAdapter = MediaAdapter(
                    onMediaDeleteClick = {},
                    onMediaImageClick = {})
                if (!item.images.isNullOrEmpty()) {
                    rvDefectMedia.adapter = mediaAdapter
                    mediaAdapter.items = item.images
                }

            }
        }
    }

fun visibleIvMedia(images: List<MediaUiModel>?): Int {
    return if (images.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

data class DefectListUiModel(
    val id: String,
    val detour: String,
    val date: String,
    val time: String,
    val device: String,
    val dateConfirm: String,
    val type: String,
    val description: String,
    val isConfirmMode: Boolean,
    val images: List<MediaUiModel>?,
    var hideDetail: Boolean = true,
    var hideLinkDetour: Boolean = true,
    val isCreated: Boolean

) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is DefectListUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
package ru.madbrains.inspection.ui.delegates

import android.graphics.Bitmap
import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_media.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun mediaDefectDelegate(clickListener: (MediaDefectUiModel) -> Unit) =
    adapterDelegateLayoutContainer<MediaDefectUiModel, DiffItem>(R.layout.item_media) {

        bind {
            itemView.apply {

                if(item.isVideo) {
                    ivVideo.visibility = View.VISIBLE
                }
                else {
                    ivVideo.visibility = View.GONE
                }
                ivMediaContent.setImageBitmap(item.image)
            }
        }
    }

data class MediaDefectUiModel(
    val id: String,
    val isVideo: Boolean,
    val image: Bitmap
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is MediaDefectUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
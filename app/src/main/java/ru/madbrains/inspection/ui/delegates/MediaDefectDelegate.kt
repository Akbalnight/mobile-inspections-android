package ru.madbrains.inspection.ui.delegates

import android.graphics.Bitmap
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_media.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.extensions.drawables

fun mediaDefectDelegate(
    clickImageListener: (MediaDefectUiModel) -> Unit,
    clickDeleteListener: (MediaDefectUiModel) -> Unit
) =
    adapterDelegateLayoutContainer<MediaDefectUiModel, DiffItem>(R.layout.item_media) {

        bind {
            itemView.apply {

                if (item.isImage) {
                    ivVideo.visibility = View.GONE
                } else {
                    ivVideo.visibility = View.VISIBLE
                }
                ivMediaContent.setOnClickListener {
                    clickImageListener.invoke(item)
                }

                if (item.isEditing) {
                    ivMediaDelete.visibility = View.VISIBLE
                    ivMediaDelete.setOnClickListener {
                        clickDeleteListener.invoke(item)
                    }
                    item.image?.let { ivMediaContent.setImageBitmap(item.image) }
                } else {
                    ivMediaDelete.visibility = View.GONE
                    if (item.isImage) {
                        Glide.with(context)
                            .load(item.url)
                            .apply(RequestOptions.bitmapTransform(CenterCrop()))
                            .placeholder(context.drawables[R.drawable.ic_item_media])
                            .into(ivMediaContent)

                    } else {
                        item.image?.let { ivMediaContent.setImageBitmap(item.image) }
                    }
                }

            }
        }


    }

data class MediaDefectUiModel(
    val id: String,
    val isImage: Boolean = true,
    val isEditing: Boolean = false,
    val image: Bitmap? = null,
    val url: String = ""
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is MediaDefectUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
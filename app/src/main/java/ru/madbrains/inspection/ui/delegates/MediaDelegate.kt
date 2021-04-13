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

fun mediaDelegate(
    clickImageListener: (MediaUiModel) -> Unit,
    clickDeleteListener: (MediaUiModel) -> Unit
) =
    adapterDelegateLayoutContainer<MediaUiModel, DiffItem>(R.layout.item_media) {

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

                if (item.isEditing){
                    ivMediaDelete.visibility = View.VISIBLE
                    ivMediaDelete.setOnClickListener {
                        clickDeleteListener.invoke(item)
                    }
                } else {
                    ivMediaDelete.visibility = View.GONE
                }

                if (item.isNetworkImage) {
                    Glide.with(context)
                            .load(item.url)
                            .apply(RequestOptions.bitmapTransform(CenterCrop()))
                            .placeholder(context.drawables[R.drawable.ic_item_media])
                            .into(ivMediaContent)
                } else {
                    item.imageBitmap?.let { ivMediaContent.setImageBitmap(it) }
                }
            }
        }


    }

data class MediaUiModel(
    val id: String,
    val isImage: Boolean = true, // признак изображение или видео
    val isNetworkImage: Boolean = true, // признак онлайн медиа или локально
    val imageBitmap: Bitmap? = null, // изображение для оффлайн изобржений (либо превью видео)
    val url: String = "", // адрес для онлайн медиа
    val isEditing: Boolean = false // возможность редактирования (удаления)
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is MediaUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
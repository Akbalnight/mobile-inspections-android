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
import java.io.File

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

                    if (item.isNetwork) {
                        ivMediaDelete.visibility = View.GONE
                    } else {
                        ivMediaDelete.visibility = View.VISIBLE
                        ivMediaDelete.setOnClickListener {
                            clickDeleteListener.invoke(item)
                        }
                    }

                    if (item.isImage) {
                        if (item.isNetwork) {
                            Glide.with(context)
                                    .load(item.url)
                                    .apply(RequestOptions.bitmapTransform(CenterCrop()))
                                    .placeholder(context.drawables[R.drawable.ic_item_media])
                                    .into(ivMediaContent)
                        } else {
                            item.imageBitmap?.let { ivMediaContent.setImageBitmap(it) }
                        }
                    } else {
                        val path: String = if (item.isNetwork) {
                            item.url
                        } else {
                            item.fileVideo?.path.toString()
                        }
                        Glide.with(context)
                                .load(path)
                                .apply(RequestOptions.bitmapTransform(CenterCrop()))
                                .placeholder(context.drawables[R.drawable.ic_item_media])
                                .into(ivMediaContent)
                    }
                }
            }
        }

data class MediaDefectUiModel(
        val id: String,
        val isImage: Boolean = true, // признак изображение или видео
        val isNetwork: Boolean = true, // признак онлайн медиа или локально
        val imageBitmap: Bitmap? = null, // изображение для оффлайн изобржений (либо превью видео)
        val fileVideo: File? = null, // видео
        val url: String = "" // адрес для онлайн медиа
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is MediaDefectUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
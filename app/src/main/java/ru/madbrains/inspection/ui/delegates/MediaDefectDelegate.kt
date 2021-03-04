package ru.madbrains.inspection.ui.delegates

import android.graphics.Bitmap
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_media.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem

fun mediaDefectDelegate(clickImageListener: (MediaDefectUiModel) -> Unit,
                        clickDeleteListener: (MediaDefectUiModel) -> Unit) =
        adapterDelegateLayoutContainer<MediaDefectUiModel, DiffItem>(R.layout.item_media) {

            bind {
                itemView.apply {
                    ivMediaDelete.setOnClickListener {
                        clickDeleteListener.invoke(item)
                    }
                    if (item.isVideo) {
                        ivVideo.visibility = View.VISIBLE
                    } else {
                        ivVideo.visibility = View.GONE
                    }
                    ivMediaContent.setImageBitmap(item.image)
                    ivMediaContent.setOnClickListener {
                        clickImageListener.invoke(item)
                    }

                    if(item.isEditing) {

                    } else {
                        ivMediaDelete.visibility = View.GONE

                    }


                    Glide.with(context)
                        .load(item.url)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(ivMediaContent)
/*
                    if(item.url.isNullOrEmpty()){

                        ivMediaContent.setImageBitmap(item.image)
                    } else {
                        if (item.isVideo) {
                            //todo video download and preview

                        } else {
                            Glide.with(context)
                                .load(item.url)
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(ivMediaContent)
                        }
                    }*/

                }
            }


        }

data class MediaDefectUiModel(
        val id: String,
        val isVideo: Boolean = false,
        val isEditing: Boolean = false,
        val videoPreview: Bitmap,
        val image: Bitmap?,
        val url: String?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is MediaDefectUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
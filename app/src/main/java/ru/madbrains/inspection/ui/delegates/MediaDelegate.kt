package ru.madbrains.inspection.ui.delegates

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_media.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.extensions.drawables
import java.io.File

fun mediaDelegate(
    clickImageListener: (MediaUiModel) -> Unit,
    clickDeleteListener: (MediaUiModel) -> Unit
) =
    adapterDelegateLayoutContainer<MediaUiModel, DiffItem>(R.layout.item_media) {

        bind {
            itemView.apply {
                ivVideo.isVisible = !item.isImage
                ivMediaContent.setOnClickListener {
                    clickImageListener.invoke(item)
                }

                ivMediaDelete.isVisible = item.isLocal
                if (item.isLocal) {
                    ivMediaDelete.setOnClickListener {
                        clickDeleteListener.invoke(item)
                    }
                }
                Glide.with(context)
                    .load(item.file)
                    .apply(RequestOptions.bitmapTransform(CenterCrop()))
                    .placeholder(context.drawables[R.drawable.ic_item_media])
                    .into(ivMediaContent)
            }
        }
    }

data class MediaUiModel(
    val id: String,
    val isLocal: Boolean = false,
    val file: File? = null
) : DiffItem {

    val isImage get() = file?.extension in arrayListOf("png", "jpg", "jpeg")

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is MediaUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
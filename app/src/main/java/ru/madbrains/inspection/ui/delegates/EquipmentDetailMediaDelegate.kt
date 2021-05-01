package ru.madbrains.inspection.ui.delegates

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_detour.view.clContainer
import kotlinx.android.synthetic.main.item_equipment_image.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.adapters.EquipmentImageAdapter
import java.io.File
import kotlin.math.roundToInt

fun equipmentDetailMediaDelegate(
    clickListener: (EquipmentDetailMediaUiModel) -> Unit,
    adapter: EquipmentImageAdapter
) =
    adapterDelegateLayoutContainer<EquipmentDetailMediaUiModel, DiffItem>(R.layout.item_equipment_image) {

        onViewAttachedToWindow { }

        bind {
            itemView.apply {
                clContainer.setOnClickListener {
                    clickListener.invoke(item)
                }
                layoutParams = RecyclerView.LayoutParams(
                    adapter.maxImageWidth,
                    RecyclerView.LayoutParams.MATCH_PARENT
                )

                // Load image
                Glide.with(ivEquipment)
                    .load(item.file)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_item_media_padded)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {
                                // Change aspect ratio
                                val imageAspectRatio =
                                    it.intrinsicWidth.toFloat() / it.intrinsicHeight.toFloat()
                                val targetImageWidth: Int =
                                    if (imageAspectRatio < adapter.maxImageAspectRatio) {
                                        // Tall image: height = max
                                        (adapter.maxImageHeight * imageAspectRatio).roundToInt()
                                    } else {
                                        // Wide image: width = max
                                        adapter.maxImageWidth
                                    }
                                layoutParams.width = targetImageWidth
                            }
                            return false
                        }
                    })
                    .into(ivEquipment)
            }
        }
    }

data class EquipmentDetailMediaUiModel(
    val file: File?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
        newItem is EquipmentDetailMediaUiModel && file?.name == file?.name

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}
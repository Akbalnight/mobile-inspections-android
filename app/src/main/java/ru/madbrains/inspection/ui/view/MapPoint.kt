package ru.madbrains.inspection.ui.view

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.TableRow
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.item_map_point.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.ui.main.routes.points.map.MapPointStatus

class MapPoint constructor(
    context: Context
) : TableRow(context) {

    constructor(context: Context, label: String, status: MapPointStatus) : this(context) {
        setLabelText(label)
        setStyle(status)
    }

    init {
        inflate(getContext(), R.layout.item_map_point, this)
    }

    private fun setLabelText(value: String?) {
        tvLabel.text = value
    }

    private fun setStyle(status: MapPointStatus) {
        container.layoutParams.height = 100
        container.layoutParams.width = 100

        val color = when (status) {
            MapPointStatus.None -> R.drawable.ic_point_blue
            MapPointStatus.Current -> R.drawable.ic_point_blue
            MapPointStatus.CompletedWithDefects -> R.drawable.ic_point_red
            MapPointStatus.Completed -> R.drawable.ic_point_green
        }

        ivBackground.setImageResource(color)
        tvLabel.setTextColor(
            ResourcesCompat.getColor(
                context.resources,
                R.color.textWhite,
                null
            )
        )

        if (status == MapPointStatus.Current) {
            ivBackground.animation?.cancel()
            val animation: Animation = AlphaAnimation(1f, 0f)
            animation.duration = 1000
            animation.interpolator = LinearInterpolator()
            animation.repeatCount = Animation.INFINITE
            animation.repeatMode = Animation.REVERSE
            ivBackground.startAnimation(animation)
        } else {
            ivBackground.animation?.cancel()
        }
    }
}
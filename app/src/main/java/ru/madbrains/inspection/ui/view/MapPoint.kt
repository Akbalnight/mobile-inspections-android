package ru.madbrains.inspection.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TableRow
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.item_map_point.view.*
import ru.madbrains.inspection.R

class MapPoint @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TableRow(context, attrs) {

    constructor(context: Context, label: String, isCompleted: Boolean = false) : this(context) {
        setLabelText(label)
        setStyle(isCompleted)
    }

    init {
        inflate(getContext(), R.layout.item_map_point, this)
        setupAttrs(context, attrs)
    }

    private fun setupAttrs(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.equipmentSpecsTableRow,
                0, 0
            )
            setLabelText(typedArray.getString(R.styleable.equipmentSpecsTableRow_labelText))
            setStyle(typedArray.getBoolean(R.styleable.equipmentSpecsTableRow_greyStyle, false))
            typedArray.recycle()
        }
    }

    private fun setLabelText(value: String?) {
        tvLabel.text = value
    }

    private fun setStyle(value: Boolean) {
        container.layoutParams.height = 100
        container.layoutParams.width = 100

        if (value) {
            ivBackground.setImageResource(R.drawable.ic_point_completed)
            tvLabel.setTextColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.textWhite,
                    null
                )
            )
        } else {
            ivBackground.setImageResource(R.drawable.ic_point)
            tvLabel.setTextColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.textMain,
                    null
                )
            )
        }
    }
}
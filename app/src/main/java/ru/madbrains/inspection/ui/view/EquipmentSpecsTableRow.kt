package ru.madbrains.inspection.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TableRow
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.view_equipment_specs_table_row.view.*
import ru.madbrains.inspection.R

class EquipmentSpecsTableRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TableRow(context, attrs) {

    constructor(context: Context, label:String, value:String?, greyStyle: Boolean = false): this(context) {
        setLabelText(label)
        setValueText(value)
        setGreyStyle(greyStyle)
    }

    init {
        inflate(getContext(), R.layout.view_equipment_specs_table_row, this)
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
            setValueText(typedArray.getString(R.styleable.equipmentSpecsTableRow_valueText))
            setGreyStyle(typedArray.getBoolean(R.styleable.equipmentSpecsTableRow_greyStyle, false))
            typedArray.recycle()
        }
    }

    fun setLabelText(value:String?){
        tvLabel.text = value
    }

    fun setValueText(value:String?){
        tvValue.text = value
    }

    private fun setGreyStyle(value:Boolean){
        if(value){
            tvValue.setTextColor(ResourcesCompat.getColor(context.resources, R.color.textMain87, null))
        }
    }
}
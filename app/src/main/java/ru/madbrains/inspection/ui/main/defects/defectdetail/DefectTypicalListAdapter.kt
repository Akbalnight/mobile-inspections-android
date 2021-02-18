package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import kotlinx.android.synthetic.main.item_defect_typical.view.*
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RouteUiModel

class DefectTypicalListAdapter(context: Context,
                               @LayoutRes private val layoutResource: Int,
                               @IdRes private val textViewResourceId: Int = 0,
                               private var values: List<DefectTypicalUiModel> = emptyList()) :
        ArrayAdapter<DefectTypicalUiModel>(context, layoutResource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d("fsfsd", "fdsfsdf")
        val view = createViewFromResource(convertView, parent, layoutResource)

        return bindData(getItem(position), view)
    }

    override fun getItem(position: Int): DefectTypicalUiModel = values[position]

    fun addItems(items: List<DefectTypicalUiModel>) {
        values = items;
        super.clear()
        super.addAll(items)
    }

    private fun createViewFromResource(convertView: View?, parent: ViewGroup, layoutResource: Int): View {
        val context = parent.context
        return convertView
                ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
    }

    private fun bindData(value: DefectTypicalUiModel, view: View): View {
        Log.d("fsfsd", "fdsfsdf222")

        view.tvName.text = value.name

        // view.tvName.text = value.name
        Log.d("fsfsd", value.name)
        return view
    }

}


data class DefectTypicalUiModel(
        val id: String?,
        val name: String?,
        val code: Int?
) : DiffItem {

    override fun areItemsTheSame(newItem: DiffItem): Boolean =
            newItem is RouteUiModel && id == newItem.id

    override fun areContentsTheSame(newItem: DiffItem): Boolean = this == newItem
}

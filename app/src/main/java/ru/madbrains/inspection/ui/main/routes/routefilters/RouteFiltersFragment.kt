package ru.madbrains.inspection.ui.main.routes.routefilters

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_route_filters.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.R
import timber.log.Timber

class RouteFiltersFragment : DialogFragment() {

    private val routeFiltersViewModel: RouteFiltersViewModel by sharedViewModel()

    private val rbs = mutableListOf<RadioButton>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_route_filters, container, false)
    }
    private fun createRadio(status: DetourStatus?, txt: CharSequence?, checked: Boolean):RadioButton {
        return RadioButton(context).apply {
            text = txt
            setTextColor(ResourcesCompat.getColor(context.resources, R.color.textMain, null))
            isChecked = checked
            tag = status?.type
            buttonTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources, R.color.elementsBlue, null))
            val padding = resources.getDimensionPixelSize(R.dimen.radio_spacing)
            setPadding(0, padding,0, padding)
            setOnClickListener {
                rbs.map { it.isChecked = false }
                isChecked = true
                routeFiltersViewModel.setFilter(status?.type)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        routeFiltersViewModel.availableStatuses.observe(viewLifecycleOwner, Observer { statuses ->
            val allRadio = createRadio(null, resources.getText(R.string.all_detours), true)
            val radios = statuses.map { status->
                createRadio(status, status.name, false)
            }
            rbs.apply {
               add(allRadio)
               addAll(radios)
            }
            for(view in rbs){
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                radiosWrap.addView(view, lp)
            }
        })

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        btnApply.setOnClickListener {
            routeFiltersViewModel.applyFilter()
            this.dismiss()
        }

        routeFiltersViewModel.selectedFilter.observe(viewLifecycleOwner, Observer { status ->
            rbs.find {
                status?.type == it.tag
            }?.apply {
                rbs.map { it.isChecked = false }
                isChecked = true
            }
        })
    }
}
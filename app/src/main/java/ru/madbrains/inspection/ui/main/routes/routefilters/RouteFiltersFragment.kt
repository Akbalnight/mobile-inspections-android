package ru.madbrains.inspection.ui.main.routes.routefilters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_route_filters.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.R

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rbs.apply {
            add(rbAll)
            add(rbNew)
            add(rbCompleted)
            add(rbNotCompleted)
            add(rbInProgress)
            add(rbCompletedAhead)
        }

        rbAll.isChecked = true

        rbAll.setOnClickListener {
            rbs.map { it.isChecked = false }
            rbAll.isChecked = true
            routeFiltersViewModel.setFilter(null)
        }
        rbNew.setOnClickListener {
            rbs.map { it.isChecked = false }
            rbNew.isChecked = true
            routeFiltersViewModel.setFilter(DetourStatus.NEW)
        }
        rbCompleted.setOnClickListener {
            rbs.map { it.isChecked = false }
            rbCompleted.isChecked = true
            routeFiltersViewModel.setFilter(DetourStatus.COMPLETED)
        }
        rbNotCompleted.setOnClickListener {
            rbs.map { it.isChecked = false }
            rbNotCompleted.isChecked = true
            routeFiltersViewModel.setFilter(DetourStatus.NOT_COMPLETED)
        }
        rbInProgress.setOnClickListener {
            rbs.map { it.isChecked = false }
            rbInProgress.isChecked = true
            routeFiltersViewModel.setFilter(DetourStatus.IN_PROGRESS)
        }
        rbCompletedAhead.setOnClickListener {
            rbs.map { it.isChecked = false }
            rbCompletedAhead.isChecked = true
            routeFiltersViewModel.setFilter(DetourStatus.COMPLETED_AHEAD)
        }

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        btnApply.setOnClickListener {
            routeFiltersViewModel.applyFilter()
            this.dismiss()
        }

        routeFiltersViewModel.selectedFilter.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                DetourStatus.NEW -> {
                    rbs.map { it.isChecked = false }
                    rbNew.isChecked = true
                }
                DetourStatus.COMPLETED -> {
                    rbs.map { it.isChecked = false }
                    rbCompleted.isChecked = true
                }
                DetourStatus.NOT_COMPLETED -> {
                    rbs.map { it.isChecked = false }
                    rbNotCompleted.isChecked = true
                }
                DetourStatus.IN_PROGRESS -> {
                    rbs.map { it.isChecked = false }
                    rbInProgress.isChecked = true
                }
                DetourStatus.COMPLETED_AHEAD -> {
                    rbs.map { it.isChecked = false }
                    rbCompletedAhead.isChecked = true
                }
                else -> {
                    rbs.map { it.isChecked = false }
                    rbAll.isChecked = true
                }
            }
        })
    }
}
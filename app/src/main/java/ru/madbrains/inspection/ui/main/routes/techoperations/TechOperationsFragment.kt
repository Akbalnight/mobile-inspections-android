package ru.madbrains.inspection.ui.main.routes.techoperations

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_points.*
import kotlinx.android.synthetic.main.fragment_tech_operations.*
import kotlinx.android.synthetic.main.fragment_tech_operations.progressView
import kotlinx.android.synthetic.main.fragment_tech_operations.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RoutePointModel
import ru.madbrains.inspection.R

import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.TechOperationAdapter
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment


class TechOperationsFragment : BaseFragment(R.layout.fragment_tech_operations) {

    companion object {
        const val KEY_POINT = "point"
    }

    private val techOperationsCardViewModel: TechOperationsViewModel by viewModel()

    private val techOperationsAdapter by lazy {
        TechOperationAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        techOperationsCardViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })
        requireNotNull(arguments).run {
            (getSerializable(TechOperationsFragment.KEY_POINT) as? RoutePointModel)?.let {
                setupToolbar(it.position)
                techOperationsCardViewModel.setPoint(it)
            }
        }


        rvTechOperations.adapter = techOperationsAdapter

       // techOperationsCardViewModel.getCard("7d8160e7-836d-45fc-8295-e9a9fa7c3a7f") //todo

        techOperationsCardViewModel.titleTechOperations.observe(viewLifecycleOwner, Observer {
            tvTitleTechOperations.text = it
        })
        techOperationsCardViewModel.techOperations.observe(viewLifecycleOwner, Observer {
            techOperationsAdapter.items = it
        })

    }

    private fun setupToolbar(positionPoint: Int?) {

        toolbarLayout.apply {
            var toolBarTitle = strings[R.string.fragment_tech_operations_app_bar]
            positionPoint?.let {
                toolBarTitle += it.toString()
            }
            tvTitle.text = toolBarTitle
            toolbarLayout.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}
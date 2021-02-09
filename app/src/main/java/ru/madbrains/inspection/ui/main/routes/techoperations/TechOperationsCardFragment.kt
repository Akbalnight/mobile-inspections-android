package ru.madbrains.inspection.ui.main.routes.techoperations

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_points.*
import kotlinx.android.synthetic.main.fragment_tech_operations.*
import kotlinx.android.synthetic.main.fragment_tech_operations.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.tvTitle
import kotlinx.android.synthetic.main.toolbar_with_menu.*
import kotlinx.android.synthetic.main.toolbar_with_menu.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R

import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.TechOperationAdapter
import ru.madbrains.inspection.ui.main.routes.RoutesViewModel


class TechOperationsCardFragment : BaseFragment(R.layout.fragment_tech_operations) {

    private val techOperationsCardViewModel: TechOperationsCardViewModel by viewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()

    private val techOperationsAdapter by lazy {
        TechOperationAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()

        rvTechOperations.adapter = techOperationsAdapter

        techOperationsCardViewModel.getCard("7d8160e7-836d-45fc-8295-e9a9fa7c3a7f") //todo

        techOperationsCardViewModel.titleTechOperations.observe(viewLifecycleOwner, Observer {
                tvTitleTechOperations.text = it
        })
        techOperationsCardViewModel.techOperations.observe(viewLifecycleOwner, Observer {
            techOperationsAdapter.items = it
        })

    }

    private fun setupToolbar() {

        toolbarLayout.apply {
            val numberControlPoint = 5;
            val appBarTitle = strings[R.string.fragment_tech_operations_app_bar]
            tvTitle.text = appBarTitle + numberControlPoint.toString()
            toolbarLayout.btnBack .setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}
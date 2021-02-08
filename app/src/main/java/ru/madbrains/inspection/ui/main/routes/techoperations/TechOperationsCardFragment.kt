package ru.madbrains.inspection.ui.main.routes.techoperations

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_route_list.*
import kotlinx.android.synthetic.main.fragment_tech_operations_card.*
import kotlinx.android.synthetic.main.toolbar_with_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R

import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.routes.RoutesAdapter
import ru.madbrains.inspection.ui.main.routes.RoutesViewModel


class TechOperationsCardFragment : BaseFragment(R.layout.fragment_tech_operations_card) {

    private val techOperationsCardViewModel: TechOperationsCardViewModel by viewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()

    private lateinit var techOperationsAdapter: TechOperationsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()

        techOperationsCardViewModel.getCard()

        techOperationsCardViewModel.techOperations.observe(viewLifecycleOwner, Observer {
            techOperationsAdapter.items = it
        })
      /*  rvRoutes.adapter = routesAdapter

        btnGetData.setOnClickListener {
            routesViewModel.getRoutes()
        }

        routesViewModel.routes.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it
            ivGetData.isVisible = false
            btnGetData.isVisible = false
        })*/
    }

    private fun setupToolbar() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_sync_title]
        }
    }
}
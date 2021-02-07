package ru.madbrains.inspection.ui.main.routes.techoperations

import android.os.Bundle
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_route_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.ui.adapters.RouteAdapter
import ru.madbrains.inspection.ui.main.routes.RoutesViewModel
import ru.madbrains.inspection.ui.main.routes.routelist.RouteListViewModel


class TechOperationsCardFragment : BaseFragment(R.layout.fragment_tech_operations_card) {

    private val techOperationsCardViewModel: TechOperationsCardViewModel by viewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
}
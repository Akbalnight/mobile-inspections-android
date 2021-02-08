package ru.madbrains.inspection.ui.main.routes.routelist

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.ui.adapters.RouteAdapter
import ru.madbrains.inspection.ui.main.routes.RoutesViewModel

class RouteListFragment : BaseFragment(R.layout.fragment_route_list) {

    private val routeListViewModel: RouteListViewModel by viewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        RouteAdapter(
            onRouteClick = {
                routeListViewModel.routeClick()
                findNavController().navigate(R.id.action_routesFragment_to_techOperationsCardFragment) //todo delete
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvRoutes.adapter = routesAdapter

        btnGetData.setOnClickListener {
            routesViewModel.getRoutes()
        }

        routesViewModel.routes.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it
            ivGetData.isVisible = false
            btnGetData.isVisible = false
        })
    }
}
package ru.madbrains.inspection.ui.main.routes.routelist

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.RouteAdapter
import ru.madbrains.inspection.ui.main.routes.RoutesViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment

class RouteListFragment : BaseFragment(R.layout.fragment_route_list) {

    private val routeListViewModel: RouteListViewModel by viewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        RouteAdapter(
            onRouteClick = {
                val route = routesViewModel.routeModels.find { routeModel ->
                    routeModel.id == it.id
                }
                routeListViewModel.routeClick(route)
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
        routeListViewModel.navigateToRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun openRoutePointsFragment(route: RouteModel) {
        val args = bundleOf(
            RoutePointsFragment.KEY_ROUTE to route
        )
        findNavController().navigate(R.id.action_routesFragment_to_routePointsFragment, args)
    }
}
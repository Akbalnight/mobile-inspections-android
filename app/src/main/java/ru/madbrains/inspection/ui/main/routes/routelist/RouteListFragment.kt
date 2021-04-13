package ru.madbrains.inspection.ui.main.routes.routelist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_route_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.DetourAdapter
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsActivity

class RouteListFragment : BaseFragment(R.layout.fragment_route_list) {

    private val routeListViewModel: RouteListViewModel by viewModel()
    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        DetourAdapter(
            onDetourClick = {
                val detour = detoursViewModel.detourModels.find { detourModel ->
                    detourModel.id == it.id
                }
                routeListViewModel.routeClick(detour)
            }
        )
    }

    @SuppressLint("LogNotTimber")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvRoutes.adapter = routesAdapter

        btnGetData.setOnClickListener {
            detoursViewModel.getDetours()
        }

        detoursViewModel.detours.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it
            ivGetData.isVisible = false
            btnGetData.isVisible = false
        })
        routeListViewModel.navigateToRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun openRoutePointsFragment(route: DetourModel) {
        RoutePointsActivity.start(this.context, route)
    }
}
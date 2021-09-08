package ru.madbrains.inspection.ui.main.routes.routelist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.clickWithDebounce
import ru.madbrains.inspection.ui.adapters.DetourAdapter
import ru.madbrains.inspection.ui.main.SyncViewModel
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment

class RouteListFragment : BaseFragment(R.layout.fragment_route_list) {

    private val detoursViewModel: DetoursViewModel by sharedViewModel()
    private val syncViewModel: SyncViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        DetourAdapter(
            onDetourClick = {
                detoursViewModel.routeClick(it.id)
            }
        )
    }

    @SuppressLint("LogNotTimber")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvRoutes.adapter = routesAdapter

        btnGetData.clickWithDebounce {
            syncViewModel.startSync()
        }

        detoursViewModel.detours.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it
            ivGetData.isVisible = it.isEmpty()
            btnGetData.isVisible = it.isEmpty()

        })
        detoursViewModel.navigateToRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun openRoutePointsFragment(route: DetourModel) {
        val args = bundleOf(
            RoutePointsFragment.KEY_DETOUR to route
        )
        findNavController().navigate(R.id.action_DetoursFragment_to_routePointsFragment, args)
    }
}
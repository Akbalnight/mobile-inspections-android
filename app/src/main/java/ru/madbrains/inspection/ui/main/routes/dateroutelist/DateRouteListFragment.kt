package ru.madbrains.inspection.ui.main.routes.dateroutelist

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_list_date.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.RouteAdapter
import ru.madbrains.inspection.ui.delegates.RouteUiModel
import ru.madbrains.inspection.ui.main.routes.RoutesViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment

class DateRouteListFragment : BaseFragment(R.layout.fragment_route_list_date) {

    companion object {
        const val KEY_TOOLBAR_TITLE = "toolbar_title"
    }

    private val dateRouteListViewModel: DateRouteListViewModel by viewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()

    private var date: String? = null

    private val routesAdapter by lazy {
        RouteAdapter(
            onRouteClick = {
                val route = routesViewModel.routeModels.find { routeModel ->
                    routeModel.id == it.id
                }
                dateRouteListViewModel.routeClick(route)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireNotNull(arguments).run {
            date = getString(KEY_TOOLBAR_TITLE)
            setupToolbar()
        }

        rvRoutes.adapter = routesAdapter

        routesViewModel.routes.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it.filterIsInstance<RouteUiModel>().filter { route ->
                route.date.split("T").firstOrNull() == date
            }
        })
        dateRouteListViewModel.navigateToRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun setupToolbar() {
        toolbarLayout.tvTitle.text = date.orEmpty()
        toolbarLayout.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openRoutePointsFragment(route: DetourModel) {
        val args = bundleOf(
            RoutePointsFragment.KEY_ROUTE to route
        )
        findNavController().navigate(R.id.action_dateRouteListFragment_to_routePointsFragment, args)
    }
}
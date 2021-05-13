package ru.madbrains.inspection.ui.main.routes.dateroutelist

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_list_date.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.DetourAdapter
import ru.madbrains.inspection.ui.delegates.DetourUiModel
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment

class DateRouteListFragment : BaseFragment(R.layout.fragment_route_list_date) {

    companion object {
        const val KEY_TOOLBAR_TITLE = "toolbar_title"
    }

    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private var date: String? = null

    private val routesAdapter by lazy {
        DetourAdapter(
            onDetourClick = {
                detoursViewModel.dateRouteClick(it.id)
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

        detoursViewModel.detours.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it.filterIsInstance<DetourUiModel>().filter { route ->
                route.date?.split("T")?.firstOrNull() == date
            }
        })

        detoursViewModel.navigateToDateRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun setupToolbar() {
        toolbarLayout.tvTitle.text = date.orEmpty()
        toolbarLayout.btnLeading.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openRoutePointsFragment(detour: DetourModel) {
        val args = bundleOf(
            RoutePointsFragment.KEY_DETOUR to detour
        )
        findNavController().navigate(R.id.action_dateRouteListFragment_to_routePointsFragment, args)
    }
}
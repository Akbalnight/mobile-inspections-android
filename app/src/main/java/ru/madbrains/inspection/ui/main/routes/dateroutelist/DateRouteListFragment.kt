package ru.madbrains.inspection.ui.main.routes.dateroutelist

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_list_date.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.data.extensions.toYYYYMMDD
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.toLocalDate
import ru.madbrains.inspection.ui.adapters.DetourAdapter
import ru.madbrains.inspection.ui.delegates.DetourUiModel
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment
import java.util.*

class DateRouteListFragment : BaseFragment(R.layout.fragment_route_list_date) {

    companion object {
        const val KEY_TOOLBAR_ARG = "KEY_TOOLBAR_ARG"
    }

    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        DetourAdapter(
            onDetourClick = {
                detoursViewModel.dateRouteClick(it.id)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val date = (requireNotNull(arguments).getSerializable(KEY_TOOLBAR_ARG) as Date)
        setupToolbar(date)

        rvRoutes.adapter = routesAdapter

        detoursViewModel.detours.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it.filterIsInstance<DetourUiModel>().filter { route ->
                route.date?.toLocalDate() == date.toLocalDate()
            }
        })


        detoursViewModel.navigateToDateRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun setupToolbar(date: Date) {
        toolbarLayout.tvTitle.text = date.toYYYYMMDD()
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
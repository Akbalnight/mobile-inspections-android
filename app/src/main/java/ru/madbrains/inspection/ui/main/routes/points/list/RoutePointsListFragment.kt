package ru.madbrains.inspection.ui.main.routes.points.list

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_points_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.TechMapModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.RoutePointAdapter
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsFragment

class RoutePointsListFragment : BaseFragment(R.layout.fragment_route_points_list) {

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val routePointsListViewModel: RoutePointsListViewModel by viewModel()

    private val routePointsAdapter by lazy {
        RoutePointAdapter(
            onRoutePointClick = {
                val techMaps = routePointsViewModel.routeDataModels.map { it.techMap }
                val techMap = techMaps.find { techMap ->
                    techMap.id == it.id
                }
                techMap?.pointNumber = it.position
                routePointsListViewModel.routePointClick(techMap)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        routePointsViewModel.detourModel?.run {
            tvPlanStartValue.text = dateStartPlan?.replace("T", " ")
            tvPlanEndValue.text = dateFinishPlan?.replace("T", " ")
            tvFactStartValue.text = dateStartFact?.replace("T", " ")
            tvFactEndValue.text = dateFinishFact?.replace("T", " ")
        }

        rvRoutePoints.adapter = routePointsAdapter

        routePointsViewModel.routePoints.observe(viewLifecycleOwner, Observer {
            routePointsAdapter.items = it
        })
        routePointsViewModel.navigateToNextRoute.observe(viewLifecycleOwner, EventObserver {
            val techMap = it.techMap
            techMap.pointNumber = it.position
            openTechOperationsFragment(techMap)
        })

        routePointsListViewModel.navigateToTechOperations.observe(
            viewLifecycleOwner,
            EventObserver {
                openTechOperationsFragment(it)
            })
    }

    private fun openTechOperationsFragment(techMap: TechMapModel) {
        val args = bundleOf(
            TechOperationsFragment.KEY_TECH_MAP to techMap
        )
        findNavController().navigate(
            R.id.action_routePointsFragment_to_techOperationsFragment,
            args
        )
    }
}
package ru.madbrains.inspection.ui.main.routes.points.list

import android.os.Bundle
import android.text.format.DateUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_points_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.RoutePointAdapter
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsFragment

class RoutePointsListFragment : BaseFragment(R.layout.fragment_route_points_list) {

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val routePointsListViewModel: RoutePointsListViewModel by viewModel()

    private val routePointsAdapter by lazy {
        RoutePointAdapter(
            onRoutePointClick = {
                val routeData = routePointsViewModel.routeDataModels.find { data ->
                    data.techMap?.id == it.id
                }
                routeData?.techMap?.pointNumber = it.position
                routePointsListViewModel.routePointClick(routeData)
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
            tvFactOrderValue.text = if(saveOrderControlPoints == true) strings[R.string.yes] else strings[R.string.no]
        }
        routePointsViewModel.durationTimer.observe(viewLifecycleOwner, Observer {time ->
            durationBlock.isVisible = time != null
            time?.let { tvDurationValue.text = DateUtils.formatElapsedTime(it) }
        })

        rvRoutePoints.adapter = routePointsAdapter

        routePointsViewModel.routePoints.observe(viewLifecycleOwner, Observer {
            routePointsAdapter.items = it
        })

        routePointsViewModel.navigateToNextRoute.observe(
            viewLifecycleOwner,
            EventObserver { routeData ->
                val techMap = routeData.techMap
                techMap?.let {
                    techMap.pointNumber = routeData.position
                    openTechOperationsFragment(routeData)
                }
            })

        routePointsListViewModel.navigateToTechOperations.observe(
            viewLifecycleOwner,
            EventObserver {
                openTechOperationsFragment(it)
            })
    }

    private fun openTechOperationsFragment(routeData: RouteDataModel) {
        val args = bundleOf(
            TechOperationsFragment.KEY_ROUTE_DATA to routeData
        )
        findNavController().navigate(
            R.id.action_routePointsFragment_to_techOperationsFragment,
            args
        )
    }
}
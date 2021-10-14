package ru.madbrains.inspection.ui.main.routes.points.list

import android.os.Bundle
import android.text.format.DateUtils
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_route_points_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.data.extensions.toHHmmddMMyy
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.RoutePointAdapter
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel

class RoutePointsListFragment : BaseFragment(R.layout.fragment_route_points_list) {

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()

    private val routePointsAdapter by lazy {
        RoutePointAdapter(
            onRoutePointClick = {
                routePointsViewModel.routePointClick(it.routeDataId)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        routePointsViewModel.detourModel?.run {
            tvPlanStartValue.text = dateStartPlan?.toHHmmddMMyy()
            tvPlanEndValue.text = dateFinishPlan?.toHHmmddMMyy()
            tvFactStartValue.text = routePointsViewModel.dateStartFact?.toHHmmddMMyy()
            tvFactEndValue.text = routePointsViewModel.dateFinishFact?.toHHmmddMMyy()
            tvFactOrderValue.text =
                if (saveOrderControlPoints == true) strings[R.string.yes] else strings[R.string.no]
        }
        routePointsViewModel.durationTimer.observe(viewLifecycleOwner, Observer { time ->
            durationBlock.isVisible = time != null
            time?.let { tvDurationValue.text = DateUtils.formatElapsedTime(it) }
        })

        rvRoutePoints.adapter = routePointsAdapter

        routePointsViewModel.routePoints.observe(viewLifecycleOwner, Observer {
            routePointsAdapter.items = it
        })
    }
}
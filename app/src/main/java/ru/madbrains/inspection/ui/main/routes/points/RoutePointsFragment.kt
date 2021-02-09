package ru.madbrains.inspection.ui.main.routes.points

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_route_points.*
import kotlinx.android.synthetic.main.fragment_route_points_list.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.routes.points.list.RoutePointsListFragment
import ru.madbrains.inspection.ui.main.routes.points.map.RoutePointsMapFragment

class RoutePointsFragment : BaseFragment(R.layout.fragment_route_points) {

    companion object {
        const val KEY_ROUTE = "route"
    }

    private lateinit var routePointsAdapter: RoutePointsAdapter

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireNotNull(arguments).run {
            (getSerializable(KEY_ROUTE) as? RouteModel)?.let {
                setupToolbar(it.name)
                routePointsViewModel.setRoute(it)
            }
        }

        setupViewPager()

        routePointsViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })
    }

    private fun setupToolbar(title: String?) {
        toolbarLayout.tvTitle.text = title.orEmpty()
        toolbarLayout.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupViewPager() {
        val fragments = listOf(
            RoutePointsListFragment(),
            RoutePointsMapFragment()
        )

        routePointsViewPager.isUserInputEnabled = false

        routePointsAdapter = RoutePointsAdapter(this).apply {
            setItems(fragments)
            routePointsViewPager.adapter = this
        }

        TabLayoutMediator(routePointsTabLayout, routePointsViewPager) { tab, position ->
            tab.text = when (fragments[position]) {
                is RoutePointsListFragment -> strings[R.string.fragment_route_points_list]
                is RoutePointsMapFragment -> strings[R.string.fragment_route_points_map]
                else -> ""
            }
            routePointsViewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}
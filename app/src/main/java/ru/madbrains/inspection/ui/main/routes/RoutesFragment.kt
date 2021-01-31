package ru.madbrains.inspection.ui.main.routes

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_routes.*
import kotlinx.android.synthetic.main.fragment_routes.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_menu_and_filter.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.routes.routecalendar.RouteCalendarFragment
import ru.madbrains.inspection.ui.main.routes.routefilters.RouteFiltersViewModel
import ru.madbrains.inspection.ui.main.routes.routelist.RouteListFragment

class RoutesFragment : BaseFragment(R.layout.fragment_routes) {

    private lateinit var routesAdapter: RoutesAdapter

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val routeFiltersViewModel: RouteFiltersViewModel by sharedViewModel()
    private val routesViewModel: RoutesViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
        setupViewPager()

        routeFiltersViewModel.selectedFilter.observe(viewLifecycleOwner, Observer {
            val filterImage = if (it == null) {
                R.drawable.ic_filter
            } else {
                R.drawable.ic_filter_filled
            }
            toolbarLayout.btnFilter.setImageResource(filterImage)
        })

        routesViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })
        routeFiltersViewModel.selectedFilter.observe(viewLifecycleOwner, Observer {
            routesViewModel.updateData(it)
        })
    }

    private fun setupToolbar() {
        toolbarLayout.tvTitle.text = strings[R.string.fragment_routes_title]
        toolbarLayout.btnMenu.setOnClickListener {
            mainViewModel.menuClick()
        }
        toolbarLayout.btnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_routesFragment_to_routeFiltersFragment)
        }
    }

    private fun setupViewPager() {
        val fragments = listOf(
            RouteListFragment(),
            RouteCalendarFragment()
        )

        routesViewPager.isUserInputEnabled = false

        routesAdapter = RoutesAdapter(this).apply {
            setItems(fragments)
            routesViewPager.adapter = this
        }

        TabLayoutMediator(routesTabLayout, routesViewPager) { tab, position ->
            tab.text = when (fragments[position]) {
                is RouteListFragment -> strings[R.string.fragment_routes_route_list]
                is RouteCalendarFragment -> strings[R.string.fragment_routes_route_calendar]
                else -> ""
            }
            routesViewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}
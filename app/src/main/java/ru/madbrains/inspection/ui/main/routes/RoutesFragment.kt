package ru.madbrains.inspection.ui.main.routes

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_routes.*
import kotlinx.android.synthetic.main.toolbar_with_menu_and_filter.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.routes.routecalendar.RouteCalendarFragment
import ru.madbrains.inspection.ui.main.routes.routelist.RouteListFragment

class RoutesFragment : BaseFragment(R.layout.fragment_routes) {

    private lateinit var routesAdapter: RoutesAdapter

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val routesViewModel: RoutesViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        toolbarLayout.tvTitle.text = strings[R.string.fragment_routes_title]
        toolbarLayout.btnMenu.setOnClickListener {
            mainViewModel.menuClick()
        }
        toolbarLayout.btnFilter.setOnClickListener {
            // TODO add click action
        }
    }

    private fun setupViewPager() {
        val fragments = listOf(
            RouteListFragment(),
            RouteCalendarFragment()
        )

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
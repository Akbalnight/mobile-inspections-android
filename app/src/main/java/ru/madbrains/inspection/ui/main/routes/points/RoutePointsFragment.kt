package ru.madbrains.inspection.ui.main.routes.points

import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_route_points.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.DetourStatus
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.list.RoutePointsListFragment
import ru.madbrains.inspection.ui.main.routes.points.map.RoutePointsMapFragment
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsViewModel

class RoutePointsFragment : BaseFragment(R.layout.fragment_route_points) {

    companion object {
        const val KEY_DETOUR = "detour"
    }

    private lateinit var routePointsAdapter: RoutePointsAdapter

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val techOperationsViewModel: TechOperationsViewModel by sharedViewModel()
    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private val stateFabs = mutableListOf<ExtendedFloatingActionButton>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val detour = requireActivity().intent.getSerializableExtra(KEY_DETOUR) as? DetourModel
        detour?.let {
            setupToolbar(it.name)
            routePointsViewModel.setDetour(it)
        }

        stateFabs.add(fabStart)
        stateFabs.add(fabContinue)
        stateFabs.add(fabFinish)

        setupViewPager()

        fabStart.setOnClickListener {
            routePointsViewModel.startRoute()
        }
        fabContinue.setOnClickListener {
            routePointsViewModel.startNextRoute()
        }
        fabFinish.setOnClickListener {
            routePointsViewModel.finishDetour(DetourStatus.COMPLETED.id)
        }

        routePointsViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })
        techOperationsViewModel.completeTechMapEvent.observe(viewLifecycleOwner, EventObserver {
            routePointsViewModel.completeTechMap(it)
        })
        routePointsViewModel.routeStatus.observe(viewLifecycleOwner, EventObserver { status ->
            stateFabs.map { it.isInvisible = true }
            when (status) {
                RoutePointsViewModel.RouteStatus.NOT_STARTED -> fabStart.isInvisible = false
                RoutePointsViewModel.RouteStatus.IN_PROGRESS -> fabContinue.isInvisible = false
                RoutePointsViewModel.RouteStatus.COMPLETED -> fabFinish.isInvisible = false
            }
        })
        routePointsViewModel.navigateToBack.observe(viewLifecycleOwner, EventObserver {
            detoursViewModel.getDetours()
            requireActivity().finish()
        })
        routePointsViewModel.navigateToCloseDialog.observe(viewLifecycleOwner, EventObserver {
            openCloseDialog()
        })
    }

    private fun setupToolbar(title: String?) {
        toolbarLayout.tvTitle.text = title.orEmpty()
        toolbarLayout.btnClose.setOnClickListener {
            routePointsViewModel.closeClick()
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

    private fun openCloseDialog() {
        findNavController().navigate(R.id.action_routePointsFragment_to_routePointsCloseDialog)
    }
}
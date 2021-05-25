package ru.madbrains.inspection.ui.main.routes.points

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_route_points.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.domain.model.DetourStatusType
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.main.routes.points.list.RoutePointsListFragment
import ru.madbrains.inspection.ui.main.routes.points.map.RoutePointsMapFragment
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsFragment
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsViewModel
import timber.log.Timber

class RoutePointsFragment : BaseFragment(R.layout.fragment_route_points) {

    companion object {
        const val KEY_DETOUR = "detour"
    }

    private lateinit var routePointsAdapter: RoutePointsAdapter

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val techOperationsViewModel: TechOperationsViewModel by sharedViewModel()

    private val stateFabs = mutableListOf<ExtendedFloatingActionButton>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireNotNull(arguments).run {
            val detour = getSerializable(KEY_DETOUR) as? DetourModel
            detour?.let {
                setupToolbar(it.name)
                routePointsViewModel.setDetour(it)
            }
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
            Timber.d("debug_dmm 1")
        }
        fabFinish.setOnClickListener {
            routePointsViewModel.finishDetourAndSave(DetourStatusType.COMPLETED)
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
            findNavController().popBackStack()
        })
        routePointsViewModel.navigateToCloseDialog.observe(viewLifecycleOwner, EventObserver {
            openCloseDialog()
        })
        routePointsViewModel.navigateToFinishDialog.observe(viewLifecycleOwner, EventObserver {
            openFinishDialog()
        })

        routePointsViewModel.navigateToNextRoute.observe(
            viewLifecycleOwner,
            EventObserver { routeData ->
                val techMap = routeData.techMap
                techMap?.let {
                    openTechOperationsFragment(routeData)
                }
            })

        routePointsViewModel.navigateToTechOperations.observe(
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

    private fun openFinishDialog() {
        findNavController().navigate(R.id.action_routePointsFragment_to_routePointsFinishDialog)
    }

    private fun openCloseDialog() {
        findNavController().navigate(R.id.action_routePointsFragment_to_routePointsCloseDialog)
    }
}
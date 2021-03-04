package ru.madbrains.inspection.ui.main.routes.routelist

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pow.api.cls.RfidPower
import com.uhf.api.cls.Reader
import kotlinx.android.synthetic.main.fragment_route_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.DetourAdapter
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment
import timber.log.Timber

class RouteListFragment : BaseFragment(R.layout.fragment_route_list) {

    private val routeListViewModel: RouteListViewModel by viewModel()
    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private val routesAdapter by lazy {
        DetourAdapter(
            onDetourClick = {
                val detour = detoursViewModel.detourModels.find { detourModel ->
                    detourModel.id == it.id
                }
                routeListViewModel.routeClick(detour)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val rPower = RfidPower(RfidPower.PDATYPE.ZoomSmart)

        val reader = Reader()

        reader.InitReader_Notype("/dev/ttyHSL3", 1)

        val ants: IntArray = intArrayOf()
        val tagcnt = IntArray(1)

        var er: Reader.READER_ERR = reader.TagInventory_Raw(ants, ants.size, 50, tagcnt)

        if (er == Reader.READER_ERR.MT_OK_ERR) {
            val tagInfo: Reader.TAGINFO = reader.TAGINFO()
            er = reader.GetNextTag(tagInfo)
            if (er === Reader.READER_ERR.MT_OK_ERR) {
                Timber.tag("RFID").d("info=%s", Reader.bytes_Hexstr(tagInfo.EpcId))
            }
        }

        startScan.setOnClickListener {
            rPower.PowerUp()
        }

        stopScan.setOnClickListener {
            rPower.PowerDown()
        }

        rvRoutes.adapter = routesAdapter

        btnGetData.setOnClickListener {
            detoursViewModel.getDetours()
        }

        detoursViewModel.detours.observe(viewLifecycleOwner, Observer {
            routesAdapter.items = it
            ivGetData.isVisible = false
            btnGetData.isVisible = false
        })
        routeListViewModel.navigateToRoutePoints.observe(viewLifecycleOwner, EventObserver {
            openRoutePointsFragment(it)
        })
    }

    private fun openRoutePointsFragment(route: DetourModel) {
        val args = bundleOf(
            RoutePointsFragment.KEY_DETOUR to route
        )
        findNavController().navigate(R.id.action_routesFragment_to_routePointsFragment, args)
    }
}
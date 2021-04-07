package ru.madbrains.inspection.ui.main.routes.routelist

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pow.api.cls.RfidPower
import com.uhf.api.cls.Reader
import com.uhf.api.cls.Reader.READER_ERR
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
import java.util.concurrent.Executors


class RouteListFragment : BaseFragment(R.layout.fragment_route_list) {

    private val routeListViewModel: RouteListViewModel by viewModel()
    private val detoursViewModel: DetoursViewModel by sharedViewModel()
    private val handler = Handler()
    private var scanIsOn = false
    private val rPower = RfidPower(RfidPower.PDATYPE.ZoomSmart)
    private val reader = Reader()

    private lateinit var runnable: Runnable

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

    @SuppressLint("LogNotTimber")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        runnable = Runnable {
            Timber.d("RFID ${rPower.GetDevPath()}")

            reader.InitReader_Notype("/dev/ttyHSL3", 1)

            val ants = IntArray(1)
            ants[0] = 1
            val tagcnt = IntArray(1)
            tagcnt[0] = 0

            var er = reader.TagInventory_Raw(ants, ants.size, 500, tagcnt)

            if (er == READER_ERR.MT_OK_ERR) {
                val tagInfo = reader.TAGINFO()
                er = reader.GetNextTag(tagInfo)
                if (er == READER_ERR.MT_OK_ERR) {
                    Timber.d("RFID ...info= ${Reader.bytes_Hexstr(tagInfo.EpcId)}")
                }
            }
            if(scanIsOn) {
                handler.postDelayed(this.runnable, 0)
            }
        }

        startScan.setOnClickListener {
            if(!scanIsOn){
                scanIsOn = true
                rPower.PowerDown()
                rPower.PowerUp()
                Executors.newSingleThreadExecutor().execute {
                    handler.post(this.runnable)
                }
            }
        }

        stopScan.setOnClickListener {
            scanIsOn = false
            rPower.PowerDown()
            handler.removeCallbacks(runnable)
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
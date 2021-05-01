package ru.madbrains.inspection.ui.main.routes.points.map.mapLevelsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.maps_level_list_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.ui.adapters.MapLevelsAdapter
import ru.madbrains.inspection.ui.main.routes.points.map.RoutePointsMapViewModel

class MapsLevelListFragment : DialogFragment() {
    private val routePointsMapViewModel: RoutePointsMapViewModel by sharedViewModel()

    private val mapLevelsAdapter by lazy {
        MapLevelsAdapter(
            onItemClick = {
                routePointsMapViewModel.setActiveMap(it)
                findNavController().popBackStack()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maps_level_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvMaps.adapter = mapLevelsAdapter

        routePointsMapViewModel.mapLevels.observe(viewLifecycleOwner) {
            mapLevelsAdapter.items = it
        }
    }

}
package ru.madbrains.inspection.ui.main.routes.points

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_route_points_close_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.DetourStatusType
import ru.madbrains.inspection.R

class RoutePointsCloseDialog : DialogFragment() {

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_route_points_close_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnComplete.setOnClickListener {
            findNavController().popBackStack()
            routePointsViewModel.finishDetourAndSave(DetourStatusType.COMPLETED_AHEAD)
        }
        btnSuspend.setOnClickListener {
            findNavController().popBackStack()
            routePointsViewModel.finishDetourAndSave(DetourStatusType.PAUSED)
        }
        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
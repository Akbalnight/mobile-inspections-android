package ru.madbrains.inspection.ui.main.routes.techoperations

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_tech_operations.*
import kotlinx.android.synthetic.main.fragment_tech_operations.progressView
import kotlinx.android.synthetic.main.fragment_tech_operations.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_close.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.R

import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.TechOperationAdapter
import ru.madbrains.inspection.ui.main.equipment.EquipmentFragment


class TechOperationsFragment : BaseFragment(R.layout.fragment_tech_operations) {

    companion object {
        const val KEY_ROUTE_DATA = "KEY_ROUTE_DATA"
    }

    private val techOperationsViewModel: TechOperationsViewModel by sharedViewModel()

    private val techOperationsAdapter by lazy {
        TechOperationAdapter(
            onDataInput = {
                techOperationsViewModel.onTechDataInput(it.id, it.inputData)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        techOperationsViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        requireNotNull(arguments).run {
            val routeDataModel = (getSerializable(KEY_ROUTE_DATA) as? RouteDataModel)
            routeDataModel?.let {
                techOperationsViewModel.setRouteData(it)
                setupToolbar(it.techMap?.pointNumber)
            }
        }

        fabTechOperationsSave.setOnClickListener {
            techOperationsViewModel.finishTechMap()
            findNavController().popBackStack()
        }

        rvTechOperations.adapter = techOperationsAdapter

        techOperationsViewModel.titleTechOperations.observe(viewLifecycleOwner, Observer {
            tvTitleTechOperations.text = it
        })

        techOperationsViewModel.techOperations.observe(viewLifecycleOwner, Observer {
            techOperationsAdapter.items = it
        })

        setupOnClickListener()

        setupNavigation()

    }

    private fun setupToolbar(positionPoint: Int?) {

        toolbarLayout.apply {
            var toolBarTitle = strings[R.string.fragment_tech_operations_app_bar]
            positionPoint?.let {
                toolBarTitle += it.toString()
            }
            tvTitle.text = toolBarTitle
            toolbarLayout.btnLeading.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupOnClickListener() {

        layoutBottomButtonAddDefect.setOnClickListener {
            techOperationsViewModel.toAddDefect()
        }

        layoutBottomButtonDevice.setOnClickListener {
            techOperationsViewModel.toEquipmentFragment()
        }
    }

    private fun setupNavigation() {

        techOperationsViewModel.navigateToAddDefect.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_techOperationsFragment_to_addDefectFragment)
        })

        techOperationsViewModel.navigateToEquipment.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_techOperationsFragment_to_equipmentFragment, bundleOf(
                EquipmentFragment.KEY_EQUIPMENT_DATA to it
            ))
        })

    }
}